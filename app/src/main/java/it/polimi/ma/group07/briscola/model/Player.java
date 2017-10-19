package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;

/**
 * Created by amari on 18-Oct-17.
 */

public class Player {
    private final String name;
    private int score;
    private ArrayList<Card> cardPile;
    private ArrayList<Card> hand;

    public Player(String name){
        this.name=name;
        score=0;
        cardPile=new ArrayList<Card>();
        hand=new ArrayList<Card>();
    }


    public Player(String hand, String pile,String name) throws InvalidCardDescriptionException {
        this.name=name;
        this.hand=new ArrayList<Card>();
        this.cardPile=new ArrayList<Card>();
        this.score=0;
        //split hand and pile strings
        ArrayList<String> handStrings=Parser.splitString(hand,2);
        ArrayList<String> pileStrings=Parser.splitString(pile,2);
        for(int i=0;i<handStrings.size();i++)
            this.hand.add(new Card(handStrings.get(i)));
        for(int i=0;i<pileStrings.size();i++){
            this.cardPile.add(new Card(pileStrings.get(i)));
        }
    }

    public int getScore(){
        return score;
    }

    public void setScore(int score){
        this.score=score;
    }

    public String getName(){
        return name;
    }

    public void addCardsinPile(ArrayList<Card> cards){
        for(Card card :cards)
        {
            this.cardPile.add(card);
        }
    }

    public ArrayList<Card> getCardPile(){
        return cardPile;
    }

    public ArrayList<Card> getHand(){
        return hand;
    }

    public void addCardToHand(Card card){
        hand.add(card);
    }

    public Card placeCardAtIndex(int index){
        return hand.remove(index);
    }

    public void reset() {
        hand.removeAll(hand);
        cardPile.removeAll(cardPile);
        score=0;
    }
    public String handToString()
    {
        String str="";
        for(Card c :hand){
            str+=c.toString();
        }
        return str;
    }

    public String pileToString()
    {
        String str="";
        for(Card c :cardPile){
            str+=c.toString();
        }
        return str;
    }


}
