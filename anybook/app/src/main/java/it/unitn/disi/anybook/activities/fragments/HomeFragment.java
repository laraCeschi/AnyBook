package it.unitn.disi.anybook.activities.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.unitn.disi.anybook.APIHandler.APIgoogleBooks;
import it.unitn.disi.anybook.APIHandler.NetworkUtils;
import it.unitn.disi.anybook.R;
import it.unitn.disi.anybook.activities.adapters.HomeAdapter;
import it.unitn.disi.anybook.data.Author;
import it.unitn.disi.anybook.data.Book;
import it.unitn.disi.anybook.databaseUtil.DbHelper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static it.unitn.disi.anybook.data.StaticStrings.WISHLIST;
import static it.unitn.disi.anybook.dataHandler.BookHandler.existBook;
import static it.unitn.disi.anybook.dataHandler.BookHandler.setAuthor;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.getBookByLibray;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.getLibraryByName;

/**
 * Questa classe rappresenta il Fragment visualizzato nella tab Home.
 *
 * @see Fragment
 */
public class HomeFragment extends Fragment {


    protected ArrayList<Author> listaAutori;
    protected ArrayList<Book> mDataSet;
    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView mRecyclerView;
    protected HomeAdapter mAdapter;
    protected TextView mTextView;
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }


    /**
     * Il costruttore pubblico vuoto è necessario: "All subclasses of Fragment must include a public
     * no-argument constructor. The framework will often re-instantiate a fragment class when needed,
     * in particular during state restore, and needs to be able to find this constructor to
     * instantiate it. If the no-argument constructor is not available, a runtime exception will
     * occur in some cases during state restore." [https://developer.android.com/reference/android/app/Fragment.html]
     */
    public HomeFragment() {
    }

    /**
     * Questo metodo crea il fragment
     *
     * @param savedInstanceState contiene i dati più recenti forniti a onSaveInstanceState().
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Questo metodo istanzia la grafica del Fragment.
     *
     * @param inflater           il LayoutInflater che viene utilizzato per "gonfiare" una view in un Fragment
     * @param container          la view gerarchicamente superiore in cui va inserito il Fragment
     * @param savedInstanceState l'eventuale stato precedente del Fragment
     * @return la View della grafica del Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.home_fragment, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.book_suggestion_recyclerView);
        mTextView = (TextView) rootView.findViewById(R.id.no_suggestion);
        if (NetworkUtils.checkConnection(getActivity())) {
            new LoadSuggest(mDataSet, savedInstanceState, mTextView).execute();
        } else {
            mTextView.setText("connessione internet assente: impossibile caricare suggerimenti");
            mTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
        return rootView;
    }

    /**
     * Questo metodo implementa un ordinamento totale tra due Book
     */
    private class mySort implements Comparator<Book> {
        @Override
        public int compare(Book o1, Book o2) {
            if (o1.getID() > o2.getID()) {
                return -1;
            }
            if (o1.getID() < o2.getID()) {
                return 1;
            }
            return 0;
        }
    }

    /**
     * Questo metodo inizializza la RecyclerView che contiene i libri suggeriti
     * @param savedInstanceState lo stato precedentemente salvato
     */
    public void initRecyclerView(Bundle savedInstanceState) {
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new HomeAdapter(mDataSet);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Questo metodo imposta il LayoutManager della RecycleView
     *
     * @param layoutManagerType Tipo di Layoutmanager che sostiusce il corrente LayoutManager
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // Se un LayoutManager è già presente, ottieni l'attuale posizione di scroll
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), 2);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    /**
     * Questo metodo salva lo stato corrente.
     *
     * @param savedInstanceState il Bundle in cui salvare lo stato corrente.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Questo metodo performa le azioni da compiere quando viene ripresa l'attività
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
      * Questo metodo genera il dataset dei libri suggeriti
     */
    private void initDataset() {

        DbHelper helper = new DbHelper(getActivity());
        ArrayList<Book> listaLibri = getBookByLibray(helper, getLibraryByName(helper, WISHLIST));

        if (listaLibri != null) {
            for (int i = 0; i < listaLibri.size(); i++) {
                listaLibri.get(i).setAuthors(setAuthor(helper, listaLibri.get(i)));
            }
            Collections.sort(listaLibri, new mySort());
            listaAutori = new ArrayList<>();
            mDataSet = new ArrayList<>();

            for (int i = 0; i < listaLibri.size(); i++) {
                for(int j = 0; j < listaLibri.get(i).getAuthors().size(); j++){
                    listaAutori.add(listaLibri.get(i).getAuthors().get(j));
                }
            }
        }
        if (listaAutori != null && listaAutori.size() > 0) {
            int numeroLibri = 1;
            int numeroIterazioni = 0;
            ArrayList<ArrayList<Book>> listaPerAutori = new ArrayList<>();
            for(int i = 0; i < listaAutori.size(); i++){
                APIgoogleBooks gb = new APIgoogleBooks();
                ArrayList<Book> listaLibriDaAutore; //= null; = gb.makeBookListSearchQuery(listaAutori.get(i).getName());
                String authorToken = listaAutori.get(i).getName().replace(" ", "%20");
                try {
                    URL booklistSearchURL = new URL("https://www.googleapis.com/books/v1/volumes?q=inauthor:" + authorToken);
                    String bookSearchResult = null;
                    try {
                        bookSearchResult = NetworkUtils.getResponseFromHttpUrl(booklistSearchURL);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    listaLibriDaAutore = gb.parseMaxNJSON(bookSearchResult, 5);
                    listaPerAutori.add(listaLibriDaAutore);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(i == 5){
                    //limita gli autori ad al massimo 5
                    i = listaAutori.size();
                }
            }
            int max_libri = 4;
            while (numeroLibri <= max_libri) {
                for (int i = 0; i < listaPerAutori.size(); i++) {
                    if (numeroLibri <= max_libri) {
                        Book libro;
                        if (listaPerAutori.get(i) != null) {
                            double ran = Math.random();
                            int posizione = (int) (ran * listaPerAutori.get(i).size());
                            libro = listaPerAutori.get(i).get(posizione);
                            if (libro != null) {
                                boolean esistenteInLista = false;
                                for (int j = 0; j < mDataSet.size(); j++) {
                                    if (mDataSet.get(j).getISBN() == libro.getISBN()) {
                                        esistenteInLista = true;
                                    }
                                }
                                if (libro.getISBN() > 0 && !esistenteInLista && !existBook(helper, libro)) {
                                    mDataSet.add(libro);
                                    numeroLibri++;
                                }
                            }
                        }
                    } else {
                        i = listaPerAutori.size();
                    }
                }
                numeroIterazioni++;
                if(numeroIterazioni > 5){
                    numeroLibri = max_libri + 1;
                }
            }

        }
        helper.close();
    }

    /**
     * Questo async task inizializza il dataset e carica le immagini di copertina dei suggerimenti
     */
    private class LoadSuggest extends AsyncTask<Void, Void, Void>{
        ArrayList<Book> lista;
        Bundle bundle;
        TextView textView;

        public LoadSuggest(ArrayList<Book> lista, Bundle bundle, TextView textView){
            this.lista = lista;
            this.bundle = bundle;
            this.textView = textView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            initDataset();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //initDataset();
            if (mDataSet != null && mDataSet.size() > 0) {
                initRecyclerView(bundle);
                textView.setVisibility(View.GONE);
            } else {
                 textView.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(aVoid);
        }
    }

}