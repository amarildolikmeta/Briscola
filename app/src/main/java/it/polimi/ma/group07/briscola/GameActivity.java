package it.polimi.ma.group07.briscola;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;
import it.polimi.ma.group07.briscola.model.GameState;
import it.polimi.ma.group07.briscola.model.StateBundle;

import static it.polimi.ma.group07.briscola.model.GameState.DRAW;
import static it.polimi.ma.group07.briscola.model.GameState.WON;

public class GameActivity extends AppCompatActivity {
    LinearLayout[] players;
    LinearLayout surface;
    LinearLayout[] playerViews;
    Button[] piles;
    Button briscolaCard;
    Briscola game;
    StateBundle state;
    LinearLayout gameView;
    Button newGameButton;
    Button restartButton;
    boolean gameFinished;
    String startConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameView=(LinearLayout) findViewById(R.id.gameView);
        newGameButton=(Button) findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(newGameListener);
        restartButton=(Button) findViewById(R.id.restartButton);
        restartButton.setOnClickListener(restartListener);
        players=new LinearLayout[2];
        piles=new Button[2];
        playerViews=new LinearLayout[2];
        players[0]=(LinearLayout) findViewById(R.id.player1Cards);
        players[1]=(LinearLayout) findViewById(R.id.player2Cards);
        piles[0]=(Button) findViewById(R.id.pile1);
        piles[0].setOnClickListener(pileButtonListener);
        piles[0].setText("0");
        piles[1]=(Button) findViewById(R.id.pile2);
        piles[1].setOnClickListener(pileButtonListener);
        piles[1].setText("0");
        playerViews[0]=(LinearLayout)findViewById(R.id.player1View);
        playerViews[1]=(LinearLayout)findViewById(R.id.player2View);
        surface=(LinearLayout) findViewById(R.id.surface);
        briscolaCard=(Button) findViewById(R.id.briscolaCard);
        game=new Briscola();
        startConfiguration=game.toString();
        state=game.getGameState();
        buildInterface(state);
        gameFinished=false;
    }
    private View.OnClickListener buttonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(gameFinished)
                return;
            LinearLayout playerView=(LinearLayout) v.getParent().getParent();
            int playerIndex=((LinearLayout)playerView.getParent()).indexOfChild(playerView);
            if(playerIndex==0&&state.currentPlayer==1 || playerIndex==2 && state.currentPlayer==0){
                return ;
            }
            Button b=(Button) v;
            GameState s=game.onPerformMove(((LinearLayout)b.getParent()).indexOfChild(b));
            state=game.getGameState();
            if(s==WON || s==DRAW)
            {
                gameFinished=true;
                AlertDialog alertDialog = new AlertDialog.Builder(GameActivity.this).create();
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
            flushInterface();
            buildInterface(state);
        }
    };
    private View.OnClickListener pileButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            AlertDialog alertDialog = new AlertDialog.Builder(GameActivity.this).create();
            alertDialog.setTitle("Pile");
            String message="No cards in Pile";
            LinearLayout playerView=(LinearLayout) v.getParent();
            ArrayList<String> pile;
            if(((LinearLayout)playerView.getParent()).indexOfChild(playerView)==0)
            {
                pile=state.pile1;
            }
            else
            {
                pile=state.pile2;
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
    };
    private View.OnClickListener newGameListener = new View.OnClickListener() {
        public void onClick(View v) {
            AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
            alert.setTitle("New Game?");
            alert.setMessage("Are you sure you want to start a New Game?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    game=new Briscola();
                    state=game.getGameState();
                    flushInterface();
                    gameFinished=false;
                    buildInterface(state);
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
    };
    private View.OnClickListener restartListener = new View.OnClickListener() {
        public void onClick(View v) {
            AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
            alert.setTitle("Restart Game?");
            alert.setMessage("Are you sure you want to restart the game?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        game=new Briscola(startConfiguration);
                    } catch (InvalidGameStateException e) {
                        e.printStackTrace();
                    } catch (InvalidCardDescriptionException e) {
                        e.printStackTrace();
                    }
                    state=game.getGameState();
                    flushInterface();
                    gameFinished=false;
                    buildInterface(state);
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
    };
    private void buildInterface(StateBundle state){
        playerViews[state.currentPlayer].setBackgroundResource(R.drawable.custom_border);
        playerViews[(state.currentPlayer+1)%2].setBackgroundResource(R.drawable.no_border);
        piles[0].setText(state.score1+"");
        piles[1].setText(state.score2+"");
        briscolaCard.setText(state.briscola);
        for(int j=0;j<state.hand1.size();j++)
        {
            Button b=new Button(this);
            b.setText(state.hand1.get(j));
            players[0].addView(b);
            b.setOnClickListener(buttonListener);
        }
        for(int j=0;j<state.hand2.size();j++)
        {
            Button b=new Button(this);
            b.setText(state.hand2.get(j));
            players[1].addView(b);
            b.setOnClickListener(buttonListener);
        }
        for(int j=0;j<state.surface.size();j++)
        {
            Button b=new Button(this);
            b.setText(state.surface.get(j));
            surface.addView(b);
        }
    }
    public void flushInterface(){
        players[0].removeAllViews();
        players[1].removeAllViews();
        surface.removeAllViews();
    }
}
