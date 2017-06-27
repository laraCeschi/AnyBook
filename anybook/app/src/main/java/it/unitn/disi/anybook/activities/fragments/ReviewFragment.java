
package it.unitn.disi.anybook.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import it.unitn.disi.anybook.APIHandler.NetworkUtils;
import it.unitn.disi.anybook.R;

/**
 * Questa classe rappresenta il Fragment che viene utilizzato nella schermata di presentazione delle
 * recensioni legate ad un libro.
 */
public class ReviewFragment extends Fragment {

    protected String iFrameString;

    /**
     * Questo metodo costruisce un ReviewFragment utilizzando il parametro
     * @param html l'iFrame con cui costruire il ReviewFragment
     * @return il ReviewFragment costruito
     */
    public static ReviewFragment newInstanceReviewFragment(String html){
        ReviewFragment r = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString("html", html);
        r.setArguments(args);
        return r;
    }


    /**
     * Questo metodo crea il Fragment e inizializza il dataset della RecycleView ospitata
     * @param savedInstanceState contiene i dati più recenti forniti a onSaveInstanceState().
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        String html = args.getString("html", "");
        if(!html.equals("")){
            iFrameString = html;
        }
    }

    /**
     *Questo metodo istanzia la grafica del Fragment.
     * @param inflater il LayoutInflater che viene utilizzato per "gonfiare" una view in un Fragment
     * @param container la view gerarchicamente superiore in cui va inserito il Fragment
     * @param savedInstanceState l'eventuale stato precedente del Fragment
     * @return la View della grafica del Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.review_fragment, container, false);

        WebView webView = (WebView) rootView.findViewById(R.id.web_view);
        System.out.println(iFrameString);
        if(iFrameString != null && NetworkUtils.checkConnection(getActivity())){
            webView.loadUrl(iFrameString);
        }
        else{
            TextView error = (TextView) rootView.findViewById(R.id.review_not_found);
            error.setVisibility(View.VISIBLE);
            webView.setVisibility(View.INVISIBLE);
        }
        return rootView;
    }

}