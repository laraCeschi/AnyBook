package it.unitn.disi.anybook.activities;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import it.unitn.disi.anybook.R;
import it.unitn.disi.anybook.activities.adapters.HelpAdapter;

/**
 * Questa classe rappresenta l'activity che gestisce la sezione di aiuto dell'app: cioè il tutorial
 * e l'about
 */
public class HelpActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    /**
     * Un fragment contenente una semplice view.
     */
    public static class PlaceholderFragment extends Fragment {

        RecyclerView mRecyclerView;
        int[] mDataSet;
        protected LayoutManagerType mCurrentLayoutManagerType;
        protected RecyclerView.LayoutManager mLayoutManager;
        private static final String KEY_LAYOUT_MANAGER = "layoutManager";
        protected HelpAdapter mAdapter;
        private enum LayoutManagerType {
            GRID_LAYOUT_MANAGER,
            LINEAR_LAYOUT_MANAGER
        }

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.help_tutorial_fragment, container, false);
                initDataset();
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.help_recyclerview);

                mLayoutManager = new LinearLayoutManager(getContext());

                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

                if (savedInstanceState != null) {
                    mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                            .getSerializable(KEY_LAYOUT_MANAGER);
                }
                setRecyclerViewLayoutManager(mCurrentLayoutManagerType);


                mAdapter = new HelpAdapter(mDataSet);
                mRecyclerView.setAdapter(mAdapter);
            }
            else if  (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                rootView = inflater.inflate(R.layout.fragment_help, container, false);
            }
            return rootView;
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

        //TODO: sostituire con le immagini vere
        void initDataset() {
            mDataSet = new int[7];
            mDataSet[0] = R.drawable.home_360;
            mDataSet[1]= R.drawable.menu_360;
            mDataSet[2] = R.drawable.lista_librerie_360;
            mDataSet[3]= R.drawable.wishlist_360;
            mDataSet[4] = R.drawable.libro_360;
            mDataSet[5]= R.drawable.recensioni_360;
            mDataSet[6]= R.drawable.selezione_librerie_360;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        /**
         * Il costruttore di base, che, preso un Fragment manager, chiama il costruttore della superclasse
         * @param fm il fragment manager per il costruttore della superclasse
         */
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "TUTORIAL";
                case 1:
                    return "ABOUT";
            }
            return null;
        }
    }
}
