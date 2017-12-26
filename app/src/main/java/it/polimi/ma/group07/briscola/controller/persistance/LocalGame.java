package it.polimi.ma.group07.briscola.controller.persistance;

/**
 * Object Representation of a local game
 */

public class LocalGame {
    public static final String TERMINATED="Terminated";
    public static final String WON="Won";
    public static final String LOST="Lost";
    public static final String DRAWN="Drawn";
    /**
     * starting configuration of a game
     */
    public String startConfiguration;
    /**
     * String representation of the moves performed
     * from the start of the game until the present
     */
    public String moves;
    /**
     *  Running :Game hasn't finished yet
     *  Won
     *  Drawn
     *  Lost
     */
    public String state;

    public LocalGame(String startConfiguration,String moves,String state){
        this.startConfiguration=startConfiguration;
        this.moves=moves;
        this.state=state;
    }
}
