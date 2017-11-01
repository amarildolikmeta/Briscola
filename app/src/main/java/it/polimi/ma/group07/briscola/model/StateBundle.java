package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

/**
 * Created by amari on 23-Oct-17.
 */

public class StateBundle {
    public ArrayList<String> hand1;
    public ArrayList<String> hand2;
    public ArrayList<String> pile1;
    public ArrayList<String> pile2;
    public int score1;
    public int score2;
    public ArrayList<String> surface;
    public String briscola;
    public int currentPlayer;
    public int deckSize;
    public boolean playableState;
    public StateBundle(ArrayList<String> hand1,ArrayList<String> hand2,ArrayList<String> surface,String briscola,int currentPlayer,
                       ArrayList<String> pile1,ArrayList<String> pile2,int score1,int score2,int deckSize,boolean playableState) {
        this.hand1 = hand1;
        this.briscola = briscola;
        this.hand2 = hand2;
        this.surface = surface;
        this.currentPlayer=currentPlayer;
        this.pile1=pile1;
        this.pile2=pile2;
        this.score1=score1;
        this.score2=score2;
        this.deckSize=deckSize;
        this.playableState=playableState;
    }
}
