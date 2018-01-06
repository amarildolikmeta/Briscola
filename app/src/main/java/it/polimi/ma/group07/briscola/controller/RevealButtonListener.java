package it.polimi.ma.group07.briscola.controller;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.Parser;

/**
 * Handles the pressing of
 * the reveal opponent cards button
 */

public class RevealButtonListener implements View.OnClickListener {
    GameActivity activity;
    public RevealButtonListener(GameActivity activity) {
        this.activity=activity;
    }

    @Override
    public void onClick(View v) {
        /**
         * Play Button Sound
         */
        SettingsController.getInstance().playButtonClickSound();
        /**
         * wait if the player doesn't have the turn
         * avoids exceptions due to animations being performed
         */
        if(!activity.isReady||!activity.controller.isPlayable()){
            Toast.makeText(activity,"Wait Your Turn",Toast.LENGTH_SHORT).show();
            return;}
        ArrayList<String> opponentCards= Parser.splitString(Briscola.getInstance().getPlayerHand(1),2);
        Log.i("Reveal","Cards"+opponentCards);
        activity.revealCards(opponentCards);
    }
}
