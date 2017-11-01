package it.polimi.ma.group07.briscola.controller;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.R;
import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;
import it.polimi.ma.group07.briscola.model.StateBundle;

/**
 * Created by amari on 31-Oct-17.
 */

public class Coordinator {

    private static  Coordinator Instance;
    private final boolean singlePlayer;
    private String startConfiguration;
    private String movesPerformed;
    private StateBundle state;
    private Handler handler ;
    public Coordinator(String startConfiguration,boolean singlePlayer){
        this.startConfiguration=startConfiguration;
        this.singlePlayer=singlePlayer;
        this.handler= new Handler();
        this.movesPerformed="";
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
        state=Briscola.getInstance().getGameState();
        if(Briscola.getInstance().isGameFinished())
        {

            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("Congratulations");
            String message="Game Finished";
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            alertDialog.show();
        }
        if(Briscola.getInstance().isRoundFinished()){
            Briscola.getInstance().finishRound();
            //state=game.getGameState();
            LinearLayout[] playerViews=activity.getPlayerViews();
            playerViews[state.currentPlayer].setBackgroundResource(R.drawable.custom_border);
            playerViews[(state.currentPlayer+1)%2].setBackgroundResource(R.drawable.no_border);

            activity.flushInterface();
            activity.buildInterface(state);
            //deal a card after a second
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 10 seconds
                    Briscola.getInstance().dealCard();
                    state=Briscola.getInstance().getGameState();
                    activity.flushInterface();
                    activity.buildInterface(state);
                }
            }, 1500);
            //deal second card   a second later
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 10 seconds
                    Briscola.getInstance().dealCard();
                    state=Briscola.getInstance().getGameState();
                    activity.flushInterface();
                    activity.buildInterface(state);
                }
            }, 3000);
            //make AI Play
            if (Briscola.getInstance().getCurrentPlayer() == 1 && singlePlayer) {

                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        onPerformMove(activity, AIPlayer.getMoveFromState(Briscola.getInstance().getCurrentPlayerState()));
                    }
                }, 4500);

            }

        }
        else {
            activity.flushInterface();
            activity.buildInterface(state);
            //AIPlays
            if (Briscola.getInstance().getCurrentPlayer() == 1 && singlePlayer) {

                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        onPerformMove(activity, AIPlayer.getMoveFromState(Briscola.getInstance().getCurrentPlayerState()));
                    }
                }, 1500);

            }
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
        state=Briscola.getInstance().getGameState();
        activity.flushInterface();
        activity.buildInterface(state);
    }

    public void onNewGame(GameActivity activity){
        Briscola.getInstance().restart();
        startConfiguration=Briscola.getInstance().toString();
        movesPerformed="";
        state=Briscola.getInstance().getGameState();
        activity.flushInterface();
        activity.buildInterface(state);
    }

    public StateBundle getState(){
        return state;
    }
    public String getStartConfiguration() {
        return startConfiguration;
    }

    public void setStartConfiguration(String startConfiguration) {
        this.startConfiguration = startConfiguration;
    }

    public  void setState(GameActivity activity,StateBundle state) {
        this.state = state;

    }

    public String getMovesPerformed() {
        return movesPerformed;
    }
}
