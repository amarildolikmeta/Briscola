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
    private int currentPlayer;
    private ArrayList<Card> surface;
    public Briscola(int numPlayers)
    {
        deck=new Deck();
        brain=new Brain();
        surface=new ArrayList<Card>();
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
        deck.addLastCard(b);
        currentPlayer= 0;
    }

    public Briscola(String description){
        //TODO initialize game from description String
    }

    public String surfaceToString(){
        String str="";
        for(Card c:surface){
            str+=c.toString();
        }
        return str;
    }

    public void resetGame(){
        deck=new Deck();
        round=1;
        for(int i=0;i<players.size();i++)
            players.get(i).reset();
    }
    public void onPerformMove()
    {

    }

    @Override
    public String toString(){
        String str="";
        //add Current player to play
        str+=currentPlayer;
        //add the trump suit
        str+=briscola.toString();
        //add the deck string representation
        str+=deck.toString()+".";
        //add the surface cards
        str+=surfaceToString()+".";
        //add player hands
        for(Player p:players){
            str+=p.handToString()+".";
        }
        //add Player piles
        for(Player p:players){
            str+=p.pileToString();
            if(p!=players.get(players.size()-1)){
                str+=".";
            }
        }
        return str;
    }
}
