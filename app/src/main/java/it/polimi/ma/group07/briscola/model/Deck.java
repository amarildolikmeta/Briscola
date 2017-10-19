package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by amari on 18-Oct-17.
 */

public class Deck {
    private ArrayList<Card> cards;

    public Deck()
    {
        cards=new ArrayList<Card>();
        for(Suit s: Suit.values())
        {
            for(Value v: Value.values())
            {
                cards.add(new Card(s,v));
            }
        }
        shuffleDeck();
    }
    public Deck(String desc)
    {
        cards=new ArrayList<Card>();
        //TODO create deck from string description
    }
    public ArrayList<Card> getDeck(){
        return cards;
    }
    public void shuffleDeck(){
        Collections.shuffle(cards);
    }
    public Card drawCard() {
        Card card=cards.remove(0);
        return card;
    }
    public void addLastCard(Card card){
        cards.add(card);
    }
    @Override
    public String toString()
    {
        String str="";
        for(Card c:cards)
            str+=c.toString();
        return str;
    }
}
