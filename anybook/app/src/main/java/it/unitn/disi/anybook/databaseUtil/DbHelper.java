package it.unitn.disi.anybook.databaseUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * In questa classe avviene la creazione delle varie tabelle e il controllo della versione del database
 * è presente il metodo per l'upgrade del database di sistema
 * il metodo onCreate crea le stringhe di query e le esegue
 * il metodo onupgrade elimina il tutto e lo ricrea
 * ATTENZIONE!! durante la eliminazione delle tabelle vengono cancellati in modo permanente i dati
 * rivedere la funzione affinchè facia un backup e importi i vecchi dati nel nuovo database
 */

public class DbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "unitn.unitn.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 12;

    /**
     * Questo metodo costruisce un DbHelper
     * @param context il contesto necessario al costruttore della superclasse
     */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Questo metodo costuisce le tabelle del database impostando i constraints sulle colonne
     * @param db il database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE_BOOK = "CREATE TABLE " + DbContract.BookEntry.TABLE_NAME + " ( " +
                DbContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.BookEntry.ISBN + " INTEGER NOT NULL UNIQUE, " +
                DbContract.BookEntry.TITLE + " VARCHAR, " +
                DbContract.BookEntry.PUBLISHER + " VARCHAR, " +
                DbContract.BookEntry.DESCRIPTION + " VARCHAR, " +
                DbContract.BookEntry.RATING + " INTEGER, " +
                DbContract.BookEntry.RATINGCOUNT + " INTEGER, " +
                DbContract.BookEntry.IMAGE_THUMB + " VARCHAR, " +
                DbContract.BookEntry.SALEABILITY + " BOOLEAN, " +
                DbContract.BookEntry.LINK_FOR_SALE + " VARCHAR, " +
                DbContract.BookEntry.IFRAME + " VARCHAR, " +
                DbContract.BookEntry.LINK_FOR_SHARE + " VARCHAR " +
                " );";

        db.execSQL(SQL_CREATE_TABLE_BOOK);

        final String SQL_CREATE_TABLE_AUTHOR = " CREATE TABLE " + DbContract.AuthorEntry.TABLE_NAME + " ( " +
                DbContract.AuthorEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.AuthorEntry.NAME + " VARCHAR " +
                " ); ";

        db.execSQL(SQL_CREATE_TABLE_AUTHOR);

        final String SQL_CREATE_TABLE_CATEGORY = " CREATE TABLE " + DbContract.CategoryEntry.TABLE_NAME + " ( " +
                DbContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.CategoryEntry.NAME + " VARCHAR " +
                " ); ";

        db.execSQL(SQL_CREATE_TABLE_CATEGORY);

        final String SQL_CREATE_TABLE_BOOKAUTHOR = " CREATE TABLE " + DbContract.BookAuthorEntry.TABLE_NAME + " ( " +
                DbContract.BookAuthorEntry.ID_AUTHOR + " INTEGER NOT NULL, " +
                DbContract.BookAuthorEntry.ID_BOOK + " INTEGER NOT NULL, " +
                " PRIMARY KEY ( " + DbContract.BookAuthorEntry.ID_BOOK +
                ", " + DbContract.BookAuthorEntry.ID_AUTHOR + " ), " +
                " FOREIGN KEY ( " + DbContract.BookAuthorEntry.ID_AUTHOR + " ) " +
                " REFERENCES " + DbContract.AuthorEntry.TABLE_NAME + " ( " + DbContract.AuthorEntry._ID + " ), " +
                " FOREIGN KEY ( " + DbContract.BookAuthorEntry.ID_BOOK + " ) " +
                " REFERENCES " + DbContract.BookEntry.TABLE_NAME + " ( " + DbContract.BookEntry._ID + " ) " +
                " ); ";

        db.execSQL(SQL_CREATE_TABLE_BOOKAUTHOR);

        final String SQL_CREATE_TABLE_LIST = " CREATE TABLE " + DbContract.ListEntry.TABLE_NAME + " ( " +
                DbContract.ListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.ListEntry.NAME + " VARCHAR " +
                " ); ";

        db.execSQL(SQL_CREATE_TABLE_LIST);

        final String SQL_CREATE_TABLE_BOOKCATEGORY = " CREATE TABLE " + DbContract.BookCategoryrEntry.TABLE_NAME + " ( " +
                DbContract.BookCategoryrEntry.ID_BOOK + " INTEGER NOT NULL, " +
                DbContract.BookCategoryrEntry.ID_CATEGORY + " INTEGER NOT NULL, " +
                " PRIMARY KEY ( " + DbContract.BookCategoryrEntry.ID_BOOK +
                ", " + DbContract.BookCategoryrEntry.ID_CATEGORY + " ), " +
                " FOREIGN KEY ( " + DbContract.BookCategoryrEntry.ID_CATEGORY + " ) " +
                " REFERENCES " + DbContract.CategoryEntry.TABLE_NAME + " ( " + DbContract.CategoryEntry._ID + " ), " +
                " FOREIGN KEY ( " + DbContract.BookCategoryrEntry.ID_BOOK + " ) " +
                " REFERENCES " + DbContract.BookEntry.TABLE_NAME + " ( " + DbContract.BookEntry._ID + " ) " +
                " ); ";

        db.execSQL(SQL_CREATE_TABLE_BOOKCATEGORY);

        final String SQL_CREATE_TABLE_BOOKLIST = " CREATE TABLE " + DbContract.BookListEntry.TABLE_NAME + " ( " +
                DbContract.BookListEntry.ID_LIST + " INTEGER NOT NULL, " +
                DbContract.BookListEntry.ID_BOOK + " INTEGER NOT NULL, " +
                " PRIMARY KEY ( " + DbContract.BookListEntry.ID_BOOK +
                ", " + DbContract.BookListEntry.ID_LIST + " ), " +
                " FOREIGN KEY ( " + DbContract.BookListEntry.ID_LIST + " ) " +
                " REFERENCES " + DbContract.ListEntry.TABLE_NAME + " ( " + DbContract.ListEntry._ID + " )," +
                " FOREIGN KEY ( " + DbContract.BookCategoryrEntry.ID_BOOK + " ) " +
                " REFERENCES " + DbContract.BookEntry.TABLE_NAME + " ( " + DbContract.BookEntry._ID + " ) " +
                " ); ";

        db.execSQL(SQL_CREATE_TABLE_BOOKLIST);
    }


    /**
     * Questo metodo elimina le tabelle del database per ricrearlo quando viene modificata la versione del database
     * @param db il database
     * @param oldVersion la vecchia versione del database
     * @param newVersion la nuova versione del database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.BookAuthorEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.BookCategoryrEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.BookListEntry.TABLE_NAME);

        db.execSQL("DROP TABLE IF EXISTS " + DbContract.BookEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.AuthorEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.CategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.ListEntry.TABLE_NAME);

        onCreate(db);
    }
}