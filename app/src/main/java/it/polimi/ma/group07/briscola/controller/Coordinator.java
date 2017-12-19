package it.polimi.ma.group07.briscola.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.MainActivity;
import it.polimi.ma.group07.briscola.R;
import it.polimi.ma.group07.briscola.controller.persistance.DataRepository;
import it.polimi.ma.group07.briscola.controller.persistance.DatabaseRepository;
import it.polimi.ma.group07.briscola.controller.persistance.LocalGame;
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
    private DataRepository repository;
    private boolean playable;

    public Coordinator(String startConfiguration,boolean singlePlayer){
        this.startConfiguration=startConfiguration;
        this.singlePlayer=singlePlayer;
        this.handler= new Handler();
        this.movesPerformed="";
        playable=true;
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
        Log.i("Coordinator","Playing index:"+index);
        int player=Briscola.getInstance().getCurrentPlayer();
        String card=Briscola.getInstance().onPerformMove(index);
        playable=false;
        movesPerformed+=""+index;
        state=Briscola.getInstance().getPlayerState(playerIndex);
        activity.playCard(card,player,index);
        Log.i("Coordinator","Card Played:"+card);
        }
    @Override
    public void onMovePerformed(final GameActivity activity){
        if(Briscola.getInstance().isGameFinished())
        {
            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("Game Finished");
            String message="You Won :";
            String state=LocalGame.WON;
            if(Briscola.getInstance().getWinner()==-1) {
                message = "Draw :";
                state=LocalGame.DRAWN;
            }
            else if(Briscola.getInstance().getWinner()==1) {
                message = "You Lost :";
                state=LocalGame.LOST;
            }
            message+=""+Briscola.getInstance().getPlayers().get(0).getScore()+" points";
            alertDialog.setMessage(message);

            getRepository().saveLocalGame(new LocalGame(startConfiguration,movesPerformed,state));
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
            handler.postDelayed(new Runnable() {
                public void run() {
                    finishRound(activity);
                }
            },700);

        }
        else {
            //AIPlays
            if (Briscola.getInstance().getCurrentPlayer() == 1 && singlePlayer) {

                handler.postDelayed(new Runnable() {
                    public void run() {
                        PlayerState state=Briscola.getInstance().getPlayerState(Briscola.getInstance().getCurrentPlayer());
                        onPerformMove(activity, AIPlayer.getMoveFromState(state));
                    }
                },700);

            }
            else
                playable=true;
        }
    }

    @Override
    public boolean isPlayable() {
        return playable;
    }

    private void finishRound(final GameActivity activity) {
        Briscola.getInstance().finishRound();
        state=Briscola.getInstance().getPlayerState(playerIndex);
        ArrayList<String> cardsDealt=new ArrayList<>();
          if(Briscola.getInstance().hasMoreCards())
              for(int i=0;i<Briscola.getInstance().getNumberPlayers();i++)
                cardsDealt.add(Briscola.getInstance().dealCard());
          else
              Briscola.getInstance().dealCards();
        Log.i("Round Finished","Will Deal"+cardsDealt.toString());
        boolean isLastDraw=!Briscola.getInstance().hasMoreCards();
        Log.i("Round Finished","Round finished:Winner:"+Briscola.getInstance().getCurrentPlayer());

        activity.finishRound(Briscola.getInstance().getCurrentPlayer(),cardsDealt,isLastDraw);
        activity.setScores(Briscola.getInstance().getScores());
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

    @Override
    public void setState(PlayerState state) {

    }

    @Override
    public int getPlayerIndex() {
        //in single mode player is 0 by default
        return 0;
    }

    @Override
    public void finishGame(String reason) {
        //Save game to replay it later
        if(!Briscola.getInstance().isGameFinished()) {
            Log.i("Coordinator","State:"+startConfiguration+" Moves:"+movesPerformed);
            getRepository().saveCurrentGame(new LocalGame(startConfiguration, movesPerformed, "Running"));
        }
    }

    @Override
    public DataRepository getRepository() {
        return DatabaseRepository.getInstance();
    }

    @Override
    public void onUndo(GameActivity activity) {

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

    public void setMoves(String moves) {
        this.movesPerformed = moves;
    }
}
