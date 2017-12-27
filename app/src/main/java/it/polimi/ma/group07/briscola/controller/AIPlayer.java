package it.polimi.ma.group07.briscola.controller;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

import it.polimi.ma.group07.briscola.model.Brain;
import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.Card;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Player;
import it.polimi.ma.group07.briscola.model.PlayerState;
import it.polimi.ma.group07.briscola.model.Suit;
import it.polimi.ma.group07.briscola.model.Value;

/**
 * Represents the opponent in the local game
 * will take decisions based on the state of the game
 * The AI takes a quasi-greedy approach
 */

public class AIPlayer {
    /**
     * List of all the cards in the game
     * use to count cards
     */
    private static ArrayList<String> cards;
    /**
     * Rule applier of the game
     * Use to check the reward of the moves
     */
    private static Brain brain;
    /**
     * List of values that have high points in the game
     * These values should be given priority
     */
    private static ArrayList<String> highValues;
    private static ArrayList<String> lowValues;
    private static ArrayList<String> values;
    /**
     * Return the index of the card to be played based on the state of the game
     * @param state Representstion of the state of the game as seen from the player
     * @return index of the card to be played
     */
    public static int getMoveFromState(PlayerState state){
        Log.i("AI","Taking Deision...");
        /**
         * Populate the lists if theyre are empty
         */
        if(brain==null){
            brain=Briscola.getInstance().getBrain();
        }

        if(highValues==null){
            highValues= new ArrayList<>();
            highValues.add("1");
            highValues.add("3");
            highValues.add("K");
        }
        if(lowValues==null){
            lowValues=new ArrayList<>();
            lowValues.add("H");
            lowValues.add("J");
            lowValues.add("7");
            lowValues.add("6");
            lowValues.add("5");
            lowValues.add("4");
            lowValues.add("2");
        }
        if(values==null){
            values= new ArrayList<>();
            values.add("1");
            values.add("3");
            values.add("K");
            values.add("H");
            values.add("J");
            values.add("7");
            values.add("6");
            values.add("5");
            values.add("4");
            values.add("2");
        }
        int currentPlayer=state.currentPlayer;

        if(cards==null){
            ArrayList<Card>cardList= new ArrayList<>(Briscola.getInstance().getCardList());
            cards=new ArrayList<>();
            for(Card c:cardList)
                cards.add(c.toString());
        }
        Log.i("AI","Lists Populated");
        /**
         * remove the cards already played
         */
        for(int i =0;i<state.opponentPiles.size();i++){
            ArrayList<String> tmp=state.opponentPiles.get(i);
            for(String c:tmp){
                cards.remove(c);
            }
        }
        for(String c:state.ownPile)
            cards.remove(c);
        for(String c:state.surface)
            cards.remove(c);
        Log.i("AI","Removed Cards Played");
        /**
         * Lists used to make the decisions
         */
        ArrayList<String> myBriscolas=new ArrayList<>();
        ArrayList<String> myHighValues=new ArrayList<>();
        ArrayList<String> myLowValues=new ArrayList<>();
        ArrayList<String> briscolaInGame=new ArrayList<>();
        ArrayList<String> highValueInGame=new ArrayList<>();
        ArrayList<String> cardsToPlay=new ArrayList<>();
        for(String c:state.hand){
            if(c.substring(1).equals(state.briscola.substring(1)))
            {
                myBriscolas.add(c);
            }
            else if(highValues.contains(c.substring(0,1)))
            {
                myHighValues.add(c);
            }
            else
                myLowValues.add(c);
            cards.remove(c);
        }
        for(String c:cards){
            if(c.substring(1).toString().equals(state.briscola.substring(1)))
            {
                briscolaInGame.add(c);
                }
            if(highValues.contains(c.substring(0,1)))
            {
                highValueInGame.add(c);
            }
        }
        orderByValue(myHighValues);
        orderByValue(myBriscolas);
        orderByValue(myLowValues);
        Log.i("AI","Cards in Play Listed");
        Log.i("AI","Hand:"+state.hand);
        Log.i("AI","Briscolas:"+myBriscolas);
        Log.i("AI","Low Values:"+myLowValues);
        Log.i("AI","High Values:"+myHighValues);
        /**
         * Case when you are second player to play
         */
        if(state.surface.size()==1){
            Log.i("AI","1 Card in Surface");
            ArrayList<Integer> reward=new ArrayList<>();
            ArrayList<String> surface=state.surface;
            String card=surface.get(0);
            /**
             * card in play is not briscola
             */
            if(!card.substring(1).contains(state.briscola.substring(1))) {
                if(state.deckSize==2){
                    /**
                     * next one is last draw , try to take the last briscola
                     * if it's best for you
                     */
                    if(getMaxValue(briscolaInGame).contains(state.briscola)){
                        for(int i=myLowValues.size()-1;i>-1;i--){
                            String c=myLowValues.get(i);
                            surface.add(c);
                            int winnerCard = 0;
                            try {
                                winnerCard = brain.determineWinnerString(surface);
                                if (winnerCard != 1) {
                                    int r=brain.calculatePointsString(surface);
                                    if((r<9 &&highValues.contains(state.briscola.substring(0,1)))||
                                            r<5){
                                        /**
                                         * lose round on purpose to get the last briscola next round
                                         */
                                        int index=state.hand.indexOf(c);
                                        Log.i("AI", "Playing index:" +index);
                                        return index;
                                    }
                                }

                            } catch (InvalidCardDescriptionException e) {
                                e.printStackTrace();
                            }
                            surface.remove(surface.size() - 1);
                        }
                    }
                }
                /**
                 * Give priority to high values that are not briscola
                 * get points out of them if you can so opponent doesn't win them over
                 */
                for (String c : myHighValues) {
                    surface.add(c);
                    int winnerCard = 0;
                    try {
                        winnerCard = brain.determineWinnerString(surface);
                        if (winnerCard == 1) {
                            int index=state.hand.indexOf(c);
                            Log.i("AI", "Playing index:" +index);
                            return index;
                        }

                    } catch (InvalidCardDescriptionException e) {
                        e.printStackTrace();
                    }
                    surface.remove(surface.size() - 1);
                }
                /**
                 * card has high value
                 * take it with a briscola if you can
                 */
                if(highValues.contains(card.substring(0,1)))
                {
                    if(myBriscolas.size()>0){
                        //return last briscola(lowest value)
                        int index=state.hand.indexOf(myBriscolas.get(myBriscolas.size()-1));
                        Log.i("AI", "Playing index:" +index);
                        return index;
                    }
                }
                else{
                    /**
                     * Play highest value that wins you the hand
                     */
                    Log.i("AI","Considering hand:");
                    for (String c : state.hand) {
                        surface.add(c);
                        int winnerCard = 0;
                        try {
                            winnerCard = brain.determineWinnerString(surface);
                            if (winnerCard == 1) {
                                reward.add(brain.calculatePointsString(surface));
                                cardsToPlay.add(c);
                            }
                            surface.remove(surface.size() - 1);
                        } catch (InvalidCardDescriptionException e) {
                            e.printStackTrace();
                        }
                    }
                    if(reward.size()==0){
                        /**
                         * cannot win hand
                         * play lowest value you can
                         */
                        if(myLowValues.size()>0)
                        {
                            String c=myLowValues.get(myLowValues.size()-1);
                            int index=state.hand.indexOf(c);
                            Log.i("AI", "AI plays:" +index);
                            return index;
                        }
                        if(myBriscolas.size()>0)
                        {
                            String c=myBriscolas.get(myBriscolas.size()-1);
                            int index=state.hand.indexOf(c);
                            Log.i("AI", "AI plays:" +index);
                            return index;
                        }
                        if(myHighValues.size()>0)
                        {
                            String c=myHighValues.get(myHighValues.size()-1);
                            int index=state.hand.indexOf(c);
                            Log.i("AI", "AI plays:" +index);
                            return index;
                        }
                        /**
                         * should never reach here
                         */
                        return (int)(Math.random()*state.hand.size());
                    }
                    int max=Collections.max(reward);
                    int index=state.hand.indexOf(cardsToPlay.get(reward.indexOf(max)));
                    Log.i("AI","AI plays "+index);
                    return index;
                }
            }
            else{
                /**
                 * Card in play is a Briscola
                 */
                if(!highValues.contains(card.substring(0,1))){
                    /**
                     * is a low value briscola
                     * give him a low value card if possible
                     */
                    if(myLowValues.size()>0)
                    {
                        String c=myLowValues.get(myLowValues.size()-1);
                        int index=state.hand.indexOf(c);
                        Log.i("AI", "AI plays:" +index);
                        return index;
                    }
                    /**
                     * otherwise win with a briscola if you can
                     */
                    for(int i=myBriscolas.size()-1;i>-1;i--){
                        String c=myBriscolas.get(i);
                        surface.add(c);
                        int winnerCard = 0;
                        try {
                            winnerCard = brain.determineWinnerString(surface);
                            if (winnerCard == 1) {
                                int index=state.hand.indexOf(c);
                                Log.i("AI", "Playing index:" +index);
                                return index;
                            }
                            surface.remove(surface.size() - 1);
                        } catch (InvalidCardDescriptionException e) {
                            e.printStackTrace();
                        }
                    }
                    /**
                     * if you can't win with a briscola
                     * give a low value briscola if you can
                     */
                    if(containsLowValue(myBriscolas))
                    {
                        int index=state.hand.indexOf(myBriscolas.get(myBriscolas.size()-1));
                        Log.i("AI", "Playing index:" +index);
                        return index;
                    }
                    /**
                     *give lowest value non briscola you have
                     */
                    if(myHighValues.size()>0)
                    {
                        int index=state.hand.indexOf(myHighValues.get(myHighValues.size()-1));
                        Log.i("AI", "Playing index:" +index);
                        return index;
                    }
                    /**
                     * last give the lowest value in hand
                     */
                    int index=state.hand.indexOf(getMinValue(state.hand));
                    Log.i("AI", "Playing index:" +index);
                    return index;
                }
            }
        }
        /**
         * Case when you are first player to play
         * Stategy is to play low
         * giving out low cards first when possible
         */
        else if(state.surface.size()==0){
            /**
             * Case When there are no more briscola cards in game
             * can play high values without worrying
             */
            if((briscolaInGame.size()==1 && state.deckSize>0) ||briscolaInGame.size()==0){
                for(String c:myHighValues){
                    if(!isThereLargerWithSameSuit(c,highValueInGame))
                        return state.hand.indexOf(c);
                }
            }
            //play lowest value card you have
            if(myLowValues.size()>0)
            {
                String c=myLowValues.get(myLowValues.size()-1);
                return state.hand.indexOf(c);
            }
            //play lowest briscola
            if(myBriscolas.size()>0)
            {
                String c=myBriscolas.get(myBriscolas.size()-1);
                return state.hand.indexOf(c);
            }
            if(myHighValues.size()>0)
            {
                String c=myHighValues.get(myHighValues.size()-1);
                return state.hand.indexOf(c);
            }
            return (int)(Math.random()*state.hand.size());
        }
        return (int)(Math.random()*state.hand.size());
    }

