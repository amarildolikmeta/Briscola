package it.polimi.ma.group07.briscola.model.Exceptions;

/**
 * Created by amari on 20-Oct-17.
 */

public class NoCardInDeckException extends BriscolaException {
    public NoCardInDeckException(String message) {
        super(message);
    }
}
