package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

/**
 * Created by amari on 18-Oct-17.
 */

public class Briscola {
    private ArrayList<Player> players;
    private Deck deck;
    private int round=0;
    private Suit briscola;
    private Brain brain;
    public Briscola(int numPlayers)
    {
        deck=new Deck();
        brain=new Brain();
        for(int i=0;i<numPlayers;i++)
            players.add(new Player("P"+(i+1)));
        round=1;
        for(int i=0;i<3;i++){
            for(int j=0;j<numPlayers;j++) {
                players.get(j).addCardToHand(deck.drawCard());
            }
        }
        Card b=deck.drawCard();
        briscola=b.getSuit();
    }

    public Briscola(String description){
        //TODO initialize game from description String
    }
    public void resetGame(){
        deck=new Deck();
        round=1;
        for(int i=0;i<players.size();i++)
            players.get(i).reset();
    }

}
