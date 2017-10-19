package it.polimi.ma.group07.briscola.model.Exceptions;

import it.polimi.ma.group07.briscola.model.Briscola;

/**
 * Created by amari on 19-Oct-17.
 */

public class InvalidCardDescriptionException extends BriscolaException {

    public InvalidCardDescriptionException(String message){
        super(message);
    }
}
