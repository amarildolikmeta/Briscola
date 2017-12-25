package it.polimi.ma.group07.briscola.controller;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;
import it.polimi.ma.group07.briscola.model.StateBundle;

/**
 * Created by amari on 31-Oct-17.
 */

public class RestartListener implements View.OnClickListener {
    private GameActivity activity;
    private String startConfiguration;
    public RestartListener(GameActivity activity){
        this.activity=activity;
    }

    @Override
    public void onClick(View v) {
        if(!activity.isReady||!activity.controller.isPlayable()){
            Toast.makeText(activity,"Wait Your Turn",Toast.LENGTH_SHORT).show();
            return;}
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("Restart Game?");
        alert.setMessage("Are you sure you want to restart the game?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                ((Coordinator)activity.controller).onRestart(activity);
                dialog.dismiss();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }
}
