package it.unitn.disi.anybook.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import it.unitn.disi.anybook.APIHandler.APIGoodreads;
import it.unitn.disi.anybook.APIHandler.APIgoogleBooks;
import it.unitn.disi.anybook.APIHandler.NetworkUtils;
import it.unitn.disi.anybook.R;
import it.unitn.disi.anybook.data.Author;
import it.unitn.disi.anybook.data.Book;
import it.unitn.disi.anybook.data.Category;
import it.unitn.disi.anybook.data.Reviews;
import it.unitn.disi.anybook.databaseUtil.DbHelper;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static it.unitn.disi.anybook.activities.fragments.LibrarySelectionFragment.newInstanceLibrarySelectionFragment;
import static it.unitn.disi.anybook.activities.fragments.ReviewFragment.newInstanceReviewFragment;
import static it.unitn.disi.anybook.dataHandler.BookHandler.existBook;
import static it.unitn.disi.anybook.dataHandler.BookHandler.getBookByISBN;
import static it.unitn.disi.anybook.dataHandler.BookHandler.setAuthor;
import static it.unitn.disi.anybook.dataHandler.BookHandler.setCategories;

/**
 * Questa classe rappresenta l'activity per mostrare i dettagli di un libro, le sue recensioni e le sue librerie
 */
public class BookActivity extends AppCompatActivity {

    private Book book;
    private Reviews reviews;
    ProgressBar progressBar;
    ImageView imageView;

