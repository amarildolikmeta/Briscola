package it.polimi.ma.group07.briscola.controller;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.R;
import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.PlayerState;
import it.polimi.ma.group07.briscola.model.StateBundle;
import it.polimi.ma.group07.briscola.view.CardViewFragment;

/**
 * Created by amari on 31-Oct-17.
 */

public class CardPressedListener implements CardViewFragment.OnCardSelectedListener {
    private GameActivity activity;

    public CardPressedListener(GameActivity activity){
        this.activity=activity;
    }


    @Override
    public void onCardSelected(CardViewFragment card) {
        if(Briscola.getInstance().isGameFinished())
            return;
        View v=card.getView();
        LinearLayout playerView=((LinearLayout) v.getParent());
        int playerIndex=((RelativeLayout)playerView.getParent()).indexOfChild(playerView);
        PlayerState state=Coordinator.getInstance().getState();
        //don't take commands if the state is not playable
        if(!state.playableState)
            return;
        //take commands only from the players turn
        if(playerIndex==0&&state.currentPlayer==0 || playerIndex==2 && state.currentPlayer==1){
            return ;
        }
        int index=((LinearLayout)v.getParent()).indexOfChild(v);
        activity.controller.onPerformMove(activity,index);
    }
}

