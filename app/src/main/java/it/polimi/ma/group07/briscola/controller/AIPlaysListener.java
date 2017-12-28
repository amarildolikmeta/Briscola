package it.polimi.ma.group07.briscola.controller;

import android.view.View;
import android.widget.PopupWindow;

import it.polimi.ma.group07.briscola.GameActivity;

/**
 * Handles the pressing of the make AI playButton
 */

class AIPlaysListener implements View.OnClickListener {
    GameActivity activity;
    PopupWindow window;
    public AIPlaysListener(GameActivity activity,PopupWindow window) {
        this.activity=activity;
        this.window=window;
    }

    @Override
    public void onClick(View v) {
        window.dismiss();
        boolean aiPlays=activity.controller.getAI();
        aiPlays=!aiPlays;
        activity.controller.setAI(activity,aiPlays);

    }
}
