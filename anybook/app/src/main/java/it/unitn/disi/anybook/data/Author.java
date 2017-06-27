package it.unitn.disi.anybook.data;

/**
 * Questa classe gestisce gli autori dei libri
 */

public class Author {
    private long db_id;
    private String name;

    /**
     * Questo metodo costuisce un Author
     * @param id l'id dell'autore all'interno del databse
     * @param nome il nome dell'autore
     */
    public Author(int id, String nome){
        this.db_id = id;
        this.name = nome;
    }

    /**
     * Questo metodo costruisce un Author
     * @param name il nome dell'autore
     */
    public Author(String name){
        this.name = name;
    }

    /**
     * Questo metodo ritorna l'id dell'autore
     * @return l'id dell'autore
     */
    public long getDb_id() {
        return db_id;
    }

    /**
     * Questo metodo imposta l'id di un autore
     * @param db_id l'id dell'autore
     */
    public void setDb_id(long db_id) {
        this.db_id = db_id;
    }

    /**
     * Questo metodo ritorna il nome dell'autore
     * @return il nome dell'autore
     */
    public String getName() {
        return name;
    }

    /**
     * Questo metodo imposta il nome dell'autore
     * @param name il nome dell'autore
     */
    public void setName(String name) {
        this.name = name;
    }

}
