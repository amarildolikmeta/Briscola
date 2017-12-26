package it.polimi.ma.group07.briscola.controller;

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
 */

public class AIPlayer {
        private static ArrayList<String> cards;
        private static Brain brain;
        private static ArrayList<String> highValues;
    public static int getMoveFromState(PlayerState state){
        /*if(brain==null){
            Briscola.getInstance().getBrain();
        }
        if(highValues==null){
            highValues= new ArrayList<>();
            highValues.add("1");
            highValues.add("3");
            highValues.add("K");
        }
        int currentPlayer=state.currentPlayer;

        if(cards==null){
            ArrayList<Card>cardList= new ArrayList<Card>(Briscola.getInstance().getCardList());
            cards=new ArrayList<String>();
            for(Card c:cardList)
                cards.add(c.toString());
        }
        for(int i =0;i<state.opponentPiles.size();i++){
            ArrayList<String> tmp=state.opponentPiles.get(i);
            for(String c:tmp){
                cards.remove(c);
            }
        }
        ArrayList<String> myBriscolas=new ArrayList<>();
        ArrayList<String> myHighValues=new ArrayList<>();
        ArrayList<String> bricolaInGame=new ArrayList<>();
        ArrayList<String> highValueInGame=new ArrayList<>();
        boolean winningBriscola=false,winningHighValue=false;
        for(String c:state.hand){
            if(c.substring(1).equals(state.briscola))
            {
                myBriscolas.add(c);
                cards.remove(c);
                state.hand.remove(c);
            }
            if(highValues.contains(c.substring(0,1)))
            {
                myHighValues.add(c);
                cards.remove(c);
                state.hand.remove(c);
            }
        }
        for(String c:cards){
            if(c.substring(1).toString().equals(state.briscola))
            {
                bricolaInGame.add(c);
                }
            if(highValues.contains(c.substring(0,1)))
            {
                highValueInGame.add(c);
            }
        }
        if(state.surface.size()==1){
            ArrayList<Integer> reward=new ArrayList<Integer>();
            ArrayList<String> surface=state.surface;
            for(String c:myHighValues){
                surface.add(c);
                int winnerCard= 0;
                try {
                    winnerCard = brain.determineWinnerString(surface);
                    winnerCard+=currentPlayer;
                    winnerCard%=Briscola.getInstance().getNumberPlayers();
                    if(winnerCard==currentPlayer){
                        winningHighValue = true;
                        reward.add(brain.calculatePointsString(surface));
                    }
                    else
                        reward.add(0);
                    surface.remove(surface.size()-1);
                } catch (InvalidCardDescriptionException e) {

                }

            }
            if(!winningHighValue) {
                for (String c : myBriscolas) {
                    try {
                        surface.add(c);
                        int winnerCard = 0;
                        winnerCard = brain.determineWinnerString(surface);
                        winnerCard += currentPlayer;
                        winnerCard %= Briscola.getInstance().getNumberPlayers();
                        if (winnerCard == currentPlayer) {
                            winningBriscola = true;
                            reward.add(brain.calculatePointsString(surface));
                        } else
                            reward.add(0);
                        surface.remove(surface.size() - 1);
                    } catch (InvalidCardDescriptionException e) {
                        e.printStackTrace();
                    }

                }

            }
            if(!winningBriscola) {
                for (String c : state.hand) {
                    surface.add(c);
                    int winnerCard = 0;
                    try {
                        winnerCard = brain.determineWinnerString(surface);
                        winnerCard += currentPlayer;
                        winnerCard %= Briscola.getInstance().getNumberPlayers();
                        if (winnerCard == currentPlayer) {
                            winningBriscola = true;
                            reward.add(brain.calculatePointsString(surface));
                        } else
                            reward.add(0);
                        surface.remove(surface.size() - 1);
                    } catch (InvalidCardDescriptionException e) {
                        e.printStackTrace();
                    }

                }

            }
            return (int) Collections.max(reward);
        }
        else if(state.surface.size()==0){

        }*/
        return (int)(Math.random()*state.hand.size());
    }
}
