package it.polimi.ma.group07.briscola.model;

import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;

/**
 * Enumeration of the Suits of the cards
 */

public enum Suit {
    BATONS(0),
    SWORDS(1),
    CUPS(2),
    GOLD(3);
    private int value;
    public static final String[] suitCharacters={"B","S","C","G"};
    private Suit(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     *
     * @return String Representation of the suits
     */
    @Override
    public String toString(){
        return suitCharacters[value];
    }
    public static Suit stringToSuit(String str) throws InvalidCardDescriptionException {
        switch (str){
            case "B":
                return Suit.BATONS;
            case "S":
                return Suit.SWORDS;
            case "C":
                return Suit.CUPS;
            case "G":
                return Suit.GOLD;
            default:
                throw new InvalidCardDescriptionException("Invalid Suit");
        }
    }
}
