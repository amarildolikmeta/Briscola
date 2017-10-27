package it.polimi.ma.group07.briscola;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;
import it.polimi.ma.group07.briscola.model.GameState;
import it.polimi.ma.group07.briscola.model.StateBundle;
import it.polimi.ma.group07.briscola.model.helper.HttpRequest;

import static it.polimi.ma.group07.briscola.model.GameState.DRAW;
import static it.polimi.ma.group07.briscola.model.GameState.WON;

public class GameActivity extends AppCompatActivity {
    public static String CONFIGURATION_KEY="entry.1910876714";
    public static String MOVES_KEY="entry.1555000500";
    public static String RESULT_KEY="entry.14507673";
    public static String URL="https://docs.google.com/forms/d/e/1FAIpQLSeonYFOe2lJMWUeb0l6-HubT5j5uAqYYbGMicrFPkmaZUj7yw/formResponse";
    LinearLayout[] players;
    LinearLayout surface;
    LinearLayout[] playerViews;
    Button[] piles;
    Button briscolaCard;
    TextView configuationView;
    TextView movesView;
    Briscola game;
    StateBundle state;
    LinearLayout gameView;
    Button newGameButton;
    Button restartButton;
    Button movesButton;
    boolean gameFinished;
    String startConfiguration;
    String movesPerformed;
    String sendResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameView=(LinearLayout) findViewById(R.id.gameView);
        newGameButton=(Button) findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(newGameListener);
        restartButton=(Button) findViewById(R.id.restartButton);
        restartButton.setOnClickListener(restartListener);
        movesButton=(Button) findViewById(R.id.movesButton);
        movesButton.setOnClickListener(movesListener);
        configuationView=(TextView) findViewById(R.id.configuration);
        movesView=(TextView) findViewById(R.id.moves);
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
        movesView.setText("No moves");
        movesPerformed="";
        configuationView.setText(startConfiguration);
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
            int index=((LinearLayout)b.getParent()).indexOfChild(b);
            GameState s=game.onPerformMove(index);
            movesPerformed+=index;
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
    private View.OnClickListener movesListener = new View.OnClickListener() {
        public void onClick(View v) {

            //Create an object for PostDataTask AsyncTask
            PostDataTask postDataTask = new PostDataTask();

            //execute asynctask
            postDataTask.execute(URL);
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
                    movesPerformed="";
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
                    movesPerformed="";
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
        movesView.setText(movesPerformed);
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

    //AsyncTask to send data as a http POST request
    private class PostDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... contactData) {
            Boolean result = true;
            String url = contactData[0];
            String postBody="";

            try {
                //all values must be URL encoded to make sure that special characters like & | ",etc.
                //do not cause problems
                postBody = CONFIGURATION_KEY+"=" + URLEncoder.encode(startConfiguration,"UTF-8") +
                        "&" + MOVES_KEY + "=" + URLEncoder.encode(movesPerformed,"UTF-8") +
                        "&" + RESULT_KEY + "=" + URLEncoder.encode(game.toString(),"UTF-8");
            } catch (UnsupportedEncodingException ex) {
                result=false;
            }


            try {
			HttpRequest httpRequest = new HttpRequest();
			sendResponse=httpRequest.sendPost(url, postBody);
		}catch (Exception exception){
			result = false;
		}

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){
            //Print Success or failure message accordingly
            Toast.makeText(GameActivity.this,result?"Message successfully sent!":"There was some error in sending message. Please try again after some time.",Toast.LENGTH_LONG).show();
        }

    }
}
