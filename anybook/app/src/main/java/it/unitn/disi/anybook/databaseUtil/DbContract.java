package it.unitn.disi.anybook.databaseUtil;

import android.provider.BaseColumns;

/**
 * Questa è la raccolta di classi che definiscono le colonne delle tabelle del database
 */

public class DbContract {
    /**
     * Questa classe definisce la tabella "book"
     */
    public static final class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "book";
        public static final String ISBN = "isbn";
        public static final String TITLE = "name";
        public static final String PUBLISHER = "publisher";
        public static final String DESCRIPTION = "description";
        public static final String RATING = "rating";
        public static final String RATINGCOUNT = "ratingcount";
        public static final String IMAGE_THUMB = "image_thumb";
        public static final String SALEABILITY = "saleability";
        public static final String LINK_FOR_SALE = "link_for_sale";
        public static final String IFRAME = "i_frame";
        public static final String LINK_FOR_SHARE = "link_for_share";
    }

    /**
     * Questa classe definisce la tabella "author"
     */
    public static final class AuthorEntry implements BaseColumns{
        public static final String TABLE_NAME = "author";
        public static final String NAME = "name";
    }

    /**
     * Questa classe definisce la tabella "category"
     */
    public static final class CategoryEntry implements BaseColumns{
        public static final String TABLE_NAME = "category";
        public static final String NAME = "name";
    }

    /**
     * Questa classe definisce la tabella "list"
     */
    public static final class ListEntry implements BaseColumns{
        public static final String TABLE_NAME = "list";
        public static final String NAME = "name";
    }

    /**
     * Questa classe definisce la tabella "book_author"
     */
    public static final class BookAuthorEntry implements BaseColumns{
        public static final String TABLE_NAME = "book_author";
        public static final String ID_AUTHOR = "id_author";
        public static final String ID_BOOK = "id_book";
    }

    /**
     * Questa classe definisce la tabella "book_category"
     */
    public static final class BookCategoryrEntry implements BaseColumns{
        public static final String TABLE_NAME = "book_category";
        public static final String ID_CATEGORY = "id_category";
        public static final String ID_BOOK = "id_book";
    }

    /**
     * Questa classe definisce la tabella "book_list"
     */
    public static final class BookListEntry implements BaseColumns{
        public static final String TABLE_NAME = "book_list";
        public static final String ID_LIST = "id_list";
        public static final String ID_BOOK = "id_book";
    }
}
