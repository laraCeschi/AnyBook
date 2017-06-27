package it.unitn.disi.anybook.APIHandler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Questa classe gestisce le operazioni relative alla connessione internet, ciò effettua le richieste,
 * ottiene le risposte e verifica il funzionamento della rete
 */

public class NetworkUtils {

    /**
     * Questo metodo costruisce un URL ben formato partendo da un URL di base e da una serie di parametri.
     *
     * @param baseURL l'URL di base
     * @param params i parametri (chiave, valore) da aggiungere all'URL
     * @return l'URL completo e ben formato
     */
    static URL buildURL (String baseURL, HashMap<String,String> params)
    {Uri builtUri = Uri.parse(baseURL);
        System.out.println(builtUri);

        for(String key : params.keySet()){
            builtUri = builtUri.buildUpon()
                    .appendQueryParameter(key, params.get(key))
                    .build();
        }

        System.out.println(builtUri);
        URL builtURL = null;

        try {
            builtURL = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        System.out.println(builtURL);
        return builtURL;
    }

    /**
     * Questo metodo restituisce la risposta ad una richiesta Httpl
     *
     * @param url l'URL da cui ottenere risposta
     * @return la risposta alla query
     * @throws IOException se avviene una IO exception durante l'apertura della connessione o
     * la creazione dello stream di input
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Questo metodo controlla l'effettiva esistenza di una connessione a internet
     * @param context il contesto da cui cercare la connessione
     * @return true se la connessione esiste, false altrimenti
     */
    public static boolean checkConnection(Context context) {
        ConnectivityManager connect = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connect != null)
        {
            NetworkInfo[] information = connect.getAllNetworkInfo();
            if (information != null)
                for (NetworkInfo info : information) {
                    if (info.getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
        }
        return false;
    }


}
