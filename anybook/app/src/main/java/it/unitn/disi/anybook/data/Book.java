package it.unitn.disi.anybook.data;

import java.util.ArrayList;

/**
 * Questa classe gestisce i libri
 */

public class Book {


    private long ISBN;
    private long ID;
    private String title;
    private String publisher;
    private String description;
    private int rating;
    private int ratingcount;
    private String image_thumb;
    private boolean saleability;
    private String link_for_sale;
    private ArrayList<Author> authors;
    private ArrayList<Category> categories;
    private String iFrame;
    private String link_for_share;


    /**
     * Questo metodo costuisce un Book
     * @param isbn il codice isbn del libro
     * @param id l'id del libro all'interno del database
     * @param title il titolo del libro
     */
    public Book(long isbn, long id, String title){
        this.ID = id;
        this.ISBN = isbn;
        this.title = title;
        this.publisher = "";
        description = "";
        rating = 0;
        ratingcount = 0;
        image_thumb = "";
        saleability = false;
        link_for_sale = "";
        iFrame = "";
        link_for_share = "";
        authors = new ArrayList<>();
        categories = new ArrayList<>();
    }

    /**
     * Questo metodo costuisce un Book
     * @param isbn il codice isbn del libro
     * @param id l'id del libro all'interno del database
     * @param title il titolo del libro
     * @param pu l'editore del libro
     * @param des la descrizione (della trama) del libro
     * @param rat il voto medio del libro
     * @param rat_c il conteggio dei voti del libro
     * @param im il link all'immagine di copertina del libro
     * @param sea true se il libro è vendibile sul Play Store, false altrimenti
     * @param link il link al play store per l'acquisto del libro, null se non esiste
     * @param iFrame il codice html delle recensioni del libro
     * @param link_for_share il link a google libri per la condivisione del libro
     */
    public Book(long isbn,
                long id,
                String title,
                String pu,
                String des,
                int rat,
                int rat_c,
                String im,
                boolean sea,
                String link,
                String iFrame,
                String link_for_share){
        this.ID = id;
        this.ISBN = isbn;
        this.title = title;
        publisher = pu;
        description = des;
        rating = rat;
        ratingcount = rat_c;
        image_thumb = im;
        saleability = sea;
        link_for_sale = link;
        this.iFrame = iFrame;
        this.link_for_share = link_for_share;
        authors = new ArrayList<>();
        categories = new ArrayList<>();
    }

    /**
     * Questo metodo costuisce un libro
     * @param isbn il codice isbn del libro
     * @param title il titolo del libro
     * @param pu l'editore del libro
     * @param des la descrizione (della trama) del libro
     * @param rat il voto medio del libro
     * @param rat_c il conteggio dei voti ottenuti dal libro
     * @param im il link all'immagine di copertina del libro
     * @param sea true se il libro è vendibile, false altrimenti
     * @param link il link al play store per l'acquisto del libro
     * @param iFrame il codice html delle recensioni del libro
     * @param link_for_share il link a google books per la condivisione del libro
     */
    public Book(long isbn,  String title,
                String pu, String des,
                int rat, int rat_c,
                String im,
                boolean sea,
                String link,
                String iFrame,
                String link_for_share){
        this.ISBN = isbn;
        this.title = title;
        publisher = pu;
        description = des;
        rating = rat;
        ratingcount = rat_c;
        image_thumb = im;
        saleability = sea;
        link_for_sale = link;
        this.ID=-1;
        this.iFrame = iFrame;
        this.link_for_share = link_for_share;
        authors = new ArrayList<>();
        categories = new ArrayList<>();
    }

    /**
     * Questo metodo costruisce un libro
     * @param isbn il codice isbn del libro
     * @param title il titolo del libro
     */
    public Book(long isbn, String title){
        this.ISBN = isbn;
        this.title = title;
        this.publisher = "";
        description = "";
        rating = 0;
        ratingcount = 0;
        image_thumb = "";
        saleability = false;
        link_for_sale = "";
        iFrame = "";
        link_for_share = "";
        authors = new ArrayList<>();
        categories = new ArrayList<>();
        this.ID=-1;
    }

    /**
     * Questo metodo ritorna l'isbn del libro
     * @return l'isbn del libro
     */
    public long getISBN() {
        return ISBN;
    }

    /**
     * Questo metodo imposta l'isbn del libro
     * @param ISBN l'isbn del libro
     */
    public void setISBN(long ISBN) {
        this.ISBN = ISBN;
    }

    /**
     * Questo metodo ritorna l'ID del libro
     * @return l'id del libro
     */
    public long getID() {
        return ID;
    }

