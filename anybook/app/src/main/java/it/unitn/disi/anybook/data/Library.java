package it.unitn.disi.anybook.data;

/**
 * Questa classe gestisce le librerie
 */

public class Library {

    private String name;
    private long id;

    /**
     * Questo metodo costruisce una Library
     * @param id l'id della library
     * @param name il nome della library
     */
    public Library(long id, String name){
        this.id = id;
        this.name = name;
    }

    /**
     * Questo metodo costuisce una Library
     * @param name il nome della library
     */
    public Library(String name){
        this.name = name;
    }

    /**
     * Questo metodo imposta il nome della library
     * @param name il nome della library
     */
    public void setName(String name) { this.name = name; }

    /**
     * Questo metodo ritorna il nome della library
     * @return il nome della library
     */
    public String getName() { return this.name; }

    /** Questo metodo imposta l'id della library
     *
     * @param id l'id della library
     */
    public void setId(long id) { this.id = id; }

    /**
     * Questo metodo ritorna l'id della library
     * @return l'id della library
     */
    public long getId() { return this.id; }

}
