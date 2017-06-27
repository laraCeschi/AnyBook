package it.unitn.disi.anybook.dataHandler;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import it.unitn.disi.anybook.data.Book;
import it.unitn.disi.anybook.data.Library;
import it.unitn.disi.anybook.databaseUtil.DbContract;
import it.unitn.disi.anybook.databaseUtil.DbHelper;

import java.util.ArrayList;

import static it.unitn.disi.anybook.dataHandler.BookHandler.deleteBook;
import static it.unitn.disi.anybook.dataHandler.BookHandler.existBook;

/**
 * Handler per gestire tutti gli oggetti library ottenuti da database
 */

public class LibraryHandler {


    /**
     * funzione per controllare l'esistenza di una libreria in database
     * @param helper l'oggetto per la conessione al database
     * @param id l'id della libreria che si vuole verificare l'esistenza
     * @return ritorna true se lo ha trovato, false altrimenti
     */
    public static boolean existLibrary(DbHelper helper, long id){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.ListEntry.TABLE_NAME,
                null,
                DbContract.ListEntry._ID + " = " + id,
                null,
                null,
                null,
                null,
                null);
        if(c.getCount() > 0){
            c.close();
            return true;
        }
        c.close();
        return false;
    }


    /**
     * funzione per controllare l'esistenza di una libreria in database
     * @param helper l'oggetto per la conessione al database
     * @param name il nome della libreria che si vuole verificare l'esistenza
     * @return ritorna true se lo ha trovato, false altrimenti
     */
    public static boolean existLibrary(DbHelper helper, String name){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.ListEntry.TABLE_NAME,
                null,
                DbContract.ListEntry.NAME + " = \"" + name + "\"",
                null,
                null,
                null,
                null,
                null);
        if(c.getCount() > 0){
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    /**
     * funzione per controllare l'esistenza di una libreria in database
     * @param helper l'oggetto per la conessione al database
     * @param library l'oggetto libreria da ricercare l'esistenza in database
     * @return ritorna true se lo ha trovato, false se no
     */
    public static boolean existLibray(DbHelper helper, Library library){
        return existLibrary(helper, library.getName());
    }


    /**
     * funzione per ricerare dei libri appartenenti ad una determinata libreria
     * @param helper l'oggetto per la conessione al database
     * @param library la libreria a cui appartengono i libri
     * @return ritona un array contente i libri, se è vuoto significa che non ne ha trovati
     */
    public static ArrayList<Book> getBookByLibray(DbHelper helper, Library library){
        if(!existLibray(helper, library)){
            return null;
        }
        ArrayList<Book> lista = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery( "SELECT * FROM " + DbContract.BookEntry.TABLE_NAME + " JOIN " +
                DbContract.BookListEntry.TABLE_NAME + " ON " +
                DbContract.BookEntry.TABLE_NAME + "." + DbContract.BookEntry._ID + " = " +
                DbContract.BookListEntry.TABLE_NAME + "." + DbContract.BookListEntry.ID_BOOK +
                " WHERE " + DbContract.BookListEntry.ID_LIST + " = " + Long.toString(library.getId()),
                null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            boolean seleability = false;
            if(c.getInt(c.getColumnIndex(DbContract.BookEntry.SALEABILITY)) == 1){
                seleability = true;
            }
            Book libro = new Book(c.getLong(c.getColumnIndex(DbContract.BookEntry.ISBN)),
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
            lista.add(libro);
            c.moveToNext();
        }
        c.close();
        return lista;
    }

    /**
     * funzione per ottenere tutte le librerie presenti in database
     * @param helper l'oggetto per la conessione al database
     * @return ritorna un array di librerie, se è vuoto significa che non ha trovato niente
     */
    public static ArrayList<Library> getAllLibrary(DbHelper helper){
        ArrayList<Library> lista = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.ListEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            Library library= new Library(c.getLong(c.getColumnIndex(DbContract.ListEntry._ID)),
                    c.getString(c.getColumnIndex(DbContract.ListEntry.NAME)));
            lista.add(library);
            c.moveToNext();
        }
        c.close();
        return lista;
    }

    /**
     * funzione per far ritornare una libreria dato un nome
     * @param helper l'oggetto per la conessione al database
     * @param name il nome della libreria da cercare
     * @return ritorna la libreria se esiste null altrimenti
     */
    public static Library getLibraryByName(DbHelper helper, String name){
        Library library = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.ListEntry.TABLE_NAME,
                null,
                DbContract.ListEntry.NAME + " = \"" + name + "\"",
                null,
                null,
                null,
                null,
                null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            library = new Library(c.getLong(c.getColumnIndex(DbContract.ListEntry._ID)),
                    c.getString(c.getColumnIndex(DbContract.ListEntry.NAME)));
            c.moveToNext();
        }
        c.close();
        return library;
    }

    /**
     * aggiunge la libreria al database
     * @param helper l'oggetto per la conessione al database
     * @param library l'oggetto libreria da inserire
     * @return ritorna true se l'inserimento è avvenuto false altrimenti
     */
    public static boolean addLibrary(DbHelper helper, Library library){
        if(existLibrary(helper, library.getName())){
            return false;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbContract.ListEntry.NAME, library.getName());
        long id;
        id = db.insert(DbContract.ListEntry.TABLE_NAME, null, cv);
        if(id >= 0){
            library.setId(id);
        }
        else{
            return false;
        }
        return true;
    }


    /**
     * funzione per verificare l'esistenza della relazione tra libro e libreria
     * @param helper l'oggetto per la conessione al database
     * @param book l'oggetto libro da verifiare
     * @param library l'oggetto libreria da veriticare
     * @return ritorna true se esiste già nel database
     */
    public static boolean existLibraryBookRelation(DbHelper helper, Book book, Library library){
        if(!existLibray(helper, library)){
            return false;
        }
        if(!existBook(helper, book)){
            return false;
        }
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.BookListEntry.TABLE_NAME,
                null,
                DbContract.BookListEntry.ID_BOOK + " = " + book.getID() + " AND " +
                        DbContract.BookListEntry.ID_LIST + " = " + library.getId(),
                null,
                null,
                null,
                null,
                null);
        if(c.getCount() > 0){
            c.close();
            return true;
        }
        else{
            c.close();
            return false;
        }
    }


    /**
     * funzione per aggiungere la relazione di appartenenza di un libro a una libraria
     * @param helper l'oggetto per la conessione al database
     * @param book l'oggetto libro da verifiare
     * @param library l'oggetto libreria da veriticare
     * @return ritorna true se l'inserimento è avvenuto false altrimenti
     */
    public static boolean addBookToLibrary(DbHelper helper, Book book, Library library){
        if(existLibraryBookRelation(helper, book, library)){
            return false;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbContract.BookListEntry.ID_BOOK, book.getID());
        cv.put(DbContract.BookListEntry.ID_LIST, library.getId());
        db.insert(DbContract.BookListEntry.TABLE_NAME, null, cv);
        return true;
    }

    /**
     * Questo metodo ritorna il numero di librerie che contengono il libro passato come parametro
     * @param helper l'oggetto per la connesione al database
     * @param book il libro sotto esame
     * @return il numero di librerie che contiene il libro, -1 se il libro non esiste
     */
    public static int allBookLibraryRelation(DbHelper helper, Book book){
        if(!existBook(helper, book)){
            return -1;
        }
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.BookListEntry.TABLE_NAME,
                null,
                DbContract.BookListEntry.ID_BOOK + " = " + book.getID(),
                null,
                null,
                null,
                null,
                null);
        int ret = c.getCount();
        c.close();
        return ret;
    }

    /**
     * Questo metodo elimina il legame tra un libro e una libreria
     * @param helper l'oggetto per la connessione al database
     * @param book il libro sotto esame
     * @param library la libreria sotto esame
     * @return true se il libro viene rimosso dalla libreria, false altrimenti
     */
    public static boolean deleteBookLibraryRelation(DbHelper helper, Book book, Library library){
        if(!existLibraryBookRelation(helper, book, library)){
            return false;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DbContract.BookListEntry.TABLE_NAME,
                DbContract.BookListEntry.ID_BOOK + " = " + book.getID() + " AND " +
                        DbContract.BookListEntry.ID_LIST + " = " + library.getId(),
                null);
        return true;
    }

    /**
     *  Questo metodo elimina una libreria dal databaee
     * @param helper  l'oggetto per la connessione al database
     * @param library la libreria da eliminare
     * @return true se l'eliminazione va a buon fine, false altrimenti
     */
    public static boolean deleteLibrary(DbHelper helper, Library library){
        if(!existLibray(helper, library)){
            return false;
        }
        SQLiteDatabase db = helper.getWritableDatabase();

        ArrayList<Book> listaLibri = getBookByLibray(helper, library);

        if(listaLibri != null) {
            for (int i = 0; i < listaLibri.size(); i++) {
                deleteBookLibraryRelation(helper, listaLibri.get(i), library);
                int numReletion = allBookLibraryRelation(helper, listaLibri.get(i));
                if (numReletion == 0) {
                    deleteBook(helper, listaLibri.get(i));
                }
            }
        }

        db = helper.getWritableDatabase();
        db.delete(DbContract.ListEntry.TABLE_NAME,
                DbContract.ListEntry._ID + " = " + Long.toString(library.getId()),
                null);
        db.close();
        return true;
    }

    /**
     * Questo metodo ritorna il numero di libri appartenenti ad una libreria
     * @param helper l'oggetto per la connessione al database
     * @param library la libreria sotto esame
     * @return il numero di libri nella libreria passata come parametro
     */
    public static int getNumbOfBookByLibrary(DbHelper helper, Library library){
        if(!existLibray(helper, library)){
            return -1;
        }
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.BookListEntry.TABLE_NAME,
                null,
                DbContract.BookListEntry.ID_LIST + " = " + library.getId(),
                null,
                null,
                null,
                null,
                null);
        int ret = c.getCount();
        c.close();
        return ret;
    }
}
