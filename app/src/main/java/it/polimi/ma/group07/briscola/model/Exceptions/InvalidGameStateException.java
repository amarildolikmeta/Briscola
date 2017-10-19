package it.polimi.ma.group07.briscola.model.Exceptions;

/**
 * Created by amari on 19-Oct-17.
 */

public class InvalidGameStateException extends BriscolaException {

    public InvalidGameStateException(String message){
        super(message);
    }

}
