package it.polimi.ma.group07.briscola.controller.persistance;

/**
 * Created by amari on 03-Dec-17.
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
    public String state;

    public OnlineGame(String state){
        this.state=state;
    }
}
