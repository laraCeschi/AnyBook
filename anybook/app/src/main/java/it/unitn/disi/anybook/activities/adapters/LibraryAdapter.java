package it.unitn.disi.anybook.activities.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.unitn.disi.anybook.R;
import it.unitn.disi.anybook.activities.LibraryActivity;
import it.unitn.disi.anybook.data.Library;
import it.unitn.disi.anybook.databaseUtil.DbHelper;

import java.util.ArrayList;

import static it.unitn.disi.anybook.dataHandler.LibraryHandler.addLibrary;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.existLibrary;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.getNumbOfBookByLibrary;

/**
 * Questa classe si occupa della disposizione dei dati in item posti in una recycleview
 * @see it.unitn.disi.anybook.activities.fragments.LibraryFragment
 */
public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {
    private static final String TAG = "LibraryAdapter";

    private static ArrayList<Library> mDataSet;
    protected Context context;

    /**
     * Questa classe descrive gli oggetti presenti nella RecycleView e la loro posizione
     * @see android.support.v7.widget.RecyclerView.ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewLibraryName;
        private final TextView textViewLibraryItemCount;


        /**
         * Questo metodo dispone i dati all'interno di una View
         * @param v la View in cui disporre il dataset
         */
        ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                    Context context = v.getContext();
                    Intent intent = new Intent(context, LibraryActivity.class);
                    intent.putExtra("name", mDataSet.get(getAdapterPosition()).getName());
                    context.startActivity(intent);
                }
            });
            textViewLibraryName = (TextView) v.findViewById(R.id.library_title);
            textViewLibraryItemCount = (TextView) v.findViewById(R.id.library_number_of_item);
        }

        TextView getTextViewLibraryTitle() {
            return textViewLibraryName;
        }
        TextView getTextViewLibraryItemCount() {return textViewLibraryItemCount;}

    }

     /**
     * Inizializza il dataset dell'adapter.
     * @param dataSet ArrayList<Library> contenente i dati da utilizzare nell'adapter della libreria
     */
    public LibraryAdapter(ArrayList<Library> dataSet) {
        mDataSet = dataSet;
    }

    /**
     *  Questo metodo crea nuovi elementi nella RecycleView
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.library_item, viewGroup, false);
        context = v.getContext();
        return new ViewHolder(v);
    }

    /**
     * Questo metodo posiziona gli elementi del dataset all'interno della View
     * @param viewHolder la View in cui disporre gli elementi
     * @param position la posizione dell'elemento da inserire
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        //definirlo con l'arraylist
        viewHolder.getTextViewLibraryTitle().setText(mDataSet.get(position).getName());
        TextView librarycount = viewHolder.getTextViewLibraryItemCount();
        Library pos = mDataSet.get(position);
        DbHelper help = new DbHelper(context);
        int num = getNumbOfBookByLibrary(help, pos);
        librarycount.setText(String.valueOf(num));
        help.close();
    }

    /**
     * Questo metodo conta gli elementi del dataset
     * @return la dimensione del dataset
     */
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /*
questo metodo aggiunge un item al dataset
 */
    public boolean addItem(String title) {
        boolean returnable = false;
        DbHelper helper = new DbHelper(context);
        boolean alreadyExist = existLibrary(helper,title);
        if (!alreadyExist) {
            Library lib = new Library(title);
            addLibrary(helper, lib);
            mDataSet.add(lib);
            notifyItemInserted(mDataSet.size());
            returnable = true;
        }
        helper.close();
        return returnable;
    }


}