package it.unitn.disi.anybook.dataHandler;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import it.unitn.disi.anybook.data.Author;
import it.unitn.disi.anybook.data.Book;
import it.unitn.disi.anybook.data.Category;
import it.unitn.disi.anybook.databaseUtil.DbContract;
import it.unitn.disi.anybook.databaseUtil.DbHelper;

import java.util.ArrayList;

import static it.unitn.disi.anybook.dataHandler.AuthorHandler.checkAuthorRelationship;
import static it.unitn.disi.anybook.dataHandler.AuthorHandler.deleteAuthor;

/**
 * Handler per gestire tutte le informazioni del book
 */

public class BookHandler {

    /**
     * controlla l'esistenza di un libro in database
     * @param dbHelper l'oggetto per la conessione al database
     * @param isbn l'ISBN del libro cercato, si ricorda che è unico in database
     * @return ritorna true se esiste in database
     */
    public static boolean existBook(DbHelper dbHelper, long isbn){
        boolean exist = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DbContract.BookEntry.TABLE_NAME,
                null,
                DbContract.BookEntry.ISBN + " = " + isbn,
                null,
                null,
                null,
                null,
                null);
        if(c.getCount() > 0){
            exist = true;
        }
        c.close();
        return exist;
    }


    /**
     * controlla l'esistenza di un libro in database
     * @param helper l'oggetto per la conessione al database
     * @param book l'oggetto libro che viene controllata l'esistenza
     * @return ritorna true se esiste in database
     */
    public static boolean existBook(DbHelper helper, Book book){
        return existBook(helper, book.getISBN());
    }


    /**
     * restituisce un libro ottenuto da database dato un ISBN
     * @param dbHelper l'oggetto per la conessione al database
     * @param isbn l'ISBN del libro cercato, si ricorda che è unico in database
     * @return ritorna il libro cercato oppure NULL se non è stato trovato
     */
    public static Book getBookByISBN(DbHelper dbHelper, long isbn){
        Book nuovo = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DbContract.BookEntry.TABLE_NAME,
                null,
                DbContract.BookEntry.ISBN + " = " + isbn,
                null,
                null,
                null,
                null,
                null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            boolean seleability = false;
            if(c.getInt(c.getColumnIndex(DbContract.BookEntry.SALEABILITY)) == 1){
                seleability = true;
            }
            nuovo = new Book(isbn,
                    c.getLong(c.getColumnIndex(DbContract.BookEntry._ID)),
                    c.getString(c.getColumnIndex(DbContract.BookEntry.TITLE)),
                    c.getString(c.getColumnIndex(DbContract.BookEntry.PUBLISHER)),
                    c.getString(c.getColumnIndex(DbContract.BookEntry.DESCRIPTION)),
                    c.getInt(c.getColumnIndex(DbContract.BookEntry.RATING)),
                    c.getInt(c.getColumnIndex(DbContract.BookEntry.RATINGCOUNT)),
                    c.getString(c.getColumnIndex(DbContract.BookEntry.IMAGE_THUMB)),
                    seleability,
                    c.getString(c.getColumnIndex(DbContract.BookEntry.LINK_FOR_SALE)),
                    c.getString(c.getColumnIndex(DbContract.BookEntry.IFRAME)),
                    c.getString(c.getColumnIndex(DbContract.BookEntry.LINK_FOR_SHARE)));
            c.moveToNext();
        }
        c.close();
        return nuovo;
    }


    /**
     * aggunge un libro al database se non esiste già
     * @param dbHelper l'oggetto per la conessione al database
     * @param libro l'oggetto libro che deve essere inserito in database
     * @return ritorna true se ha inserito in database, false se non ci è riuscito
     */
    public static boolean addBook(DbHelper dbHelper, Book libro){
        if(existBook(dbHelper, libro.getISBN())){
            return false;
        }
        SQLiteDatabase db;
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbContract.BookEntry.ISBN, libro.getISBN());
        cv.put(DbContract.BookEntry.TITLE, libro.getTitle());
        cv.put(DbContract.BookEntry.PUBLISHER, libro.getPublisher());
        cv.put(DbContract.BookEntry.DESCRIPTION, libro.getDescription());
        cv.put(DbContract.BookEntry.RATING, libro.getRating());
        cv.put(DbContract.BookEntry.RATINGCOUNT, libro.getRatingcount());
        cv.put(DbContract.BookEntry.IMAGE_THUMB, libro.getImage_thumb());
        cv.put(DbContract.BookEntry.SALEABILITY, libro.isSaleability());
        cv.put(DbContract.BookEntry.LINK_FOR_SALE, libro.getLink_for_sale());
        cv.put(DbContract.BookEntry.IFRAME, libro.getiFrame());
        cv.put(DbContract.BookEntry.LINK_FOR_SHARE, libro.getLink_for_share());

        long id;
        id = db.insert(DbContract.BookEntry.TABLE_NAME, null, cv);
        if(id >= 0) {
            libro.setId(id);
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * funzione per avere una lista di libri con il titolo voluto simile
     * @param dbHelper l'oggetto per la conessione al database
     * @param libro l'oggetto libro che contiene il titolo da cercare
     * @return ritorna l'array, se vuoto significa che non ha trovato niente
     */
    public static ArrayList<Book> getBookByTitle(DbHelper dbHelper, Book libro){
        return getBookByTitle(dbHelper, libro.getTitle());
    }

    /**
     * funzione per avere una lista di libri con il titolo voluto simile
     * @param dbHelper l'oggetto per la conessione al database
     * @param titolo la stringa da cercare nei titoli
     * @return ritorna l'array, se vuoto significa che non ha trovato niente
     */
    public static ArrayList<Book> getBookByTitle(DbHelper dbHelper, String titolo){
        ArrayList<Book> risultiati = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DbContract.BookEntry.TABLE_NAME,
                null,
                DbContract.BookEntry.TITLE + " LIKE ? %" + titolo + "% ",
                null,
                null,
                null,
                null,
                null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            boolean seleability = false;
            if(c.getInt(c.getColumnIndex(DbContract.BookEntry.SALEABILITY)) == 1){
                seleability = true;
            }
            Book libro = new Book(c.getInt(c.getColumnIndex(DbContract.BookEntry.ISBN)),
                    c.getLong(c.getColumnIndex(DbContract.BookEntry._ID)),
                    c.getString(c.getColumnIndex(DbContract.BookEntry.TITLE)),
                    c.getString(c.getColumnIndex(DbContract.BookEntry.PUBLISHER)),
                    c.getString(c.getColumnIndex(DbContract.BookEntry.DESCRIPTION)),
                    c.getInt(c.getColumnIndex(DbContract.BookEntry.RATING)),
                    c.getInt(c.getColumnIndex(DbContract.BookEntry.RATINGCOUNT)),
                    c.getString(c.getColumnIndex(DbContract.BookEntry.IMAGE_THUMB)),
                    seleability,
                    c.getString(c.getColumnIndex(DbContract.BookEntry.LINK_FOR_SALE)),
                    c.getString(c.getColumnIndex(DbContract.BookEntry.IFRAME)),
                    c.getString(c.getColumnIndex(DbContract.BookEntry.LINK_FOR_SHARE)));
            risultiati.add(libro);
            c.moveToNext();
        }
        c.close();
        return risultiati;
    }


    /**
     * la funzione ritorna gli autori inerenti un certo libro in un array
     * @param dbHelper l'oggetto per la conessione al database
     * @param book il libro su cui si vuole ricercare gli autori
     * @return ritorna un array non nullo contenente gli autori inerenti al libro, se il vettore è vuoto significa che non ha trovato nulla
     */
    public static ArrayList<Author> setAuthor(DbHelper dbHelper, Book book){
        ArrayList<Author> autori = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + DbContract.AuthorEntry._ID + ", " +
                DbContract.AuthorEntry.NAME + " FROM " + DbContract.AuthorEntry.TABLE_NAME + " JOIN " +
                DbContract.BookAuthorEntry.TABLE_NAME + " ON " +
                DbContract.AuthorEntry.TABLE_NAME + "." + DbContract.AuthorEntry._ID + " = " +
                DbContract.BookAuthorEntry.TABLE_NAME + "." + DbContract.BookAuthorEntry.ID_AUTHOR +
                "  WHERE " + DbContract.BookAuthorEntry.ID_BOOK + " = " + Long.toString(book.getID()),
                null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            Author author = new Author(c.getInt(c.getColumnIndex(DbContract.AuthorEntry._ID)),
                    c.getString(c.getColumnIndex(DbContract.AuthorEntry.NAME)));
            autori.add(author);
            c.moveToNext();
        }
        c.close();
        return autori;
    }


    /**
     * la funzione ritorna le categorie inerenti un certo libro in un array
     * @param helper l'oggetto per la conessione al database
     * @param book il libro su cui si vuole ricercare le categorie
     * @return ritorna un array non nullo contenente le categorie inerenti al libro, se il vettore è vuoto significa che non ha trovato nulla
     */
    public static ArrayList<Category> setCategories(DbHelper helper, Book book){
        ArrayList<Category> categorie = new ArrayList<> ();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + DbContract.CategoryEntry.TABLE_NAME + "." + DbContract.CategoryEntry._ID + " , " +
                DbContract.CategoryEntry.TABLE_NAME + "." + DbContract.CategoryEntry.NAME + " FROM " +
                DbContract.CategoryEntry.TABLE_NAME + " JOIN " + DbContract.BookCategoryrEntry.TABLE_NAME + " ON " +
                DbContract.CategoryEntry.TABLE_NAME + "." + DbContract.CategoryEntry._ID + " = " +
                DbContract.BookCategoryrEntry.TABLE_NAME + "." + DbContract.BookCategoryrEntry.ID_CATEGORY +
                " WHERE " +  DbContract.BookCategoryrEntry.TABLE_NAME + "." + DbContract.BookCategoryrEntry.ID_BOOK +
                " = " + Long.toString(book.getID()),
                null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            Category cat = new Category(c.getLong(c.getColumnIndex(DbContract.CategoryEntry._ID)),
                    c.getString(c.getColumnIndex(DbContract.CategoryEntry.NAME)));
            categorie.add(cat);
            c.moveToNext();
        }
        c.close();
        return categorie;
    }


    /**
     * metodo per l'eliminazione del libro, in modo safe e riccorrente, cioè tutte le dipendenze in database
     * @param helper l'oggetto per la conessione al database
     * @param book il libro che si desidera eliminare
     * @return ritorna true se il tutto è stato portato a compimento, false altrimenti
     */
    public static boolean deleteBook(DbHelper helper, Book book){
        if(!existBook(helper, book)){
            return false;
        }
        SQLiteDatabase db = helper.getWritableDatabase();

        ArrayList<Author> listaAutori = setAuthor(helper, book);
        db.delete(DbContract.BookAuthorEntry.TABLE_NAME,
                DbContract.BookAuthorEntry.ID_BOOK + " = " + Long.toString(book.getID()),
                null);

        for(int i = 0; i<listaAutori.size(); i++){
            if(checkAuthorRelationship(helper, listaAutori.get(i)) == 0){
                deleteAuthor(helper, listaAutori.get(i));
            }
        }
        db = helper.getWritableDatabase();
        db.delete(DbContract.BookCategoryrEntry.TABLE_NAME,
                DbContract.BookCategoryrEntry.ID_BOOK + " = " + Long.toString(book.getID()),
                null);

        db = helper.getWritableDatabase();
        int c = db.delete(DbContract.BookEntry.TABLE_NAME,
                DbContract.BookEntry._ID + " = " + Long.toString(book.getID()),
                null);

        if(c > -1){
            return true;
        }
        return false;
    }
}
