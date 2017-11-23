package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

/**
 * Represents a visible state of the player
 * Thought to be used by the AI
 */

public class PlayerState {
    public ArrayList<String> hand ;
    public ArrayList<String> surface;
    public ArrayList<String> ownPile;
    public ArrayList<ArrayList<String>> opponentPiles;
    public String briscola;
    public int currentPlayer;
    public int deckSize;
    public int[] opponentHandSize;
    public boolean playableState;
    public PlayerState(ArrayList<String> hand,ArrayList<String> surface,ArrayList<String> ownPile,
                       ArrayList<ArrayList<String>> opponentPile,Card briscola,int currentPlayer,
                       int deckSize,int[] opponentHandSize,boolean playableState){
        this.hand=hand;
        this.surface=surface;
        this.ownPile=ownPile;
        this.opponentPiles=opponentPile;
        this.briscola=briscola.toString();
        this.currentPlayer=currentPlayer;
        this.deckSize=deckSize;
        this.opponentHandSize=opponentHandSize;
        this.playableState=playableState;
    }
}
