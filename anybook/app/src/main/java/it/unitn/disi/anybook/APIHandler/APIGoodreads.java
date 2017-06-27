package it.unitn.disi.anybook.APIHandler;

import android.os.AsyncTask;
import android.util.Xml;

import it.unitn.disi.anybook.data.Reviews;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;

/**
 * Questa classe gestisce le API di goodreads, costruendo gli url e parsando i risultati
 */

public class APIGoodreads {
    private String keyVal = "5QIjr6omSGEKQCjL2ZJQnQ";
  //  private String secret = "klbaPoKuS1p4GcxdmF5tETLdZLpAamxnoKf4UI";

    private String baseUrlISBN = "https://www.goodreads.com/book/isbn/";
    private String baseUrlSEARCH = "https://www.goodreads.com/book/title.xml";
    private String formatKey = "format";
    private String formatVal = "xml";
    private String keyKey = "key";
    private String titleKey ="title";
    private String authorKey = "author";

    private String reviewTag = "reviews_widget";
    private String ratingCountTag = "ratings_count";
    private String avgRatingTag = "average_rating";
    private String bookTag = "book";
    private  String errorTag = "error";


    /**
     * Questo metodo costruisce un URL basandosi sul codice ISBN ottenuto come parametro, ed esegue
     * la query alle API di goodreads
     *
     * @param ISBN il codice ISBN del libro che si vuole ottenere
     */
    public Reviews makeReviewSearchQuery(String ISBN) {
        baseUrlISBN = baseUrlISBN.concat(ISBN);

        HashMap<String, String> params = new HashMap<>();
        params.put(formatKey, formatVal);
        params.put(keyKey, keyVal);
        Reviews reviews = null;

        try {
            URL reviewSearchURL = NetworkUtils.buildURL(baseUrlISBN, params);
            String reviewString = new ReviewQueryTask().execute(reviewSearchURL).get();
            reviews = Reviews.valueOf(reviewString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }

    /**
     * Questo metodo costruisce un URL basandosi sul titolo e sugli autori ottenuti come parametro, ed esegue
     * la query alle API di goodreads
     *
     * @param title il titolo del libro che si vuole ottenere
     * @param author un autore del libro che si vuole ottenere
     */
    public Reviews makeReviewSearchQuery(String title, String author) {
        HashMap<String, String> params = new HashMap<>();
        params.put(titleKey, title);
        params.put(keyKey, keyVal);
        params.put(authorKey, author);
        Reviews reviews = null;

        try {
            URL reviewSearchURL = NetworkUtils.buildURL(baseUrlSEARCH, params);
            String reviewString = new ReviewQueryTask().execute(reviewSearchURL).get();
            reviews = Reviews.valueOf(reviewString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }

    /**
     * Questo metodo avvia il parsing dell'XML risultato dalla richiesta alle API di goodreads
     * @param in il reader contenente i dati
     * @return la Review scritta nell'XML
     * @throws XmlPullParserException se si verificano errori durante il parsing
     * @throws IOException se si verificano errori durante la lettura dell'XML
     */
    public Reviews parse(Reader in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in);
            parser.nextTag();
            Reviews reviews = null;
            while (parser.next()!= XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals(bookTag)) {
                    reviews = readHTML(parser);
                    break;
                }
                else if (name.equals(errorTag)) {
                    break;
                }
                else {
                    skip(parser);
                }
            }
            return reviews;
        }
        catch (Exception e){
            in.close();
            e.printStackTrace();
            return null;
        }
        finally {
            in.close();
        }
    }


    /**
     * Questo metodo parsa il contenuto di un file xml. Se incontra un iFrame di recensioni o il voto
     * medio o il numero di review le parsa, altrimenti skippa il tag
     * @param parser il parser contenente l'xml
     * @return la Review rappresentata dall'xml
     * @throws XmlPullParserException se si verifica un errore durante il parsing
     * @throws IOException se si verifica un errore durante la lettura dell'XML
     */
    private Reviews readHTML(XmlPullParser parser) throws XmlPullParserException, IOException {
         parser.require(XmlPullParser.START_TAG, null, bookTag);

        String html = null;
        int ratingCount = 0;
        float ratingAvg = -1;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(reviewTag)) {
                html = readText(parser);
                parser.require(XmlPullParser.END_TAG, null, reviewTag);
                break;
            } else if (name.equals(ratingCountTag)) {
                ratingCount = Integer.valueOf(readText(parser));
                parser.require(XmlPullParser.END_TAG, null, ratingCountTag);
            } else if (name.equals(avgRatingTag) && ratingAvg==-1) {
                ratingAvg = Float.valueOf(readText(parser));
                parser.require(XmlPullParser.END_TAG, null, avgRatingTag);
            } else {
                skip(parser);
            }
        }
        return new Reviews(html, ratingCount, ratingAvg);
          }



    /**
     * Questo metodo estrae il testo dall'attuale tag del parser xml indicato
     * @param parser il parser contenente l'xml
     * @return il contenuto del tag testuale
     * @throws IOException se si verifica un errore durante la lettura dell'XML
     * @throws XmlPullParserException se si verifica un errore durante il parsing
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * Questo metodo serve per skippare i tag che non ci interessano nell'xml
     * @param parser il parser contenente l'xml
     * @throws XmlPullParserException se si verifica un errore durante il parsing
     * @throws IOException se si verifica un errore durante la lettura dell'XML
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    /**
     * Questo async task performa le operazioni network in maniera asincrona
     */
    private class ReviewQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String resultString;
            StringReader resultStream;
            Reviews review;
            String reviewSearchResult = null;
            try {
                resultString = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                if (resultString != null) {
                    resultStream = new StringReader(resultString);
                    review = parse(resultStream);
                    reviewSearchResult = review.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return reviewSearchResult;
        }

        @Override
        protected void onPostExecute(String bookSearchResult) {

        }
    }

}
