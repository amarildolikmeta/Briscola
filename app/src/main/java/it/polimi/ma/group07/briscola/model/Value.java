package it.polimi.ma.group07.briscola.model;

/**
 * Created by amari on 18-Oct-17.
 */

public enum Value {
    Ace(0),
    TWO(1),
    THREE(2),
    FOUR(3),
    FIVE(4),
    SIX(5),
    SEVEN(6),
    JACK(7),
    HORSE(8),
    KING(9);
    private int value;

    private Value(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
