package it.unitn.disi.anybook.data;

/**
 * Questa classe gestisce le Reviews
 */

public class Reviews {
    String HTMLiFrame;
    int ratingCount;
    float avgRating;

    final static String delimiter = "aaaaaaaaaaaaaaa:";

    /**
     * Questo metodo costuisce una Reviews
     * @param html il codice html delle recensioni
     * @param rateCount il conteggio dei voti
     * @param rating il voto medio
     */
    public Reviews(String html, int rateCount, float rating) {
        if (html != null && html.contains("<style>")) {
            HTMLiFrame = cleanHTMLSource(html);
        } else {
            HTMLiFrame = html;
        }
        ratingCount = rateCount;
        avgRating = rating;
    }

    /**
     * Questo metodo restituisce l'iFrame della review
     *
     * @return l'indirizzo web dell'iFrame
     */
    public String getHTMLiFrame() {
        return HTMLiFrame;
    }

    /**
     * Questo metodo setta l'iFrame della review
     *
     * @param HTMLiFrame l'indirizzo web dell'iFrame
     */
    public void setHTMLiFrame(String HTMLiFrame) {
        this.HTMLiFrame = HTMLiFrame;
    }

    /**
     * Questo metodo restituisce il conteggio dei voti nella review
     *
     * @return il conteggio dei voti
     */
    public int getRatingCount() {
        return ratingCount;
    }

    /**
     * questo metood imposta il conteggio dei voti nella review
     *
     * @param ratingCount il coneggio dei voti
     */
    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    /**
     * Questo metodo ritorna il voto medio della review
     *
     * @return il voto medio
     */
    public float getAvgRating() {
        return avgRating;
    }

    /**
     * Questo metodo imposta il voto medio della review
     *
     * @param avgRating il voto medio
     */
    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    /**
     * Questo metodo ritorna una stringa rappresentante tutti gli elementi della review separati da un
     * opportuno delimitatore
     *
     * @return la stringa rappresentante la review
     */
    @Override
    public String toString() {
        return HTMLiFrame + delimiter + ratingCount + delimiter + avgRating;
    }

    /**
     * Questo metodo parsa una stringa cercando di dividerla utilizzando il delimitatore e restituisce
     * una review, se la stringa era ben formata e null altrimenti
     *
     * @param string la stringa (possibilmente ottenuta con il metodo .toString()) rappresentante la review
     * @return la review rappresentata dalla stringa
     */
    public static Reviews valueOf(String string) {
        Reviews reviews = null;
        if (string != null) {
            String[] stringVal = string.split(delimiter, -1);
            try {
                reviews = new Reviews(stringVal[0], Integer.valueOf(stringVal[1]), Float.valueOf(stringVal[2]));
            } catch (Exception e) {
				e.printStackTrace();
            }
        }
        return reviews;
    }

    /**
     * Questo metodo pulisce il codice html ottenuto tramite le api di Goodreads per isolare l'indirizzo
     * dell'iFrame
     *
     * @param html il codice "sporco" contenente l'iFrame e altre informazioni
     * @return l'indirizzo dell'iFrame
     */
    public String cleanHTMLSource(String html) {
        String address = null;
        String[] firstPartition = html.split("src=\"");
        String[] secondPartition;
        if (firstPartition.length == 2) {
            secondPartition = firstPartition[1].split("\"");
            if (secondPartition.length > 0) {
                address = secondPartition[0];
            }
        }
        return address;
    }
}
