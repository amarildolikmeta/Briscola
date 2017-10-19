package it.polimi.ma.group07.briscola.model.Exceptions;

/**
 * Created by amari on 19-Oct-17.
 */

public class BriscolaException extends Exception {
    private String message;

    public BriscolaException(String message){
        this.message=message;
    }

    public String getMessage(){
        return message;
    }
}
