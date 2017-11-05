package it.polimi.ma.group07.briscola.model.Exceptions;

/**
 * Represents exceptions thrown when creating
 * a game from a string representation of it
 * but the string doesn't represent
 * a valid state
 */

public class InvalidGameStateException extends BriscolaException {

    public InvalidGameStateException(String message){
        super(message);
    }

}
