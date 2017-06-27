package it.unitn.disi.anybook.activities.fragments;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import it.unitn.disi.anybook.R;
import it.unitn.disi.anybook.activities.adapters.BookListAdapter;
import it.unitn.disi.anybook.data.Book;
import it.unitn.disi.anybook.data.Library;
import it.unitn.disi.anybook.databaseUtil.DbHelper;

import java.util.ArrayList;

import static it.unitn.disi.anybook.data.StaticStrings.WISHLIST;
import static it.unitn.disi.anybook.dataHandler.BookHandler.deleteBook;
import static it.unitn.disi.anybook.dataHandler.BookHandler.setAuthor;
import static it.unitn.disi.anybook.dataHandler.BookHandler.setCategories;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.allBookLibraryRelation;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.deleteBookLibraryRelation;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.getBookByLibray;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.getLibraryByName;

/**
 * Questa classe rappresenta il Fragment in cui viene inserita la Wishlist
 */
public class WishlistFragment extends Fragment {
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
    private AlertDialog.Builder alertDialog;
    private View view;
    private int posizione_da_eliminare = -1 ;

    /**
     * Questo metodo crea il Fragment e inizializza il dataset della RecycleView ospitata
     * @param savedInstanceState contiene i dati più recenti forniti a onSaveInstanceState().
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataset();
    }

    /**
     *Questo metodo istanzia la grafica del Fragment.
     * @param inflater il LayoutInflater che viene utilizzato per "gonfiare" una view in un Fragment
     * @param container la view gerarchicamente superiore in cui va inserito il Fragment
     * @param savedInstanceState l'eventuale stato precedente del Fragment
     * @return la View della grafica del Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_fragment, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.book_list_recyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new BookListAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);
        initDialog();
        initSwipe();
        return rootView;
    }

    /**
     * Questo metodo performa le azioni da compiere alla ripresa dell'attività
     */
    @Override
    public void onResume() {
        super.onResume();
        initDataset();
        mAdapter = new BookListAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Questo metodo imposta il LayoutManager della RecycleView
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
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
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
        DbHelper helper = new DbHelper(getContext());
        Library library = getLibraryByName(helper, WISHLIST);
        if(library != null) {
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
    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if(direction == ItemTouchHelper.LEFT){
                    //da inserire questa chiamata a funzione nel listener del dialog
                    removeView();
                    alertDialog.setTitle("Confermare l'eliminazione");
                    posizione_da_eliminare = position;
                    alertDialog.show();
                }
                /*else{
                    //un possibile swipe a destra da inserire qui
                }*/
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Paint p = new Paint();
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        //icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        //TODO: da inserire una possibile immagine
                        //RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        //c.drawBitmap(icon,null,icon_dest,p);
                    }
                    else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        //icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        // TODO: da inserire una possibile immagine
                        //RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
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
     * Questo metodo rimuove il dialog dal suo parent
     */
    private void removeView(){
        if(view.getParent() != null){
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    /**
     * Questo metodo elimina un libro data la sua posizione
     * @param position l'indice a cui si trova il libro da eliminare
     */
    private void removeBook(int position){
        DbHelper helper = new DbHelper(getActivity());
        Library library = getLibraryByName(helper, WISHLIST);
        if(deleteBookLibraryRelation(helper, mDataset.get(position), library)){
            if(allBookLibraryRelation(helper, mDataset.get(position)) == 0){
                deleteBook(helper, mDataset.get(position));
                initDataset();
                mAdapter.notifyDataSetChanged();
            }
        }
        else{
            Toast toast = Toast.makeText(getActivity(), "inmpossibile eliminare il libro", Toast.LENGTH_SHORT);
            toast.show();
        }
        helper.close();
    }

    /**
     * Questo metodo inizializza il dialog per la conferma (o annullamento) dell'eliminazione
     */
    private void initDialog(){
        alertDialog = new AlertDialog.Builder(this.getContext());
        // questo hint è solo inutile, null viene passato come rootview facoltativa
        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_layout_wishlist, null);
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
}