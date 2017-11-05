package it.polimi.ma.group07.briscola.controller;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.model.StateBundle;

/**
 * Created by amari on 31-Oct-17.
 */

public class PileButtonListener implements View.OnClickListener {
    private GameActivity activity;

    public PileButtonListener(GameActivity activity){
        this.activity=activity;
    }

    @Override
    public void onClick(View v) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Pile");
        String message="No cards in Pile";
        LinearLayout playerView=(LinearLayout) v.getParent();
        ArrayList<String> pile;
        StateBundle state=Coordinator.getInstance().getState();
        if(((LinearLayout)playerView.getParent()).indexOfChild(playerView)==0)
        {
            pile=state.pile2;
        }
        else
        {
            pile=state.pile1;
        }
        if(pile.size()>0)
            message=pile.toString();
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
