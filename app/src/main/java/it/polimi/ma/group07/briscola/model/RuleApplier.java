package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

/**
 * Created by amari on 22-Oct-17.
 */

public interface RuleApplier {

    public int determineWinner(ArrayList<Card> surface, int Briscolaplayed);

    public  int calculatePoints(ArrayList<Card> surface);
}
