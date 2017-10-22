package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by amari on 18-Oct-17.
 */

public class Brain implements RuleApplier {
    private Map<Value,Integer> valueToScore ;
    private Map<Value,Integer> valueToRank;


    private Suit trumpSuit;
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

    public int getScoreFromValue(Value value){
        return valueToScore.get(value);
    }

    public int getRankFromValue(Value value){
        return valueToRank.get(value);
    }

    public void setTrumpSuit(Suit trumpSuit) {
        this.trumpSuit = trumpSuit;
    }

    public int determineWinner(ArrayList<Card> surface,int Briscolaplayed) {
        int max=11;
        int winner=0;
        Suit winnerSuit;
        if(Briscolaplayed==0)
        {
            max=getRankFromValue(surface.get(0).getValue());
            winner=0;
            winnerSuit=surface.get(0).getSuit();

        }
        else
        {
            winnerSuit=trumpSuit;
        }
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

    public  int calculatePoints(ArrayList<Card> surface) {
        int count=0;
        for(Card c:surface){
            count+=getScoreFromValue(c.getValue());
        }
        return count;
    }
}
