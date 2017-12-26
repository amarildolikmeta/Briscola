package it.polimi.ma.group07.briscola.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

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
 * Controller for local games
 */

public class Coordinator implements GameController {
    /**
     * static reference to the controller
     */
    private static  Coordinator Instance;
    /**
     * originally you could play locally in two players
     * now obsolete
     */
    private final boolean singlePlayer;
    /**
     * the starting configuration of the current game
     * and the moves performed since the beginning of the game
     */
    private String startConfiguration;
    private String movesPerformed;
    /**
     * representation of the state of the game {@link PlayerState}
     */
    private PlayerState state;
    /**
     * Handler for Asnchronous tasks
     */
    private Handler handler ;
    /**
     * index of the player
     */
    private int playerIndex;
    /**
     * Represents if a state is playable or no
     * set to true after animations are finished
     */
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

    /**
     * Perform a move
     * @param activity reference to the activity of the game
     * @param index index of card tobe played
     */
    public void onPerformMove(final GameActivity activity, int index){
        Log.i("Coordinator","Playing index:"+index);
        int player=Briscola.getInstance().getCurrentPlayer();
        /**
         * perform the move in the model and set the state
         * to not playable until all the animations are played
         */
        String card=Briscola.getInstance().onPerformMove(index);
        playable=false;
        /**
         * save the currently made move
         */
        movesPerformed+=""+index;
        state=Briscola.getInstance().getPlayerState(playerIndex);
        /**
         * play the animation of the card
         * after the animation is finished the view will call the {#onMovePerformed method}
         * to chek the state of the game after the move is performed
         */
        activity.playCard(card,player,index);
        Log.i("Coordinator","Card Played:"+card);
        }

    /**
     * called after the animations in the view are finished
     * checks the state of the game and performes the next actions
     * accordingly
     * @param activity the activity of the game
     */
    @Override
    public void onMovePerformed(final GameActivity activity){
        /**
         * Check if the game is finished
         */
        if(Briscola.getInstance().isGameFinished())
        {
            /**
             * Display the winner and save the game that finished for statistics
             */
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
            //delete previous saved game if any
            getRepository().deleteCurrentGame();
            getRepository().saveLocalGame(new LocalGame(startConfiguration,movesPerformed,state));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    });
            alertDialog.show();
            return;
        }
        /**
         * Check if the round is finished
         */
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
                /**
                 * Delay a bit the action of the AI
                 */
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

    /**
     * Go to the next round of the game and show the animations
     * of the round being finished
     * @param activity the activity of the game
     */
    private void finishRound(final GameActivity activity) {
        Briscola.getInstance().finishRound();
        state=Briscola.getInstance().getPlayerState(playerIndex);
        /**
         * Deal next batch of cards if any
         */
        ArrayList<String> cardsDealt=new ArrayList<>();
          if(Briscola.getInstance().hasMoreCards())
              for(int i=0;i<Briscola.getInstance().getNumberPlayers();i++)
                cardsDealt.add(Briscola.getInstance().dealCard());
          else
              Briscola.getInstance().dealCards();

        Log.i("Round Finished","Will Deal"+cardsDealt.toString());
        boolean isLastDraw=!Briscola.getInstance().hasMoreCards();
        Log.i("Round Finished","Round finished:Winner:"+Briscola.getInstance().getCurrentPlayer());
        /**
         * Show the animations of passing to the next round
         * and update the scores of the players
         */
        activity.finishRound(Briscola.getInstance().getCurrentPlayer(),cardsDealt,isLastDraw);
        activity.setScores(Briscola.getInstance().getScores());
    }

    /**
     * restart the game
     * kill the current game activity and create a new one
     * with starting configuration same
     * but forget all the moves
     * @param activity activity of the game
     */
    public void onRestart(GameActivity activity){
        activity.finish();
        MainActivity.getInstance().resumeGame(startConfiguration,"");
    }
    /**
     * Start a new Game
     * kill the current game activity and create a new one
     * @param activity activity of the game
     */
    public void onNewGame(GameActivity activity){
        activity.finish();
        //don't save the finished game
        getRepository().deleteCurrentGame();
        MainActivity.getInstance().startNewGame();
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

    /**
     * Undo a move in the current game
     * @param activity activity of the game
     */
    public void onUndo(GameActivity activity) {
        /**
         * Handle the case there are no moves to undo
         */
        if(getMovesPerformed().length()==0)
            Toast.makeText(activity,"No moves to undo",Toast.LENGTH_SHORT).show();
        Log.i("Undo","moves Performed:"+movesPerformed);
        /**
         * Remove moves until it's again the user's turn to play
         */
        while(true){
            movesPerformed=movesPerformed.substring(0,movesPerformed.length()-1);
            try {
                Briscola.getInstance().moveTest(startConfiguration,movesPerformed);
                //check If it's your turn to play
                if(Briscola.getInstance().getCurrentPlayer()==0){
                    activity.flushInterface();
                    state=Briscola.getInstance().getPlayerState(0);
                    activity.buildInterface(state);
                    Log.i("Building Interface","State:"+Briscola.getInstance().toString());
                    activity.setScores(Briscola.getInstance().getScores());
                    return;
                }
            } catch (InvalidCardDescriptionException e) {
                e.printStackTrace();
            } catch (InvalidGameStateException e) {
                e.printStackTrace();
            }
        }
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
