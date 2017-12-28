package it.polimi.ma.group07.briscola.controller;

import android.view.View;

import it.polimi.ma.group07.briscola.GameActivity;

/**
 * Handles the pressing of the Exit Button
 */

public class ExitButtonListener implements View.OnClickListener {
    GameActivity activity;
    public ExitButtonListener(GameActivity activity){
        this.activity=activity;
    }
    @Override
    public void onClick(View v) {
        activity.onBackPressed();
    }
}
