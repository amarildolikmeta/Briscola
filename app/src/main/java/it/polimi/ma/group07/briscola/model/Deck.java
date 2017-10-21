package it.polimi.ma.group07.briscola.model;

import android.icu.lang.UCharacter;

import java.util.ArrayList;
import java.util.Collections;

import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Exceptions.NoCardInDeckException;

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

    public Deck(String desc) throws InvalidCardDescriptionException {
        cards=new ArrayList<Card>();
        //create array of  Strings (Length of Deck divided by 2 Characters for each Card)
        ArrayList<String> cardStrings=Parser.splitString(desc,2);
        for(String s:cardStrings){
            cards.add(new Card(s));
        }
    }


    public ArrayList<Card> getDeck(){
        return cards;
    }

    public void shuffleDeck(){
        Collections.shuffle(cards);
    }

    public Card drawCard() throws NoCardInDeckException {
        if(cards.size()==0)
            throw new NoCardInDeckException("Deck Finished");
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

    public Card getLastCard() {
        return cards.get(cards.size()-1);
    }

    public boolean hasMoreCards() {
        if(cards.size()>0)
            return true;
        return false;
    }
}
