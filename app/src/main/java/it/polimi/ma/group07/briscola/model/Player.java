package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

/**
 * Created by amari on 18-Oct-17.
 */

public class Player {
    private final String name;
    private ArrayList<Card> hand;
    private int score;
    private ArrayList<Card> cardPile;

    public Player(String name){
        this.name=name;
        score=0;
        cardPile=new ArrayList<Card>();
        hand=new ArrayList<Card>();
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
}