    /**
     * Questo metodo crea l'attività attuale.
     * Imposta la View che compone la schermata principale, la toolbar, il contenitore per le tab.
     * Il libro passato contestualmente alla chiamata dell'activity viene mostrato a schermo.
     *
     * @param savedInstanceState contiene i dati più recenti forniti a onSaveInstanceState().
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_book_activity);
        imageView = (ImageView) findViewById(R.id.book_cover);
        String jsonBook = getIntent().getStringExtra("book");
        long isbn;
        isbn = getIntent().getLongExtra("isbn", -1);
        String bookInJson = getIntent().getStringExtra("bookInJson");
        DbHelper helper = new DbHelper(this);

        if (bookInJson != null) {
            book = new Gson().fromJson(bookInJson, Book.class);
            if (book != null) {
                if (existBook(helper, book.getISBN())) {
                    book.setID(getBookByISBN(helper, book.getISBN()).getID());
                }
            } else {
                setResult(CommonStatusCodes.ERROR);
                finish();
            }
        } else if (jsonBook != null && isbn != -1) {
            APIgoogleBooks api = new APIgoogleBooks();
            try {
                book = api.parseJSON(jsonBook);
                if (book != null) {
                    if (book.getISBN() == isbn) {
                        if (existBook(helper, book.getISBN())) {
                            Book inDatabase = getBookByISBN(helper, book.getISBN());
                            book.setID(inDatabase.getID());
                            book.setRatingcount(inDatabase.getRatingcount());
                            book.setRating(inDatabase.getRating());
                            book.setiFrame(inDatabase.getiFrame());
                        }
                    } else {
                        setResult(CommonStatusCodes.ERROR);
                        finish();
                    }
                } else {
                    setResult(CommonStatusCodes.ERROR);
                    finish();
                }
            } catch (JSONException jsExcp) {
                setResult(CommonStatusCodes.ERROR);
                finish();
            }
        } else {
            if (isbn > -1) {
                book = getBookByISBN(helper, isbn);
                if (book != null) {
                    book.setAuthors(setAuthor(helper, book));
                    book.setCategories(setCategories(helper, book));
                } else {
                    setResult(CommonStatusCodes.ERROR);
                    finish();
                }
            } else {
                setResult(CommonStatusCodes.ERROR);
                finish();
            }
        }


        if (book != null) {
            if(!existBook(helper, book)) {
                APIGoodreads apiGoodreads = new APIGoodreads();
                reviews = apiGoodreads.makeReviewSearchQuery(String.valueOf(book.getISBN()));
                System.out.println(reviews);
                if (reviews != null) {
                    book.setRating((int) reviews.getAvgRating());
                    book.setRatingcount(reviews.getRatingCount());
                    book.setiFrame(reviews.getHTMLiFrame());
                } else {
                    if (book.getAuthors()!=null && book.getAuthors().size() >0) {
                        reviews = apiGoodreads.makeReviewSearchQuery(book.getTitle(), book.getAuthors().get(0).getName());
                    } else {
                        reviews = apiGoodreads.makeReviewSearchQuery(book.getTitle(), "");
                    }
                    if (reviews!=null) {
                        book.setRating((int) reviews.getAvgRating());
                        book.setRatingcount(reviews.getRatingCount());
                        book.setiFrame(reviews.getHTMLiFrame());
                    }
                }
                //System.out.println(reviews);
            }
            insertBook(book);
            ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
            setUpViewPager(mViewPager);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
        }
        else{
            setResult(CommonStatusCodes.ERROR);
            finish();
        }
        helper.close();
    }

    /**
     * Questo parametro imposta il ViewPagerAdapter per disporre tante tab quante sono necessarie
     *
     * @param viewPager il "contenitore" delle tab
     * @see ViewPagerAdapter
     */
    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(newInstanceReviewFragment(book.getiFrame()), "RECENSIONI");
        String bookInJson = new Gson().toJson(book);
        adapter.addFrag(newInstanceLibrarySelectionFragment(bookInJson), "LIBRERIE");
        viewPager.setAdapter(adapter);
    }


    /**
     * Questa classe gestisce i vari Fragment attualmente necessari
     *
     * @see FragmentPagerAdapter
     */
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        /**
         * Questo metodo restituisce un Fragment data una posizione del PagerAdapter
         *
         * @param position l'indice del Fragment da restituire
         * @return il Fragment nella posizione indicata
         */
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        /**
         * Questo metodo conta i Fragment presenti nel ViewPagerAdapter
         *
         * @return il numero di Fragment presenti nel ViewPagerAdapter
         */
        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        /**
         * Questo metodo aggiunge un  Fragment al ViewPagerAdapter
         *
         * @param fragment il Fragment da aggiungere
         * @param title    il titolo del Fragment indicato
         */
        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        /**
         * Questo metodo restituisce il titolo di un Fragment dato l'indice
         *
         * @param position l'indice del Fragment di cui si desidera ottenere il titolo
         * @return il titolo del Fragment nella posizione indicata
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /**
     * Questo metodo inserisce un libro nella View
     *
     * @param book il libro da visualizzare a schermo
     */
    public void insertBook(Book book) {
        if (book != null) {
            String currentText;
            float currentFloat;
            int currentInt;

            boolean imageNotFound = book.getImage_thumb() == null || book.getImage_thumb().equals("");

            // aggiunge la copertina del libro
            if (!imageNotFound && NetworkUtils.checkConnection(this)) {
                new LoadImage().execute(book.getImage_thumb());
                System.out.println(book.getImage_thumb());
            }

            // aggiunge gli autori
            String stringOfAuthors = "";
            ArrayList<Author> authors = book.getAuthors();
            if (authors != null) {
                int size = authors.size();
                for (int i = 0; i < size; i++) {
                    stringOfAuthors = stringOfAuthors.concat(authors.get(i).getName());
                    if (i < size - 1) {
                        stringOfAuthors = stringOfAuthors.concat(", ");
                    }
                }
                TextView mAuthor = (TextView) findViewById(R.id.author);
                String text;
                if (!stringOfAuthors.equals("")) {
                    if (size > 1) {
                        text = "Autori: " + stringOfAuthors;
                    } else {
                        text = "Autore: " + stringOfAuthors;
                    }
                    mAuthor.setText(text);
                }
            }

            // aggiunge l'editore
            TextView mPublisher = (TextView) findViewById(R.id.publisher);
            currentText = book.getPublisher();
            if (currentText != null) {
                mPublisher.setText(currentText);
            } else {
                mPublisher.setVisibility(GONE);
            }

            // aggiunge il numero di recensioni
            TextView mRatingCount = (TextView) findViewById(R.id.book_rating_count);
            String reviewNumber;
            currentInt = book.getRatingcount();
            if (currentInt != -1) {
                reviewNumber = String.valueOf(currentInt + " recensioni");
                mRatingCount.setText(reviewNumber);
            }

            // aggiunge il voto
            RatingBar mRating = (RatingBar) findViewById(R.id.book_rating);
            currentFloat = book.getRating();
            if (currentFloat != -1) {
                mRating.setRating(currentFloat);
            }

            // aggiunge i generi
            TextView mCategories = (TextView) findViewById(R.id.categories);
            String stringOfGenres = "";
            ArrayList<Category> categories = book.getCategories();
            if (categories != null) {
                int size = categories.size();
                for (int i = 0; i < size; i++) {
                    stringOfGenres = stringOfGenres.concat(categories.get(i).getName());
                    if (i < size - 1) {
                        stringOfGenres = stringOfGenres.concat(", ");
                    }
                }
                String text;
                if (!stringOfGenres.equals("")) {
                    if (size > 1) {
                        text = "Generi: " + stringOfGenres;
                    } else {
                        text = "Genere: " + stringOfGenres;
                    }
                    mCategories.setText(text);
                }
            } else {
                mCategories.setVisibility(GONE);
            }

            // aggiunge la descrizione
            TextView mDescription = (TextView) findViewById(R.id.description);
            String descipt = book.getDescription();
            if (descipt != null) {
                mDescription.setText(descipt);
            }

            // aggiunge il link al play store
            final String link = book.getLink_for_sale();
            if (book.isSaleability() && link != null) {
                TextView mBuy = (TextView) findViewById(R.id.buyIt);
                String buyItNow = "Compralo ora!";
                mBuy.setText(buyItNow);
                mBuy.setVisibility(VISIBLE);
                mBuy.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                                } catch (android.content.ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
            }

            // aggiunge il titolo
            TextView toolbar = (TextView) findViewById(R.id.titlebar);
            currentText = book.getTitle();
            if (currentText != null) {
                toolbar.setText(book.getTitle());
            }


            Button shareButton = (Button) findViewById(R.id.shareIt);
            currentText = book.getLink_for_share();
            if (currentText!= null) {
                shareButton.setOnClickListener(Share(currentText));
            }
        }
    }

    /**
     * Questo metodo costruisce un listener che avvia un selettore per l'applicazione su cui
     * condividere il link indicato come parametro
     * @param link il messaggio da condividere
     * @return il listener
     */
    private View.OnClickListener Share(String link) {
        final String message = link;
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, "Condividi su:"));
            }
        };
    }

    /**
     * Questo AsyncTask carica l'immagine di copertina di un libro in maniera asincrona
     */
    private class LoadImage extends AsyncTask<String, Void, Bitmap> {

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