    private static boolean containsLowValue(ArrayList<String> myBriscolas) {
        for(String c:myBriscolas){
            if(lowValues.contains(c.substring(0,1)))
                return true;
        }
        return false;
    }

    private static void orderByValue(ArrayList<String> cards){
        int rankI,rankJ;
        String tmp;
        for(int i=0;i<cards.size()-1;i++){
            for(int j=i+1;j<cards.size();j++){
                rankI=values.indexOf(cards.get(i).substring(0,1));
                rankJ=values.indexOf(cards.get(j).substring(0,1));
                if(rankI>rankJ){
                    tmp=cards.get(i);
                    cards.set(i,cards.get(j));
                    cards.set(j,tmp);
                }
            }
        }
    }
    private static String getMaxValue(ArrayList<String> cards){
        if(cards.size()==0)
            return null;
        int maxRank=values.indexOf(cards.get(0).substring(0,1));
        int maxIndex=0;
        int rank;
        for(int i=1;i<cards.size()-1;i++){
            rank=values.indexOf(cards.get(i).substring(0,1));
            if(rank<maxRank){
                maxRank=rank;
                maxIndex=i;
            }
        }
        return cards.get(maxIndex);
    }
    private static String getMinValue(ArrayList<String> cards){
        if(cards.size()==0)
            return null;
        int minRank=values.indexOf(cards.get(0).substring(0,1));
        int minIndex=0;
        int rank;
        for(int i=1;i<cards.size()-1;i++){
            rank=values.indexOf(cards.get(i).substring(0,1));
            if(rank>minRank){
                minRank=rank;
                minIndex=i;
            }
        }
        return cards.get(minIndex);
    }
    private static boolean isThereLargerWithSameSuit(String card,ArrayList<String> collection){
        int rank=values.indexOf(card.substring(0,1));
        int cardRank;
        for(String c:collection){
            //if they are same suit
            if(c.substring(1).contains(card.substring(1))){
                cardRank=values.indexOf(c.substring(0,1));
                if(cardRank<rank)
                    return true;
            }
        }
        return false;
    }
}
