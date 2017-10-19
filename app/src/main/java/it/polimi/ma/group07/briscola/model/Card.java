package it.polimi.ma.group07.briscola.model;

/**
 * Created by amari on 18-Oct-17.
 */

public class Card {
    private static final String[] suitCharacters={"B","S","C","G"};
    private static final String[] valueCharacters={"A","2","3","4","5","6","7","J","H","K"};
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
        return valueCharacters[suit.getValue()]+suitCharacters[value.getValue()];
    }
}
