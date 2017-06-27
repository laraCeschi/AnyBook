
package it.unitn.disi.anybook.activities.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import  android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import it.unitn.disi.anybook.R;
import it.unitn.disi.anybook.activities.BookActivity;
import it.unitn.disi.anybook.data.Book;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * Questa classe si occupa della disposizione dei dati in item posti in una recycleview
 * @see it.unitn.disi.anybook.activities.fragments.HomeFragment
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private ArrayList<Book> mDataSet;

    /**
     * Questa classe descrive gli oggetti presenti nella RecycleView e la loro posizione
     * @see android.support.v7.widget.RecyclerView.ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCover;
        private final ProgressBar progressBar;
        private Book book;

        /**
         * Questo metodo dispone i dati all'interno di una View
         * @param v la View in cui disporre il dataset
         */
        ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, BookActivity.class);
                    intent.putExtra("bookInJson", new Gson().toJson( book));
                    context.startActivity(intent);
                }
            });
            ivCover = (ImageView) v.findViewById(R.id.cover_suggestion);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar_book_suggestion);
        }

        ImageView getIvCover() {
            return ivCover;
        }
        ProgressBar getProgressBar () {return progressBar;}
        void setBook(Book book) { this.book = book; }
    }

    /**
     * Inizializza il dataset dell'adapter
     * @param dataSet ArrayList<Book> contenente i libri da impostare come suggeriti nella recyclerview
     */
    public HomeAdapter(ArrayList<Book> dataSet) {
        mDataSet = dataSet;
    }

    /**
     *  Questo metodo crea nuovi elementi nella RecycleView
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.book_suggestion_item, viewGroup, false);

        return new ViewHolder(v);
    }

    /**
     * Questo metodo posiziona gli elementi del dataset all'interno della View
     * @param viewHolder la View in cui disporre gli elementi
     * @param position la posizione dell'elemento da inserire
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Book currentBook = mDataSet.get(position);

        viewHolder.setBook(currentBook);

        if(currentBook.getImage_thumb() != null) {
            if (!currentBook.getImage_thumb().equals("")) {
                LoadImage myTask = new LoadImage(viewHolder.getIvCover(), viewHolder.getProgressBar());
                myTask.execute(currentBook.getImage_thumb());
            }
        }
    }

    /**
     * Questo metodo conta gli elementi del dataset
     * @return la dimensione del dataset
     */
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Questo async task carica in maniera asincrona le copertine dei libri suggeriti
     */
    class LoadImage extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;
        private ProgressBar progressBar;

        public LoadImage(ImageView imageView, ProgressBar progressBar){
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
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                InputStream inputStream= connection.getInputStream();
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