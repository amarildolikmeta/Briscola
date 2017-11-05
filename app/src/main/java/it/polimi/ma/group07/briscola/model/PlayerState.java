package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

/**
 * Represents a visible state of the player
 * Thought to be used by the AI
 */

public class PlayerState {
    public ArrayList<Card> hand ;
    public ArrayList<Card> surface;
    public ArrayList<Card> ownPile;
    public ArrayList<ArrayList<Card>> opponentPiles;
    public Card briscola;

    public PlayerState(ArrayList<Card> hand,ArrayList<Card> surface,ArrayList<Card> ownPile,
                       ArrayList<ArrayList<Card>> opponentPile,Card briscola){
        this.hand=hand;
        this.surface=surface;
        this.ownPile=ownPile;
        this.opponentPiles=opponentPile;
        this.briscola=briscola;
    }
}
