package it.unitn.disi.anybook.dataHandler;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import it.unitn.disi.anybook.data.Book;
import it.unitn.disi.anybook.data.Category;
import it.unitn.disi.anybook.databaseUtil.DbContract;
import it.unitn.disi.anybook.databaseUtil.DbHelper;

/**
 * Questa classe gestisce le query al database per i generi (Category)
 */

public class CategoryHandler {

    /**
     * funzione per controllare l'esistenza di una categoria in database
     * @param helper l'oggetto per la conessione al database
     * @param category l'oggetto categoria da controllarne l'esistenza
     * @return ritorna true se esiste in database, false altrimenti
     */
    public static boolean existCategory(DbHelper helper, Category category){
        boolean exist = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.CategoryEntry.TABLE_NAME,
                null,
                DbContract.CategoryEntry._ID + " = " + category.getID(),
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
     * funzione per controllare l'esistenza di una categoria in database
     * @param helper l'oggetto per la conessione al database
     * @param id l'id della categoria da cercare in database
     * @return ritorna true se esiste in database, false altrimenti
     */
    public static boolean existCategory(DbHelper helper, long id){
        boolean exist = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.BookCategoryrEntry.TABLE_NAME,
                null,
                DbContract.CategoryEntry._ID + " = " + id,
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
     * funzione per controllare l'esistenza di una categoria in database
     * @param helper l'oggetto per la conessione al database
     * @param name il nome della categoria da ricercarne l'esistenza
     * @return ritorna true se esiste in database, false altrimenti
     */
    public static boolean existCategory(DbHelper helper, String name){
        boolean exist = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.CategoryEntry.TABLE_NAME,
                null,
                DbContract.CategoryEntry.NAME + " = ' " + name + " '",
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
     * funzione per controllare l'esistenza della relazione tra libro e categoria
     * @param dbHelper l'oggetto per la conessione al database
     * @param book l'oggetto libro da controllare
     * @param category l'oggetto categoria da controllare
     * @return ritorna true se esiste la entry in database, false altrimenti
     */
    public static boolean existBookCategoryRelation(DbHelper dbHelper, Book book, Category category){
        if(!existCategory(dbHelper,category)){
            return true;
        }
        if(!BookHandler.existBook(dbHelper, book.getISBN())){
            return true;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DbContract.BookCategoryrEntry.TABLE_NAME,
                null,
                DbContract.BookCategoryrEntry.ID_BOOK + " = " + book.getID() + " AND " +
                        DbContract.BookCategoryrEntry.ID_CATEGORY + " = " + category.getID(),
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
     * funzione per aggiungere la ralazione tra libro e categoria
     * @param dbHelper l'oggetto per la conessione al database
     * @param book l'oggetto libro
     * @param category l'oggetto categoria
     * @return ritorna true se l'inserimento è riuscito, false altrimenti, protrebbe significare anche la preesistenza della entry  in database
     */
    public static boolean addCategoryToBook(DbHelper dbHelper, Book book, Category category){
        if(!existCategory(dbHelper,category)){
            return false;
        }
        if(!BookHandler.existBook(dbHelper, book.getISBN())){
            return false;
        }
        if(existBookCategoryRelation(dbHelper, book, category)){
            return false;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbContract.BookCategoryrEntry.ID_BOOK, book.getID());
        cv.put(DbContract.BookCategoryrEntry.ID_CATEGORY, category.getID());
        long id = db.insert(DbContract.BookCategoryrEntry.TABLE_NAME, null, cv);
        if(id >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Questo metodo restituisce una categoria dato il suo nome
     * @param helper l'oggetto per la connessione al database
     * @param name il nome della categoria da cercare
     * @return la Category trovata, null se non esiste
     */
    public static Category getCategoryByName(DbHelper helper, String name){
        Category category = null;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.query(DbContract.CategoryEntry.TABLE_NAME,
                null,
                DbContract.CategoryEntry.NAME + " = '" + name + "' ",
                null,
                null,
                null,
                null,
                null);
        c.moveToFirst();
        while(c.isAfterLast()){
            category = new Category(c.getLong(c.getColumnIndex(DbContract.CategoryEntry._ID)),
                    c.getString(c.getColumnIndex(DbContract.CategoryEntry.NAME)));
        }
        c.close();
        db.close();
        return category;
    }


    /**
     * aggiunge una nuova categoria in database
     * @param helper l'oggetto per la conessione al database
     * @param category l'oggetto categoria da inserire
     * @return ritorna true se l'inserimento è riuscito, false altrimenti, protrebbe significare anche la preesistenza della entry  in database
     */
    public static boolean addCategory(DbHelper helper, Category category){
        if(existCategory(helper, category.getName())){
            return false;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbContract.CategoryEntry.NAME, category.getName());
        long id;
        id = db.insert(DbContract.CategoryEntry.TABLE_NAME, null, cv);
        if(id >= 0){
            category.setID(id);
        }
        else {
            return false;
        }
        return true;
    }
}
