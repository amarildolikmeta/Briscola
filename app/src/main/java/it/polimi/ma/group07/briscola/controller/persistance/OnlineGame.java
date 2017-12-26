package it.polimi.ma.group07.briscola.controller.persistance;

/**
 * Object Representation of an online game
 */

public class OnlineGame {
    /**
     *  Terminated :Game terminated before finished
     *  Won
     *  Drawn
     *  Lost
     */
    public static final String TERMINATED="Terminated";
    public static final String WON="Won";
    public static final String LOST="Lost";
    public static final String DRAWN="Drawn";
    /**
     * State of the game
     */
    public String state;

    public OnlineGame(String state){
        this.state=state;
    }
}
