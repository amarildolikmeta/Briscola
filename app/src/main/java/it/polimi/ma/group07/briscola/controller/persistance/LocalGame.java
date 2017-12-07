package it.polimi.ma.group07.briscola.controller.persistance;

/**
 * Created by amari on 03-Dec-17.
 */

public class LocalGame {
    public static final String RUNNING="Running";
    public static final String WON="Won";
    public static final String LOST="Lost";
    public static final String DRAWN="Drawn";
    public String startConfiguration;
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
