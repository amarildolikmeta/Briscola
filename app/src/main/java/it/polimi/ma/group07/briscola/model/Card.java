package it.polimi.ma.group07.briscola.model;

import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;

/**
 * Created by amari on 18-Oct-17.
 */

public class Card {

    private  final Suit suit;
    private  final  Value value;

    public Card(Suit suit,Value value){
        this.suit=suit;
        this.value=value;
    }
    public Card(String desc) throws InvalidCardDescriptionException {
        if(desc.length()!=2)
            throw new InvalidCardDescriptionException("Invalid Length");
        //extract Value of Card
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
