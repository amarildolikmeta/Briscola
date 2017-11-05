package it.polimi.ma.group07.briscola.model.Exceptions;

/**
 * Represents custom exceptions thrown in the game
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
