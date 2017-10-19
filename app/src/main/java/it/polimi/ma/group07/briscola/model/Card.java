package it.polimi.ma.group07.briscola.model;

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
    public Suit getSuit()
    {
        return suit;
    }
    public Value getValue(){
        return value;
    }
    @Override
    public String toString(){
        return suit.toString()+value.toString();
    }
}
