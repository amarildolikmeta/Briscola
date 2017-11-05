package it.polimi.ma.group07.briscola.model;

import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;

/**
 * Represents a Napolitean Card
 */

public class Card {
    /**
     * suit of the card
     */
    private  final Suit suit;
    /**
     * value of the card
     */
    private  final  Value value;

    public Card(Suit suit,Value value){
        this.suit=suit;
        this.value=value;
    }

    /**
     * creates a card from a string representation of it
     * @param desc 2 character string representing suit and value of the card
     * @throws InvalidCardDescriptionException {@link InvalidCardDescriptionException}
     */
    public Card(String desc) throws InvalidCardDescriptionException {
        if(desc.length()!=2)
            throw new InvalidCardDescriptionException("Invalid Length");
        /**
         * extract Suit and Value of Card
         */
        value=Value.stringToValue(desc.substring(0,1));
        suit=Suit.stringToSuit(desc.substring(1));

    }
    public Suit getSuit()
    {
        return suit;
    }
    public Value getValue(){
        return value;
    }

    @Override
    public String toString(){
        return value.toString()+suit.toString();
    }
}
