package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;

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

    //if number of players not specified
    public Briscola(){
        this(2);
    }

    public Briscola(int numPlayers)
    {
        deck=new Deck();
        brain=new Brain();
        players=new ArrayList<Player>();
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

    public Briscola(String description) throws InvalidGameStateException, InvalidCardDescriptionException {
        this(description,2);
    }

    public Briscola(String description,int numPlayers) throws InvalidCardDescriptionException, InvalidGameStateException {
        try {
            State state=Parser.parseState(description,numPlayers);
            briscola=Suit.stringToSuit(state.trump);
            currentPlayer=state.currentPlayer;
            deck=new Deck(state.deck);
            players=new ArrayList<Player>();
            surface=new ArrayList<Card>();
            //create players and distribute card in hand and pile
            for(int i=0;i<state.hands.length;i++){
                players.add(new Player(state.hands[i],state.piles[i],"P"+(i+1)));
            }
            //place cards in surface
            for(String s:Parser.splitString(state.surface,2)){
                surface.add(new Card(s));
            }

        } catch (InvalidGameStateException | InvalidCardDescriptionException e) {
            System.out.println(e.getMessage());
            throw e;
        }
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
        str+=briscola;
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
            str+=p.pileToString()+".";
        }
        str=str.substring(0,str.length()-1);
        return str;
    }
}
