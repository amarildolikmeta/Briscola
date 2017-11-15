package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

/**
 * Represents the interface a Rule applier will offer to the model
 */

public interface RuleApplier {
    /**
     * Determines the winner from an array of cards representing the surface
     * @param surface list of cards in the surface
     * @return the index of the winning card in the surface
     */
    public int determineWinner(ArrayList<Card> surface);

    /**
     * calculates the points won from a list of cards
     * @param surface list of cards representing the surface
     * @return points the surface is worth
     */
    public  int calculatePoints(ArrayList<Card> surface);
    /**
     * determine the winner player by taking into account also special cases as
     * Game finishes in draw :return -1
     * Game isn't finished yet :return -2
     * @param players ArrayList of Players
     * @return index of winning player or {-1;-2}
     */
    public int determineWinningPlayer(ArrayList<Player> players);
}
