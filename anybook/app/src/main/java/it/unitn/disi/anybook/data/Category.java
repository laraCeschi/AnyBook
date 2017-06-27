package it.unitn.disi.anybook.data;

/**
 * Questa classe gestisce i generi di un libro
 */

public class Category {

    private long ID;
    private String name;

    /**
     * Questo metodo costuisce una Category
     * @param ID l'id del genere di libro nel database
     * @param name il nome del genere di libro
     */
    public Category(long ID, String name){
        this.ID = ID;
        this.name = name;
    }

    /**
     * Questo metodo costuisce una Category
     * @param name il nome del genere di libro
     */
    public Category(String name){
        this.name = name;
    }

    /**
     * Questo metodo imposta l'id del genere
     * @param id l'id del genere
     */
    public void setID(long id){
        this.ID = id;
    }

    /**
     * Questo metodo imposta il nome del genere
     * @param name il nome del genere
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Questo metodo ritorna l'ID del genere
     * @return l'id del genere
     */
    public long getID(){
        return this.ID;
    }

    /**
     * Questo metodo ritorna il nome del genere
     * @return il nome del genere
     */
    public String getName(){
        return this.name;
    }

}