    /**
     * Questo metodo imposta l'ID del libro
     * @param ID l'id del libro
     */
    public void setID(long ID) {
        this.ID = ID;
    }

    /**
     * Questo metodo ritorna il titolo del libro
     * @return il titolo del libro
     */
    public String getTitle() {
        return title;
    }

    /**
     * Questo metodo imposta il titolo del libro
     * @param title il titolo del libro
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Questo metodo ritorna l'editore del libro
     * @return l'editore del libro
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Questo metodo imposta l'editore del libro
     * @param pu l'editore del libro
     */
    public void setPublisher(String pu){
        publisher = pu;
    }

    /**
     * Questo metodo imposta la descrizione del libro
     * @return la descrizione del libro
     */
    public String getDescription() {
        return description;
    }

    /**
     * Questo metodo ritorna la descrizione del libro
     * @param description la descrizione del libro
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Questo metodo ritorna il voto medio del libro
     * @return il voto medio del libro
     */
    public int getRating() {
        return rating;
    }

    /**
     * Questo metodo imposta il voto medio del libro
     * @param rating il voto medio del libro
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Questo metodo conteggia le recensioni ottenute da un libro
     * @return le recensioni ottenute da un libro
     */
    public int getRatingcount() {
        return ratingcount;
    }

    /**
     * Questo metodo imposta le recensioni ottenute da un libro
     *  @param ratingcount le recensioni ottenute da un libro
     */
    public void setRatingcount(int ratingcount) {
        this.ratingcount = ratingcount;
    }

    /**
     * Questo metodo ritorna il link all'immagine di copertina del libro
     * @return  il link all'immagine di copertina del libro
     */
    public String getImage_thumb() {
        return image_thumb;
    }

    /**
     * Questo metodo imposta  il link all'immagine di copertina del libro
     * @param image_thumb  il link all'immagine di copertina del libro
     */
    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    /**
     * Questo metodo ritorna vero o falso in base a se un libro è acquistabile oppure no
     * @return vero se il libro è acquistabile, false altrimenti
     */
    public boolean isSaleability() {
        return saleability;
    }

    /**
     * Questo metodo imposta la disponibilità all'acquisto di un libro
     * @param saleability true se il libro è presente sul play store, false altrimenti
     */
    public void setSaleability(boolean saleability) {
        this.saleability = saleability;
    }

    /**
     * Questo metodo ritorna il link per l'acquisto del libro
     * @return il link per l'acquisto del libro
     */
    public String getLink_for_sale() {
        return link_for_sale;
    }

    /**
     * Questo metodo imposta il link per l'acquisto del libro
     * @param link_for_sale il link per l'acquisto del libro
     */
    public void setLink_for_sale(String link_for_sale) {
        this.link_for_sale = link_for_sale;
    }

    /**
     * Questo metodo imposta gli autori di un libro
     * @param list un ArrayList di Authors rappresentante gli autori di un libro
     */
    public void setAuthors(ArrayList<Author> list){ this.authors = list; }

    /**
     * Questo metodo ritorna gli autori del libro
     * @return gli autori del libro
     */
    public ArrayList<Author> getAuthors() { return this.authors; }

    /**
     * Questo metodo imposta i generi di un libro
     * @param list un ArrayList di Category rappresentante i generi di un libro
     */
    public void setCategories(ArrayList<Category> list) { this.categories = list; }

    /**
     * Questo metodo ritorna i generi di un libro
     * @return i generi di un libro
     */
    public ArrayList<Category> getCategories() { return this.categories; }

    /**
     * Questo metodo imposta il codice html delle recensioni del libro
     * @param iFrame il codice html delle recensioni del libro
     */
    public void setiFrame(String iFrame) { this.iFrame = iFrame; }

    /**
     * Questo metodo ritorna il codice html delle recensioni del libro
     * @return il codice html delle recensioni del libro
     */
    public String getiFrame() { return iFrame; }

    /**
     * Questo metodo imposta il link per la condivisione di un libro
     * @param link_for_share il link per la condivisione di un libro
     */
    public void setLink_for_share(String link_for_share) { this.link_for_share = link_for_share; }

    /**
     * Questo metodo ritorna il link per la condivisione di un libro
     * @return il link per la condivisione di un libro
     */
    public String getLink_for_share() { return link_for_share; }

    /**
     * Questo metodo imposta l'id di un libro
     * @param id l'id del libro
     * @return true
     */
    public boolean setId(long id){
        this.ID = id;
        return true;
    }

}
