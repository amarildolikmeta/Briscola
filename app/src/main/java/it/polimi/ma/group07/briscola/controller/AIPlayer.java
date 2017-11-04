package it.polimi.ma.group07.briscola.controller;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.Card;
import it.polimi.ma.group07.briscola.model.Player;
import it.polimi.ma.group07.briscola.model.PlayerState;
import it.polimi.ma.group07.briscola.model.Suit;
import it.polimi.ma.group07.briscola.model.Value;

/**
 * Created by amari on 31-Oct-17.
 */

public class AIPlayer {
        private static ArrayList<Card> cardList;
    public static int getMoveFromState(PlayerState state){

        cardList= new ArrayList<Card>(Briscola.getInstance().getCardList());
        for(int i =0;i<state.opponentPiles.size();i++){
            ArrayList<Card> tmp=state.opponentPiles.get(i);
            for(Card c:tmp){
                cardList.remove(c);
            }
        }
        return (int)(Math.random()*3)%state.hand.size();
    }
}
