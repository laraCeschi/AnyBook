package it.unitn.disi.anybook.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import it.unitn.disi.anybook.APIHandler.APIgoogleBooks;
import it.unitn.disi.anybook.R;
import it.unitn.disi.anybook.activities.fragments.HomeFragment;
import it.unitn.disi.anybook.activities.fragments.LibraryFragment;
import it.unitn.disi.anybook.activities.fragments.WishlistFragment;
import it.unitn.disi.anybook.data.Library;
import it.unitn.disi.anybook.databaseUtil.DbHelper;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.List;


import static it.unitn.disi.anybook.data.StaticStrings.IN_POSSESSO;
import static it.unitn.disi.anybook.data.StaticStrings.WISHLIST;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.addLibrary;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.existLibray;


/**
 * Questa classe rappresenta l'activity principale dell'applicazione: contiene il drawer menu, le tab di home,
 * libreria e wishlist
 */
public class TabActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private ViewPager mViewPager;
    DrawerLayout mDrawerLayout;
    NavigationView mDrawerNav;
    ActionBarDrawerToggle mDrawerToggle;

    /**
     * questo metodo inizializza gli elementi della view
     * @param savedInstanceState lo stato precedente dell'activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setUpViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // DB
        popolateDatabase();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout = drawer;

        mDrawerNav = (NavigationView) findViewById(R.id.left_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.drawer_open, R.string.drawer_close);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerNav.setNavigationItemSelectedListener(this);
        mDrawerNav.setItemIconTintList(null);
    }

    /**
     * Questo metodo imposta i fragment delle tab della home page
     * @param viewPager il view pager in cui verranno inseriti i frammenti
     */
    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new WishlistFragment(), "WISHLIST");
        adapter.addFrag(new HomeFragment(), "HOME");
        adapter.addFrag(new LibraryFragment(), "LIBRERIE");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

    /**
     * Questa classe rappresenta l'adapter per i fragment delle tab
     */
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /**
     * Questo metodo mostra la tab della wishlist
     */
    public void gotoWishlistTab() {
        mViewPager.setCurrentItem(0);
    }

    /**
     *  Questo metodo mostra la tab della Home
     */
    public void gotoHomeTab() {
        mViewPager.setCurrentItem(1);
    }

    /**
     * Questo metodo mostra la tab delle librerie
     */
    public void gotoLibraryTab() {
        mViewPager.setCurrentItem(2);
    }

    /**
     * Questo metodo avvia l'activity della scansione
     * @param view questo parametro è necessario per la chiamata tramite il metodo OnClick di un bottone
     */
    public void scanBarcode(View view) {
        if (ActivityCompat.checkSelfPermission(TabActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TabActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
        } else {
            Intent intent = new Intent(this, ScanActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    /**
     * Questo metodo mostra i risultati della richiesta dei permessi
     * @param requestCode il codice di richiesta
     * @param permissions i permessi richiesti
     * @param grantResults interi rappresentati, per ogni permesso richiesto, la risposta
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, ScanActivity.class);
                    startActivityForResult(intent, 0);
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Questo metodo performa le azioni da effettuare quando viene ripresa l'activity
     */
    @Override
    protected void onRestart() {
        super.onRestart();
    }


    /**
     * Questo metodo gestisce il risultato della chiamata ad un'altra activity
     * @param requestCode il codice con cui è stata lanciata l'activity
     * @param resultCode il codice restituito dall'activity
     * @param data i possibili dati di ritorno dell'activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //qui arriva il barcode nella variabile data
        //un possibile codice sarebbe
        //result del scan activity
        if (requestCode == 0) {
            //qui si arriva perche il lettore di barcode finisce di leggere
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");

                    APIgoogleBooks api = new APIgoogleBooks();
                    //questo book è un json
                    String book = api.makeBookSearchQuery(barcode.displayValue);
                    Intent intent = new Intent(this, BookActivity.class);
                    intent.putExtra("book", book);
                    intent.putExtra("isbn", Long.valueOf(barcode.displayValue));
                    startActivityForResult(intent, 1);
                } else {
                    Toast toast = Toast.makeText(this, "no barocode found", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
        if (requestCode == 1) {
            //qui si arriva perche la BookActivity non è riuscita a ottenere il libro in nessun modo
            if (resultCode == CommonStatusCodes.ERROR) {
                Toast toast = Toast.makeText(this, "isbn non trovato", Toast.LENGTH_LONG);
                toast.show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Questo metodo gestisce le azioni da effettuare quando viene premuto il pulsante "back", cioè, se il drawer è aperto, lo chiude
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     *Questo metodo gestisce i comportamenti dei pulsanti del drawer menu
     * @param item l'elemento selezionato del menu
     * @return sempre true
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_scan) {
            scanBarcode(mViewPager);
        } else if (id == R.id.nav_wishlist) {
            gotoWishlistTab();
        } else if (id == R.id.nav_library) {
            gotoLibraryTab();
        } else if (id == R.id.nav_home) {
            gotoHomeTab();
        } else if (id == R.id.nav_help) {
           Intent intent = new Intent(this, HelpActivity.class);
           startActivity(intent);
        }
        item.setChecked(false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Questo metodo inizializza il database, inserendo le librerie di default wishlist e in possesso
     */
    private void popolateDatabase() {
        DbHelper helper = new DbHelper(this);

        Library wish = new Library(WISHLIST);
        Library in_pos = new Library(IN_POSSESSO);

        if(!existLibray(helper, wish)) {
            addLibrary(helper, wish);
        }
        if(!existLibray(helper, in_pos)) {
            addLibrary(helper, in_pos);
        }
        helper.close();
    }
}

