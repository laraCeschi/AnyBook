
package it.unitn.disi.anybook.activities.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import it.unitn.disi.anybook.APIHandler.NetworkUtils;
import it.unitn.disi.anybook.R;
import it.unitn.disi.anybook.activities.BookActivity;
import it.unitn.disi.anybook.data.Author;
import it.unitn.disi.anybook.data.Book;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * Questa classe si occupa della disposizione dei dati in item posti in una recycleview
 *
 * @see it.unitn.disi.anybook.activities.fragments.WishlistFragment
 * @see it.unitn.disi.anybook.activities.fragments.LibraryFragment
 */
public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder>
        implements Filterable {

    private ArrayList<Book> mDataSet;
    private ArrayList<Book> mOriginalDataset;
    public Context context;


    /**
     * Questo metodo inizializza il dataset     *
     * @param dataSet ArrayList<Book> contenenti i dati da inserire nella RecyclerView
     */
    public BookListAdapter(ArrayList<Book> dataSet) {
        mDataSet = dataSet;
        mOriginalDataset = dataSet;
    }

    /**
     * Questa classe descrive gli oggetti presenti nella RecycleView e la loro posizione
     *
     * @see android.support.v7.widget.RecyclerView.ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final ImageView ivCover;
        private final TextView tvAuthor;
        private final RatingBar rbRating;
        private final ProgressBar progressBar;
        private long isbn;
        /**
         * Questo metodo dispone i dati all'interno di una View
         *
         * @param v la View in cui disporre il dataset
         */
        ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = tvTitle.getContext();
                    Intent intent = new Intent(context, BookActivity.class);
                    intent.putExtra("isbn", isbn);
                    context.startActivity(intent);
                }
            });
            tvTitle = (TextView) v.findViewById(R.id.title);
            ivCover = (ImageView) v.findViewById(R.id.cover);
            tvAuthor = (TextView) v.findViewById(R.id.author);
            rbRating = (RatingBar) v.findViewById(R.id.ratingBar);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar_book_item);
        }

        TextView getTvTitle() {
            return tvTitle;
        }

        TextView getTvAuthor() {
            return tvAuthor;
        }

        RatingBar getRbRating() {
            return rbRating;
        }

        ImageView getIvCover() {
            return ivCover;
        }

        ProgressBar getProgressBar() {
            return progressBar;
        }

        void setIsbn(long isbn) {
            this.isbn = isbn;
        }
    }


    /**
     * Questo metodo crea nuovi elementi nella RecycleView
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.book_preview_item, viewGroup, false);

        return new ViewHolder(v);
    }

    /**
     * Questo metodo posiziona gli elementi del dataset all'interno della View
     *
     * @param viewHolder la View in cui disporre gli elementi
     * @param position   la posizione dell'elemento da inserire
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Book currentBook = mDataSet.get(position);
        viewHolder.getTvTitle().setText(currentBook.getTitle());
        ArrayList<Author> authors = currentBook.getAuthors();
        String authorsString = "";

        if (authors != null) {
            for (int i = 0; i < authors.size(); i++) {
                if (i == 0) {
                    authorsString = authors.get(i).getName();
                } else {
                    authorsString = authorsString.concat(", ").concat(authors.get(i).getName());
                }
            }
            viewHolder.getTvAuthor().setText(authorsString);
        }
        if (currentBook.getRating() > 0) {
            viewHolder.getRbRating().setRating(currentBook.getRating());
        }

        viewHolder.setIsbn(currentBook.getISBN());

        if (currentBook.getImage_thumb() != null && NetworkUtils.checkConnection(context)) {
            if (!currentBook.getImage_thumb().equals("")) {
                LoadImage myTask = new LoadImage(viewHolder.getIvCover(), viewHolder.getProgressBar());
                myTask.execute(currentBook.getImage_thumb());
            }
        }
    }

    /**
     * Questo metodo conta gli elementi del dataset
     *
     * @return la dimensione del dataset
     */
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Questo metodo rimuove un oggetto data la sua posizione
     * @param position l'indice dell'elemento da rimuovere
     */
    public void removeItem(int position) {
        mDataSet.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataSet.size());
    }


    /**
     * Questo metodo implementa il filtro da utilizzare per la ricerca all'interno di una libreria
     * @return il filtro
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                try {
                    mDataSet = (ArrayList<Book>) results.values;
                } catch (ClassCastException c) {
                    mDataSet = mOriginalDataset;
                }
                BookListAdapter.this.notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Book> filteredResults;
                if (constraint.length() == 0) {
                    filteredResults = mOriginalDataset;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }


    /**
     * Questo metodo ritorna i risultati filtrati dalla parola chiave passata per parametro
     * @param constraint la parola chiave con cui filtrare i risultati
     * @return i risultati filtrati
     */
    protected ArrayList<Book> getFilteredResults(String constraint) {
        ArrayList<Book> results = new ArrayList<>();
        boolean isInAuthor;
        boolean isInTitle;
        ArrayList<Author> authors;

        for (Book item : mOriginalDataset) {
            isInAuthor = false;
            isInTitle = item.getTitle().toLowerCase().contains(constraint);
            authors = item.getAuthors();
            if (authors != null) {
                for (Author author : authors) {
                    isInAuthor = isInAuthor || author.getName().toLowerCase().contains(constraint);
                }
            }
            if (isInAuthor || isInTitle) {
                results.add(item);
            }
        }
        return results;
    }

    /**
     * Questo async task carica le copertine dei libri della libreria in maniera asincrona
     */
    class LoadImage extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;
        private ProgressBar progressBar;

        public LoadImage(ImageView imageView, ProgressBar progressBar) {
            this.imageView = imageView;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... voids) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(voids[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressBar.setVisibility(GONE);
            imageView.setImageBitmap(bitmap);
        }
    }
}