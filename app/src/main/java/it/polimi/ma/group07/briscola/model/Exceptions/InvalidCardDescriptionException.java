package it.polimi.ma.group07.briscola.model.Exceptions;

import it.polimi.ma.group07.briscola.model.Briscola;

/**
 * Exception thrown in case the 2 string encoding for
 * any of the cards in not a valid one.
 * The suit or the value of the cards are not in the range of possible values.
 */

public class InvalidCardDescriptionException extends BriscolaException {

    public InvalidCardDescriptionException(String message){
        super(message);
    }
}
