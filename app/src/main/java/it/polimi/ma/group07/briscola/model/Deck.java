package it.polimi.ma.group07.briscola.model;

import android.icu.lang.UCharacter;

import java.util.ArrayList;
import java.util.Collections;

import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Exceptions.NoCardInDeckException;

/**
 * Represents a deck of Cards
 */

public class Deck {
    /**
     * Array of the cards in the deck
     */
    private ArrayList<Card> cards;

    /**
     * Creates a new deck and shuffles it
     */
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

    /**
     * Creates a deck of cards from a string representation
     * @param desc  string representation of deck
     * @throws InvalidCardDescriptionException {@link InvalidCardDescriptionException}
     */
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

    /**
     * shuffles the cards in the deck
     */
    public void shuffleDeck(){
        Collections.shuffle(cards);
    }

    /**
     * Draws next card
     * @return Reference to the topmost card in the deck
     * @throws NoCardInDeckException {@link NoCardInDeckException}
     */
    public Card drawCard() throws NoCardInDeckException {
        if(cards.size()==0)
            throw new NoCardInDeckException("Deck Finished");
        Card card=cards.remove(0);
        return card;
    }

    public void addLastCard(Card card){
        cards.add(card);
    }

    /**
     *
     * @return String representation of the deck
     */
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

    /**
     *
     * @return true if the deck is not empty
     */
    public boolean hasMoreCards() {
        if(cards.size()>0)
            return true;
        return false;
    }

    public int getSize() {
        return getDeck().size();
    }
}
