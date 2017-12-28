package it.polimi.ma.group07.briscola.controller;

import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.model.Briscola;

/**
 * Handles the pressing of the suggest moveButton
 */

class SuggestMoveListener implements View.OnClickListener {
    private final PopupWindow window;
    GameActivity activity;
    public SuggestMoveListener(GameActivity activity, PopupWindow window) {
        this.activity=activity;
        this.window=window;
    }
    @Override
    public void onClick(View v) {
        if(!activity.isReady||activity.controller.getAI()){
            Toast.makeText(activity,"Disable AI Play First",Toast.LENGTH_SHORT).show();
            return;
        }
        window.dismiss();
        activity.controller.suggestMove(activity);
    }
}
