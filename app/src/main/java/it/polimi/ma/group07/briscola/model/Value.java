package it.polimi.ma.group07.briscola.model;

import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;

/**
 * Created by amari on 18-Oct-17.
 */

public enum Value {
    ACE(0),
    TWO(1),
    THREE(2),
    FOUR(3),
    FIVE(4),
    SIX(5),
    SEVEN(6),
    JACK(7),
    HORSE(8),
    KING(9);
    public static final String[] valueCharacters={"1","2","3","4","5","6","7","J","H","K"};
    private int value;

    private Value(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    @Override
    public String toString(){
        return valueCharacters[value];
    }

    public static Value stringToValue(String str) throws InvalidCardDescriptionException {
        switch (str){
            case "1":
                return Value.ACE;
            case "2":
                return Value.TWO;
            case "3":
                return Value.THREE;
            case "4":
                return Value.FOUR;
            case "5":
                return Value.FIVE;
            case "6":
                return Value.SIX;
            case "7":
                return Value.SEVEN;
            case "J":
                return Value.JACK;
            case "H":
                return Value.HORSE;
            case "K":
                return Value.KING;
            default: {
                throw new InvalidCardDescriptionException("Invalid Value");
            }
        }
    }

}
