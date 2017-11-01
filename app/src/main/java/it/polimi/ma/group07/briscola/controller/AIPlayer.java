package it.polimi.ma.group07.briscola.controller;

import it.polimi.ma.group07.briscola.model.Player;
import it.polimi.ma.group07.briscola.model.PlayerState;

/**
 * Created by amari on 31-Oct-17.
 */

public class AIPlayer {

    public static int getMoveFromState(PlayerState state){

        return (int)(Math.random()*3)%state.hand.size();
    }
}
