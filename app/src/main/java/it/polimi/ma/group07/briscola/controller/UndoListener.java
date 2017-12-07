package it.polimi.ma.group07.briscola.controller;

import android.view.View;

import it.polimi.ma.group07.briscola.GameActivity;

/**
 * Created by amari on 31-Oct-17.
 */

public class UndoListener implements View.OnClickListener {
    private GameActivity activity;

    public UndoListener(GameActivity activity){
        this.activity=activity;
    }
    @Override
    public void onClick(View v) {
        activity.controller.onUndo(activity);
    }
}
