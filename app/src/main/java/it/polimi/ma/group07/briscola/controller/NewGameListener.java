package it.polimi.ma.group07.briscola.controller;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.model.Briscola;

/**
 * Created by amari on 31-Oct-17.
 */

public class NewGameListener implements View.OnClickListener {
    private GameActivity activity;

    public NewGameListener(GameActivity activity){
        this.activity=activity;
    }
    @Override
    public void onClick(View v) {
        if(!activity.isReady||!activity.controller.isPlayable()){
            Toast.makeText(activity,"Wait Your Turn",Toast.LENGTH_SHORT).show();
            return;}
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("New Game?");
        alert.setMessage("Are you sure you want to start a New Game?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.controller.onNewGame(activity);
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
