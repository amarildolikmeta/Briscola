package it.polimi.ma.group07.briscola.model.Exceptions;

/**
 * Represents an exception thrown when
 * we try to draw a card when the deck is empty
 */

public class NoCardInDeckException extends BriscolaException {
    public NoCardInDeckException(String message) {
        super(message);
    }
}
