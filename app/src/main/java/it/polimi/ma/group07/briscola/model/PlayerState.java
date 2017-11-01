package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

/**
 * Created by amari on 31-Oct-17.
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
