package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents on object that will apply the rules of the game
 * {@inheritDoc }
 */

public class Brain implements RuleApplier {
    /**
     * Dictionaries to translate the value of cards into a rank
     * and a score. In this way the Cards themselves are decoupled
     * from the rules of the briscola game
     */
    private Map<Value,Integer> valueToScore ;
    private Map<Value,Integer> valueToRank;

    /**
     * Each game has a trump suit that doesnt change throughout the game
     * is used to determine the winner of each round
     */
    private Suit trumpSuit;

    /**
     * Creates the Brain object and initializes the
     * Maps
     */
    public Brain(){
        valueToRank=new HashMap<Value, Integer>();
        valueToScore=new HashMap<Value,Integer>();

        valueToRank.put(Value.ACE,1);
        valueToRank.put(Value.THREE,2);
        valueToRank.put(Value.KING,3);
        valueToRank.put(Value.HORSE,4);
        valueToRank.put(Value.JACK,5);
        valueToRank.put(Value.SEVEN,6);
        valueToRank.put(Value.SIX,7);
        valueToRank.put(Value.FIVE,8);
        valueToRank.put(Value.FOUR,9);
        valueToRank.put(Value.TWO,10);

        valueToScore.put(Value.ACE,11);
        valueToScore.put(Value.THREE,10);
        valueToScore.put(Value.KING,4);
        valueToScore.put(Value.HORSE,3);
        valueToScore.put(Value.JACK,2);
        valueToScore.put(Value.SEVEN,0);
        valueToScore.put(Value.SIX,0);
        valueToScore.put(Value.FIVE,0);
        valueToScore.put(Value.FOUR,0);
        valueToScore.put(Value.TWO,0);

    }

    /**
     * return the numeric score of the card's Value
     * @param value of the card
     * @return integer in range [0:11]
     */
    public int getScoreFromValue(Value value){
        return valueToScore.get(value);
    }

    /**
     * return the numeric rank of the card's Value
     * @param value of the Cards
     * @return integer in range [0:11]
     */
    public int getRankFromValue(Value value){
        return valueToRank.get(value);
    }

    /**
     * set the trum Suit of the game
     * @param trumpSuit ArrayList of Cards\
     */
    public void setTrumpSuit(Suit trumpSuit) { this.trumpSuit = trumpSuit; }

    /**
     * return the index of the winning card in range from 0 to the number of players - 1
     * @param surface ArrayList of Cards
     * @return integer included in [0:numberOfPlayers-1]
     */
    public int determineWinner(ArrayList<Card> surface) {
        int briscolaplayed=0;
        /**
         * count the number of cards that have the trump suit
         * this number affects the way the winner of the round is determined
         */
        for(int i=0;i<surface.size();i++)
            if(surface.get(i).getSuit().toString().equals(trumpSuit.toString()))
                briscolaplayed++;
        int max=11;
        int winner=0;
        Suit winnerSuit;
        /**
         * Case when no trump suit cards are played
         */
        if(briscolaplayed==0)
        {
            /**
             * Winner suit is the suit of the first card
             * in the surface
             * and the maximum value rank in the field is
             * the rank of that card
             */
            max=getRankFromValue(surface.get(0).getValue());
            winner=0;
            winnerSuit=surface.get(0).getSuit();

        }
        else
        {
            /**
             * if any briscola cards were played
             * the trump suit is the winner suit
             */
            winnerSuit=trumpSuit;
        }
        /**
         * find the winner by comparing each card's suit with the winner suit
         * and their values with the maximun rank
         */
        for(int i =0;i<surface.size();i++){
            int rank=getRankFromValue(surface.get(i).getValue());
            Suit s=surface.get(i).getSuit();
            if(rank<max && s.toString().equals(winnerSuit.toString()))
            {
                max=rank;
                winner=i;
            }
        }
        return winner;
    }

    /**
     * return an integer count of points in the the provided Card Pile
     * @param surface ArrayList of Cards from a Card Pile
     * @return integer included in [0:120]
     */
    public  int calculatePoints(ArrayList<Card> surface) {
        int count=0;
        for(Card c:surface){
            count+=getScoreFromValue(c.getValue());
        }
        return count;
    }
}
