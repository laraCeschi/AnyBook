package it.unitn.disi.anybook.APIHandler;

import android.os.AsyncTask;

import it.unitn.disi.anybook.data.Author;
import it.unitn.disi.anybook.data.Book;
import it.unitn.disi.anybook.data.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Questa classe gestisce le API di google books, costruendo gli url e parsando le risposte
 */

public class APIgoogleBooks {

    private final static String BOOK_BASE_URL =
            "https://www.googleapis.com/books/v1/volumes?";

    private final static String PARAM_QUERY = "q";

    /**
     * Questo metodo costruisce un URL basandosi sul codice ISBN ottenuto come parametro, ed esegue
     * la query alle Google Books API
     *
     * @param ISBN il codice ISBN del libro che si vuole ottenere
     */
    public String makeBookSearchQuery(String ISBN) {
        HashMap<String, String> params = new HashMap<>();
        params.put(PARAM_QUERY, ISBN);

        try {
            URL bookSearchURL = NetworkUtils.buildURL(BOOK_BASE_URL, params);
            System.out.println(bookSearchURL);
            return new BookQueryTask().execute(bookSearchURL).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Questo metodo costruisce un URL basandosi sull'autore ottenuto come parametro, ed esegue
     * la query alle Google Books API
     *
     * @param AuthorName l'autore dei libri che si vogliono ottenere
     */
    public ArrayList<Book> makeBookListSearchQuery(String AuthorName) {
        String authorToken = AuthorName.replace(" ", "%20");
        try {
            URL booklistSearchURL = new URL("https://www.googleapis.com/books/v1/volumes?q=inauthor:" + authorToken);
            System.out.println(booklistSearchURL);
            String json = new BookQueryTask().execute(booklistSearchURL).get();
            return parseMaxNJSON(json, 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Questo metodo parsa una stringa formattata come il JSON di un Volume (vedi le Google Books API)
     * e restituisce il corrispondente oggetto Book
     *
     * @param response la stringa ottenuta dalla query alle google books API
     * @return un oggetto Book che rappresenta il JSON, null se la query ha fallito
     * @throws JSONException se qualcosa va storto con il JSON
     */
    public Book parseJSON(String response) throws JSONException {
        ArrayList<Book> answer = parseMaxNJSON(response, 1);
        Book returnable = null;
        if (answer != null && answer.size() > 0) {
            returnable = answer.get(0);
        }
        return returnable;
    }

    /**
     * Questo metodo parsa una stringa formattata come il JSON di una lista di Volumi (vedi le Google Books API)
     * e restituisce il corrispondente oggetto Book
     *
     * @param response   la stringa ottenuta dalla query alle google books API
     * @param maxResults il numero massimo di libri da restituire
     * @return un oggetto Book che rappresenta il JSON, null se la query ha fallito
     * @throws JSONException se qualcosa va storto con il JSON
     */
    public ArrayList<Book> parseMaxNJSON(String response, int maxResults) throws JSONException {
        ArrayList<Book> JSONsBook = null;
        JSONObject json = new JSONObject(response);
        boolean isErroneousState = false;

        try {
            json.getJSONArray("items").getJSONObject(0);
        } catch (JSONException e) {
            isErroneousState = true;
        }


        if (!isErroneousState) {
            JSONArray results = json.getJSONArray("items");
            int max;
            if (results.length() < maxResults) {
                max = results.length();
            } else {
                max = maxResults;
            }
            JSONsBook = new ArrayList<>();
            JSONObject currentJSONBook;
            Book currentBook;

            for (int i = 0; i < max; i++) {
                currentJSONBook = results.getJSONObject(i);
                currentBook = parseOneJSONBook(currentJSONBook);
                if (currentBook != null) {
                    JSONsBook.add(currentBook);
                }
            }
        }
        return JSONsBook;
    }

    /**
     * Questo metodo effettua il parsing di un solo libro
     * @param jsonBook il json corrispondente al libro
     * @return il libro corrispondente al json
     * @throws JSONException se si verificano errori durante il parsing del json
     */
    private Book parseOneJSONBook(JSONObject jsonBook) throws JSONException {
        Book JSONsBook;
        long ISBN = -1;
        String title, publisher, description, image_thumb, buyLink, shareLink, id;
        boolean saleability;
        ArrayList<Author> authors = new ArrayList<>();
        ArrayList<Category> categories = new ArrayList<>();

        // self link per condivisione
        try {
            id = jsonBook.getString("id");
            shareLink = "https://books.google.it/books?id=" + id;
        } catch (Exception e) {
            shareLink = null;
        }

        JSONObject firstItemVolumeInfo = jsonBook.getJSONObject("volumeInfo");

        // ISBN
        JSONArray identifiers = firstItemVolumeInfo.getJSONArray("industryIdentifiers");
        JSONObject identifier;
        String identifierType;
        boolean isStringsEqual;
        for (int i = 0; i < identifiers.length(); i++) {
            identifier = identifiers.getJSONObject(i);
            identifierType = identifier.getString("type");
            isStringsEqual = identifierType.equalsIgnoreCase("ISBN_13");
            if (isStringsEqual) {
                ISBN = identifier.getLong("identifier");
                break;
            }
        }

        //title
        try {
            title = firstItemVolumeInfo.getString("title");
        } catch (Exception e) {
            title = null;
        }

        // publisher
        try {
            publisher = firstItemVolumeInfo.getString("publisher");
        } catch (Exception e) {
            publisher = null;
        }

        // description
        try {
            description = firstItemVolumeInfo.getString("description");
        } catch (Exception e) {
            description = null;
        }


        // image
        try {
            JSONObject images = firstItemVolumeInfo.getJSONObject("imageLinks");
            image_thumb = images.getString("thumbnail");
        } catch (Exception e) {
            image_thumb = null;
        }


        // authors
        try {
            JSONArray authorsList = firstItemVolumeInfo.getJSONArray("authors");
            String authorName;
            for (int i = 0; i < authorsList.length(); i++) {
                authorName = authorsList.getString(i);
                authors.add(new Author(authorName));
            }
        } catch (Exception e) {
            authors = null;
        }

        //categories
        try {
            JSONArray categoriesList = firstItemVolumeInfo.getJSONArray("categories");
            String categoryName;
            for (int i = 0; i < categoriesList.length(); i++) {
                categoryName = categoriesList.getString(i);
                categories.add(new Category(categoryName));
            }
        } catch (Exception e) {
            categories = null;
        }

        try {
            JSONObject firstItemSaleInfo = jsonBook.getJSONObject("saleInfo");
            try {
                // saleability
                String saleable = firstItemSaleInfo.getString("saleability");
                switch (saleable) {
                    case "NOT_FOR_SALE":
                        saleability = false;
                        break;
                    case "FREE":
                        saleability = true;
                        break;
                    default:
                        saleability = true;
                        break;
                }
            } catch (Exception e) {
                saleability = false;
            }
            try {
                // link for buying
                buyLink = firstItemSaleInfo.getString("buyLink");
            } catch (Exception e) {
                buyLink = null;
            }

        } catch (Exception e) {
            saleability = false;
            buyLink = null;
        }


        JSONsBook = new Book(
                ISBN,
                title,
                publisher,
                description,
                -1,
                -1,
                image_thumb,
                saleability,
                buyLink,
                "",
                shareLink
        );
        JSONsBook.setAuthors(authors);
        JSONsBook.setCategories(categories);

        return JSONsBook;
    }

    /**
     * Questo async task permette di effettuare le operazioni network in maniera asincrona
     */
    private class BookQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String bookSearchResult = null;
            try {
                bookSearchResult = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bookSearchResult;
        }

        @Override
        protected void onPostExecute(String bookSearchResult) {

        }
    }
}

