package it.unitn.disi.anybook.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import it.unitn.disi.anybook.R;
import it.unitn.disi.anybook.activities.adapters.BookListAdapter;
import it.unitn.disi.anybook.data.Book;
import it.unitn.disi.anybook.data.Library;
import it.unitn.disi.anybook.databaseUtil.DbHelper;
import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.ArrayList;

import static it.unitn.disi.anybook.dataHandler.BookHandler.deleteBook;
import static it.unitn.disi.anybook.dataHandler.BookHandler.setAuthor;
import static it.unitn.disi.anybook.dataHandler.BookHandler.setCategories;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.allBookLibraryRelation;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.deleteBookLibraryRelation;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.getBookByLibray;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.getLibraryByName;

/**
 * Questa classe rappresenta l'activity che gestisce la lista delle librerie
 */
public class LibraryActivity
        extends AppCompatActivity {

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected BookListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Book> mDataset;
    private View view;
    private AlertDialog.Builder alertDialog;
    private int posizione_da_eliminare = -1;
    private String name;

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


        setContentView(R.layout.library_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_library);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.library_recyclerView);

        mLayoutManager = new LinearLayoutManager(this);

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);


        String libraryName = getIntent().getStringExtra("name");
        name = libraryName;
        if (libraryName != null) {
            initDataset();
        }
        else {
            setResult(CommonStatusCodes.ERROR);
            finish();
        }

        TextView title = (TextView) findViewById(R.id.title_library);

        title.setText(libraryName);


        if (mDataset!= null) {
            System.out.println(mDataset.size());
            if (mDataset.size() > 0) {
                mAdapter = new BookListAdapter(mDataset);
                mRecyclerView.setAdapter(mAdapter);
                initDialog();
                initSwipe();
            } else {
                TextView emptyLibrary = (TextView) findViewById(R.id.empty_library);
                emptyLibrary.setVisibility(View.VISIBLE);
                mRecyclerView.setAdapter(null);
                mRecyclerView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Questo metodo performa le azioni quando viene ripresa l'activity, ridisponendo i dati
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        String libraryName = getIntent().getStringExtra("name");

        if (libraryName != null) {
            initDataset();
        }
        if (mDataset!= null && mDataset.size()>0){
            mAdapter = new BookListAdapter(mDataset);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            mRecyclerView.setAdapter(null);
            mRecyclerView.setVisibility(View.GONE);
            TextView emptyLibrary =(TextView) findViewById(R.id.empty_library);
            emptyLibrary.setVisibility(View.VISIBLE);
        }
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
                mLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(this);
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
     * Questo metodo genera il dataset da inserire nella RecycleView
     */
    private void initDataset() {
        DbHelper helper = new DbHelper(this);
        Library library = getLibraryByName(helper, name);
        if (library != null) {
            mDataset = getBookByLibray(helper, library);
        }
        if(mDataset != null) {
            for (int i = 0; i < mDataset.size(); i++) {
                mDataset.get(i).setAuthors(setAuthor(helper, mDataset.get(i)));
                mDataset.get(i).setCategories(setCategories(helper, mDataset.get(i)));
            }
        }
        helper.close();
    }


    /**
     * Questo metodo inserisce tutti i listener e la callback per il corretto funzionamento dell'eliminazione
     * di un oggetto tramite swipe
     */
    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    //da inserire questa chiamata a funzione nel listener del dialog
                    //mAdapter.removeItem(position);
                    removeView();
                    alertDialog.setTitle("Confermare l'eliminazione");
                    posizione_da_eliminare = position;
                    alertDialog.show();
                }//un possibile swipe a destra da inserire qui
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Paint p = new Paint();
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        //icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        //RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        //c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        //icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        //RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        //c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    /**
     * Questo metodo rimuove la view dal suo parent
     */
    private void removeView() {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    /**
     * Questo metodo rimuove un libro dal dataset data la sua posizione
     * @param position la posizione dell'elemento da rimuovere
     */
    private void removeBook(int position) {
        DbHelper helper = new DbHelper(this);
        Library library = getLibraryByName(helper, name);
        if (deleteBookLibraryRelation(helper, mDataset.get(position), library)) {
            if (allBookLibraryRelation(helper, mDataset.get(position)) == 0) {
                deleteBook(helper, mDataset.get(position));
                initDataset();
                mAdapter.notifyDataSetChanged();
            }
        } else {
            Toast toast = Toast.makeText(this, "impossibile eliminare il libro", Toast.LENGTH_SHORT);
            toast.show();
        }
        helper.close();
    }

    /**
     * Questo metodo avvia il Dialog per la conferma (o annullamento) dell'eliminazione
     * dell'elemento dal dataset
     */
    private void initDialog() {
        alertDialog = new AlertDialog.Builder(this);
        view = getLayoutInflater().inflate(R.layout.dialog_layout_wishlist, null);
        alertDialog.setView(view);
        alertDialog.setPositiveButton("ELIMINA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeBook(posizione_da_eliminare);
                mAdapter.removeItem(posizione_da_eliminare);
                mAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("ANNULLA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * Questo metodo imposta e gestisce la ricerca di un libro all'interno di una libreria
     * @param menu il menu in cui è inserita la ricerca
     * @return true se la ricerca è stata gestista, false se viene applicato il comportamento di default
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.library_menu, menu);
        MenuItem item = menu.findItem(R.id.search_title);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                mAdapter.getFilter().filter(text);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}