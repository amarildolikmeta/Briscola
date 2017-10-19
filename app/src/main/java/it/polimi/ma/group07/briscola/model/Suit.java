package it.polimi.ma.group07.briscola.model;

/**
 * Created by amari on 18-Oct-17.
 */

public enum Suit {
    BATONS(0),
    SWORDS(1),
    CUPS(2),
    GOLD(3);
    private int value;
    private static final String[] suitCharacters={"B","S","C","G"};
    private Suit(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString(){
        return suitCharacters[value];
    }
}
