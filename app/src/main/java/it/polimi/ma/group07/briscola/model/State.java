package it.polimi.ma.group07.briscola.model;

import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;

/**
 * Represents an object representing the different
 * fields of the game in String representation
 * Returned by the parser class after parsing a representation
 * {@link Parser}
 */

public class State {
    public  int currentPlayer;
    public String trump;
    public String deck;
    public String surface;
    public String[] hands;
    public String[] piles;

    public State(int currentPlayer, String trump, String deck, String surface, String[] hands, String[] piles) {
        this.currentPlayer=currentPlayer;
        this.trump=trump;
        this.deck=deck;
        this.surface=surface;
        this.hands=hands;
        this.piles=piles;
    }
}
