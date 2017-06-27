
package it.unitn.disi.anybook.activities.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import it.unitn.disi.anybook.R;


/**
 * Questa classe si occupa della disposizione dei dati in item posti in una recycleview
 * @see it.unitn.disi.anybook.activities.HelpActivity
 */
public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.ViewHolder> {

    private int[] mDataSet;

    /**
     * Questa classe descrive gli oggetti presenti nella RecycleView e la loro posizione
     * @see android.support.v7.widget.RecyclerView.ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        /**
         * Questo metodo dispone i dati all'interno di una View
         * @param v la View in cui disporre il dataset
         */
        ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.image);
         }

        ImageView getImageView() {
            return imageView;
        }
       }

    /**
     * Questo metodo inizializza il dataset dell'adapter
     * @param dataSet int[] contenente gli id delle risorse da inserire come tutorial
     */
    public HelpAdapter(int[] dataSet) {
        mDataSet = dataSet;
    }

    /**
     *  Questo metodo crea nuovi elementi nella RecycleView
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.help_tutorial_item, viewGroup, false);

        return new ViewHolder(v);
    }

    /**
     * Questo metodo posiziona gli elementi del dataset all'interno della View
     * @param viewHolder la View in cui disporre gli elementi
     * @param position la posizione dell'elemento da inserire
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        int currentSrc = mDataSet[position];
        viewHolder.getImageView().setImageResource(currentSrc);

       }

    /**
     * Questo metodo conta gli elementi del dataset
     * @return la dimensione del dataset
     */
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}