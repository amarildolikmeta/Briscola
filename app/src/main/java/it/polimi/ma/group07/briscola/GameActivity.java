package it.polimi.ma.group07.briscola;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import it.polimi.ma.group07.briscola.controller.CardPressedListener;
import it.polimi.ma.group07.briscola.controller.Coordinator;
import it.polimi.ma.group07.briscola.controller.NewGameListener;
import it.polimi.ma.group07.briscola.controller.PileButtonListener;
import it.polimi.ma.group07.briscola.controller.RestartListener;
import it.polimi.ma.group07.briscola.controller.SendDataListener;
import it.polimi.ma.group07.briscola.model.Briscola;

import it.polimi.ma.group07.briscola.model.StateBundle;


public class GameActivity extends AppCompatActivity {

    LinearLayout gameView;
    LinearLayout[] players;
    LinearLayout surface;
    LinearLayout[] playerViews;
    Button[] piles;
    Button briscolaCard;
    Button newGameButton;
    Button restartButton;
    Button movesButton;
    CardPressedListener cardPressedListener;
    boolean singlePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView=(LinearLayout) findViewById(R.id.gameView);
        singlePlayer=getIntent().getExtras().getBoolean("singlePlayer");

        newGameButton=(Button) findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(new NewGameListener(GameActivity.this));

        restartButton=(Button) findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new RestartListener(GameActivity.this));

        movesButton=(Button) findViewById(R.id.movesButton);
        movesButton.setOnClickListener(new SendDataListener(GameActivity.this));

        players=new LinearLayout[2];
        piles=new Button[2];
        playerViews=new LinearLayout[2];

        players[0]=(LinearLayout) findViewById(R.id.player1Cards);
        players[1]=(LinearLayout) findViewById(R.id.player2Cards);

        PileButtonListener pileButtonListener=new PileButtonListener(GameActivity.this);
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

        Briscola game=Briscola.createInstance();
        cardPressedListener=new CardPressedListener(GameActivity.this);
        StateBundle state=game.getGameState();
        buildInterface(state);
        Coordinator.createInstance(game.toString(),singlePlayer);
        Coordinator.getInstance().setState(GameActivity.this,state);
    }


    public void buildInterface(StateBundle state){
        if(state.playableState) {
            playerViews[state.currentPlayer].setBackgroundResource(R.drawable.custom_border);
            playerViews[(state.currentPlayer + 1) % 2].setBackgroundResource(R.drawable.no_border);
        }
        piles[0].setText(state.score1+"");
        piles[1].setText(state.score2+"");
        Log.i("Briscola Scores","Score 1:"+state.score1+"\tScore2:"+state.score2);
        briscolaCard.setText(state.briscola+"\n"+state.deckSize);
        for(int j=0;j<state.hand1.size();j++)
        {
            Button b=new Button(this);
            b.setText(state.hand1.get(j));
            players[0].addView(b);
            b.setOnClickListener(cardPressedListener);
        }
        for(int j=0;j<state.hand2.size();j++)
        {
            Button b=new Button(this);
            b.setText(state.hand2.get(j));
            players[1].addView(b);
            if(!singlePlayer)
                b.setOnClickListener(cardPressedListener);
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

    public LinearLayout[] getPlayerViews() {
        return playerViews;
    }


}
