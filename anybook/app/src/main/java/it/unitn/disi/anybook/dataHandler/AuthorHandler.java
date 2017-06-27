package it.unitn.disi.anybook.dataHandler;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import it.unitn.disi.anybook.data.Author;
import it.unitn.disi.anybook.data.Book;
import it.unitn.disi.anybook.databaseUtil.DbContract;
import it.unitn.disi.anybook.databaseUtil.DbHelper;

/**
 * Questa classe gestisce le query al database per gli autori (Author)
 */

public class AuthorHandler {


    /**
     * funzione per controllare l'esistenza di un autore in database
     * @param dbHelper l'oggetto per la conessione al database
     * @param id l'id dell'autore in database
     * @return ritorna true se esiste, false altrimenti
     */
    public static boolean existAuthor(DbHelper dbHelper, long id){
        boolean exist = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DbContract.AuthorEntry.TABLE_NAME,
                null,
                DbContract.AuthorEntry._ID + " = " + id,
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
     * funzione per controllare l'esistenza di un autore in database
     * @param helper l'oggetto per la conessione al database
     * @param name il nome dell'autore in database
     * @return ritorna true se esiste, false altrimenti
     */
    public static boolean existAuthor(DbHelper helper, String name){
        boolean exist = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.AuthorEntry.TABLE_NAME,
                null,
                DbContract.AuthorEntry.NAME + " = " + "\"" + name + "\"",
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
     * funzione per aggiungere al database un autore
     * @param dbHelper l'oggetto per la conessione al database
     * @param author l'oggetto autore da inserire in database
     * @return ritorna true se l'inserimento è andatao a buon fine, false altrimenti, puo significare anche la già esistenza dell'autore in database
     */
    public static boolean addAuthor(DbHelper dbHelper, Author author){
        if(existAuthor(dbHelper, author.getName())){
            return false;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbContract.AuthorEntry.NAME, author.getName());
        long id = 0;
        try {
            db.beginTransaction();
            id = db.insert(DbContract.AuthorEntry.TABLE_NAME, null, cv);
            db.setTransactionSuccessful();
            db.endTransaction();
        }catch (SQLException e){
            System.out.print(e.toString());
        }
        if(id >= 0){
            author.setDb_id(id);
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Questo metodo restituisce un autore dato il suo nome
     * @param helper  l'oggetto per la conessione al database
     * @param name il nome dell'autore
     * @return l'autore trovato nel database, null se non è presente nel db
     */
    public static Author getAuthorByName(DbHelper helper, String name){
        Author author = null;

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.AuthorEntry.TABLE_NAME,
                null,
                DbContract.AuthorEntry.NAME + " = " + "\"" + name + "\"",
                null,
                null,
                null,
                null,
                null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            author = new Author(c.getInt(c.getColumnIndex(DbContract.AuthorEntry._ID)),
                    c.getString(c.getColumnIndex(DbContract.AuthorEntry.NAME)));
            c.moveToNext();
        }
        c.close();
        return author;
    }

    /**
     * funzione per controllare l'esisteza in database della relazione tra un libro e un autore
     * @param dbHelper db.endTransaction();
     * @param author l'oggetto autore da controllare
     * @param book l'oggetto libro da controllare
     * @return ritorna true se la relazione esite gia in database, false altrimenti
     */
    public static boolean existAuthorBookRelation(DbHelper dbHelper, Author author, Book book){
        if(!existAuthor(dbHelper, author.getDb_id())){
            return true;
        }
        if(!BookHandler.existBook(dbHelper, book.getISBN())){
            return true;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DbContract.BookAuthorEntry.TABLE_NAME,
                null,
                DbContract.BookAuthorEntry.ID_BOOK + " = " + book.getID() + " AND "+
                        DbContract.BookAuthorEntry.ID_AUTHOR + " = " + author.getDb_id(),
                null,
                null,
                null,
                null,
                null);

        if(c.getCount() > 0){
            c.close();
            db.close();
            return true;
        }
        else{
            c.close();
            db.close();
            return false;
        }
    }

    /**
     * aggiunge la relazione tra il libro e l'autore se non esiste gia
     * @param dbHelper l'oggetto per la conessione al database
     * @param author l'oggetto che descrive l'autore
     * @param book l'oggetto che descrive il libro
     * @return ritorna true se è stata inserita la entry in database
     */
    public static boolean addAuthorToBook(DbHelper dbHelper, Author author, Book book){
        if(!existAuthor(dbHelper, author.getDb_id())){
            return false;
        }
        if(!BookHandler.existBook(dbHelper, book.getISBN())){
            return false;
        }
        if(existAuthorBookRelation(dbHelper, author, book)){
            return false;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbContract.BookAuthorEntry.ID_BOOK, book.getID());
        cv.put(DbContract.BookAuthorEntry.ID_AUTHOR, author.getDb_id());
        db.insert(DbContract.BookAuthorEntry.TABLE_NAME, null, cv);
        db.close();
        return true;
    }

    /**
     * Questo metodo elimina un autore dal database
     * @param helper l'oggetto per la conessione al database
     * @param author l'autore da eliminare
     * @return true se l'eliminazione va a buon fine, false altrimenti
     */
    public static boolean deleteAuthor(DbHelper helper, Author author){
        if(!existAuthor(helper, author.getDb_id())){
            return false;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DbContract.AuthorEntry.TABLE_NAME,
                DbContract.AuthorEntry._ID + " = " + Long.toString(author.getDb_id()),
                null);
        db.close();
        return true;
    }

    /**
     *  Questo metodo controlla se questo autore è referenziato da qualche altra tabella nel database
     * @param helper l'oggetto per la conessione al database
      * @param author l'autore sotto esame
     * @return il numero di riferimenti all'autore, -1 se l'autore non esiste
     */
    public static int checkAuthorRelationship(DbHelper helper, Author author){
        if(!existAuthor(helper, author.getDb_id())){
            return -1;
        }
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.BookAuthorEntry.TABLE_NAME,
                null,
                DbContract.BookAuthorEntry.ID_AUTHOR + " = " + Long.toString(author.getDb_id()),
                null,
                null,
                null,
                null,
                null);
        int i = c.getCount();
        c.close();
        db.close();
        return i;
    }
}
