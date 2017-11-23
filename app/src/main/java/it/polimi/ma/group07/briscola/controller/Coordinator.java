package it.polimi.ma.group07.briscola.controller;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.LinearLayout;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.R;
import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;
import it.polimi.ma.group07.briscola.model.PlayerState;
import it.polimi.ma.group07.briscola.model.StateBundle;

/**
 * Created by amari on 31-Oct-17.
 */

public class Coordinator implements GameController {

    private static  Coordinator Instance;
    private final boolean singlePlayer;
    private String startConfiguration;
    private String movesPerformed;
    private PlayerState state;
    private Handler handler ;
    private int playerIndex;
    public Coordinator(String startConfiguration,boolean singlePlayer){
        this.startConfiguration=startConfiguration;
        this.singlePlayer=singlePlayer;
        this.handler= new Handler();
        this.movesPerformed="";
        playerIndex=0;
    }

    public static Coordinator createInstance(String startConfiguration,boolean singlePlayer){
        Instance=new Coordinator(startConfiguration,singlePlayer);
        return Instance;
    }

    public static Coordinator getInstance(){
        if(Instance==null)
            Instance=new Coordinator(Briscola.getInstance().toString(),false);
        return Instance;
    }

    public void onPerformMove(final GameActivity activity, int index){
        boolean s=Briscola.getInstance().onPerformMove(index);
        movesPerformed+=""+index;
        state=Briscola.getInstance().getPlayerState(playerIndex);
        activity.flushInterface();
        activity.buildInterface(state);
        if(Briscola.getInstance().isGameFinished())
        {

            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("Game Finished");
            String message="You Won";
            if(Briscola.getInstance().getWinner()==-1)
                message="Draw";
            else if(Briscola.getInstance().getWinner()==1)
                message="You Lost";
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            alertDialog.show();
            return;
        }
        if(Briscola.getInstance().isRoundFinished()){
            Log.i("Round Finished","Finishing");
            finishRound(activity);
        }
        else {
            //AIPlays
            if (Briscola.getInstance().getCurrentPlayer() == 1 && singlePlayer) {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        PlayerState state=Briscola.getInstance().getPlayerState(Briscola.getInstance().getCurrentPlayer());
                        onPerformMove(activity, AIPlayer.getMoveFromState(state));
                    }
                }, 1500);

                    }
            }
        }

    private void finishRound(final GameActivity activity) {
        Briscola.getInstance().finishRound();
        Log.i("Round Finished","Round finished");
        state=Briscola.getInstance().getPlayerState(playerIndex);
        //deal a card after a second
        handler.postDelayed(new Runnable() {
            public void run() {
                activity.flushInterface();
                activity.buildInterface(state);
            }
        }, 1500);
            handler.postDelayed(new Runnable() {
                public void run() {
                    Briscola.getInstance().dealCard();
                    state=Briscola.getInstance().getPlayerState(playerIndex);
                    activity.flushInterface();
                    activity.buildInterface(state);
                    Log.i("Round Finished","1st Card Dealt");
                }
            }, 3000);
        handler.postDelayed(new Runnable() {
            public void run() {
                Briscola.getInstance().dealCard();
                state=Briscola.getInstance().getPlayerState(playerIndex);
                activity.flushInterface();
                activity.buildInterface(state);
                Log.i("Round Finished","2nd Card Dealt");
            }
        }, 4500);

        //make AI Play
        if (Briscola.getInstance().getCurrentPlayer() == 1 && singlePlayer) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    PlayerState state=Briscola.getInstance().getPlayerState(Briscola.getInstance().getCurrentPlayer());
                    onPerformMove(activity, AIPlayer.getMoveFromState(state));
                }
            },6000);
        }

    }

    public void onRestart(GameActivity activity){
        try {
            Briscola.getInstance().startFromConfiguration(startConfiguration);
        } catch (InvalidCardDescriptionException e) {
            e.printStackTrace();
        } catch (InvalidGameStateException e) {
            e.printStackTrace();
        }
        movesPerformed="";
        state=Briscola.getInstance().getPlayerState(playerIndex);
        activity.flushInterface();
        activity.buildInterface(state);
    }

    public void onNewGame(GameActivity activity){
        Briscola.getInstance().restart();
        startConfiguration=Briscola.getInstance().toString();
        movesPerformed="";
        state=Briscola.getInstance().getPlayerState(playerIndex);
        activity.flushInterface();
        activity.buildInterface(state);
    }

    public PlayerState getState(){
        return state;
    }
    public String getStartConfiguration() {
        return startConfiguration;
    }

    public void setStartConfiguration(String startConfiguration) {
        this.startConfiguration = startConfiguration;
    }

    public  void setState(GameActivity activity,PlayerState state) {
        this.state = state;

    }

    public String getMovesPerformed() {
        return movesPerformed;
    }
}
