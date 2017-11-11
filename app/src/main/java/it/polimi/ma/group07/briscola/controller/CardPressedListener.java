package it.polimi.ma.group07.briscola.controller;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.R;
import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.StateBundle;

/**
 * Created by amari on 31-Oct-17.
 */

public class CardPressedListener implements View.OnClickListener {
    private GameActivity activity;

    public CardPressedListener(GameActivity activity){
        this.activity=activity;
    }
    @Override
    public void onClick(View v) {
        if(Briscola.getInstance().isGameFinished())
            return;
        LinearLayout playerView=(LinearLayout) v.getParent().getParent();
        int playerIndex=((LinearLayout)playerView.getParent()).indexOfChild(playerView);
        StateBundle state=Coordinator.getInstance().getState();
        //don't take commands if the state is not playable
        if(!state.playableState)
            return;
        //take commands only from the players turn
        if(playerIndex==0&&state.currentPlayer==0 || playerIndex==2 && state.currentPlayer==1){
            return ;
        }
        ImageView b=(ImageView) v;
        int index=((LinearLayout)b.getParent()).indexOfChild(b);
        Coordinator.getInstance().onPerformMove(activity,index);

    }

    }

