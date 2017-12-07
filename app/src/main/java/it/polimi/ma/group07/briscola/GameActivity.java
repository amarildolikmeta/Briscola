package it.polimi.ma.group07.briscola;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.List;

import it.polimi.ma.group07.briscola.controller.CardPressedListener;
import it.polimi.ma.group07.briscola.controller.Coordinator;
import it.polimi.ma.group07.briscola.controller.GameController;
import it.polimi.ma.group07.briscola.controller.NewGameListener;
import it.polimi.ma.group07.briscola.controller.PileButtonListener;
import it.polimi.ma.group07.briscola.controller.RestartListener;
import it.polimi.ma.group07.briscola.controller.SendDataListener;
import it.polimi.ma.group07.briscola.controller.ServerCoordinator;
import it.polimi.ma.group07.briscola.model.Briscola;

import it.polimi.ma.group07.briscola.model.Player;
import it.polimi.ma.group07.briscola.model.PlayerState;
import it.polimi.ma.group07.briscola.model.StateBundle;
import it.polimi.ma.group07.briscola.view.CardBackFragment;
import it.polimi.ma.group07.briscola.view.CardViewFragment;

import static java.security.AccessController.getContext;


public class GameActivity extends AppCompatActivity  {

    RelativeLayout gameView;
    LinearLayout surface;
    LinearLayout[] playerViews;
    LinearLayout deckView;
    Button newGameButton;
    Button restartButton;
    Button movesButton;
    LinearLayout briscolaCard;
    LinearLayout deck;
    CardPressedListener cardPressedListener;
    boolean singlePlayer;
    FragmentManager fragmentManager;
    public GameController controller;
    private static boolean isActive;
    public boolean isReady;
    @Override
    public void onStart(){
        super.onStart();
        isActive=true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Game Activity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        fragmentManager=getSupportFragmentManager();

        gameView=(RelativeLayout) findViewById(R.id.gameView);
        singlePlayer=getIntent().getExtras().getBoolean("singlePlayer");
        briscolaCard=(LinearLayout) findViewById(R.id.briscolaCard);
        deck=(LinearLayout) findViewById(R.id.deck);
        deckView=(LinearLayout) findViewById(R.id.deckView);

        newGameButton=(Button) findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(new NewGameListener(GameActivity.this));

        restartButton=(Button) findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new RestartListener(GameActivity.this));

        movesButton=(Button) findViewById(R.id.movesButton);
        movesButton.setOnClickListener(new SendDataListener(GameActivity.this));

        playerViews=new LinearLayout[2];

        PileButtonListener pileButtonListener=new PileButtonListener(GameActivity.this);

        playerViews[0]=(LinearLayout)findViewById(R.id.player1View);
        playerViews[1]=(LinearLayout)findViewById(R.id.player2View);

        surface=(LinearLayout) findViewById(R.id.surface);
        Log.i("Game Activity","Views found");
        cardPressedListener=new CardPressedListener(GameActivity.this);


        PlayerState state;
        //game just started
        if(savedInstanceState == null){
            if(singlePlayer) {
                Briscola game=Briscola.getInstance();
                state=game.getPlayerState(0);
                String startConfiguration=getIntent().getExtras().getString("startConfiguration");
                controller = Coordinator.createInstance(startConfiguration, singlePlayer);
                Coordinator.getInstance().setState(GameActivity.this, state);
                String movesPerformed=getIntent().getExtras().getString("movesPerformed");
                Coordinator.getInstance().setMoves(movesPerformed);
                String name="c"+state.briscola.toLowerCase();
                int resourceId = getResources().getIdentifier(name, "drawable",
                        getPackageName());
                if(resourceId!=0){
                    CardViewFragment card=new CardViewFragment();
                    card.setImageId(resourceId);
                    fragmentManager.beginTransaction().add(briscolaCard.getId(),card).commit();
                }
                startGame(state);
            }
            else{
                controller=new ServerCoordinator();
                try {
                    ((ServerCoordinator)controller).startGame(GameActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            state=controller.getState();
            buildInterface(state);
        }
    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        savedInstanceState.putString("configuration",Briscola.getInstance().toString());
        // etc.
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
        alert.setTitle("Quit");
        alert.setMessage("Are you sure you want to quit the game?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
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
    @Override
    public void onDestroy(){
        isActive=false;
        controller.finishGame("abandon");
        super.onDestroy();
    }
    public void buildInterface(PlayerState state) {
        if(!isActive)
            return;
        isReady=false;
        flushInterface();
        int opponentSize=state.opponentHandSize[0];
        for (int j = 0; j < state.hand.size(); j++) {
            String name = "c" + state.hand.get(j).toString().toLowerCase();
            int resourceId = getResources().getIdentifier(name, "drawable",
                    getPackageName());
            CardViewFragment card = new CardViewFragment();
            card.setImageId(resourceId);
            card.setOnCardSelectedListener(cardPressedListener);
            fragmentManager.beginTransaction().add(playerViews[0].getId(), card).commitNow();
        }

        for (int j = 0; j < opponentSize; j++) {
                CardBackFragment card = new CardBackFragment();
                fragmentManager.beginTransaction().add(playerViews[1].getId(), card).commitNow();
        }

        for (int j = 0; j < state.surface.size(); j++) {
            String name = "c" + state.surface.get(j).toString().toLowerCase();
            int resourceId = getResources().getIdentifier(name, "drawable",
                    getPackageName());
            CardViewFragment card = new CardViewFragment();
            card.setImageId(resourceId);
            fragmentManager.beginTransaction().add(surface.getId(), card).commitNow();
        }
        if (state.deckSize > 1) {
            CardBackFragment card = new CardBackFragment();
            fragmentManager.beginTransaction().add(deck.getId(), card).commitNow();
            Log.i("Build interfaace", "Deck");
        }

        if (state.deckSize > 0) {
            String name = "c" + state.briscola.toLowerCase();
            int resourceId = getResources().getIdentifier(name, "drawable",
                    getPackageName());
            CardViewFragment card = new CardViewFragment();
            card.setOnCardSelectedListener(null);
            card.setImageId(resourceId);
            fragmentManager.beginTransaction().add(briscolaCard.getId(), card).commitNow();
            Log.i("Build interfaace", "Briscola");
        }
        isReady=true;
    }
        public void startGame(PlayerState state) {
            isReady=false;
            int opponentSize=state.opponentHandSize[0];
            CardBackFragment c=new CardBackFragment();
            fragmentManager.beginTransaction().add(deck.getId(),c).commitNow();

            for(int j=0;j<state.hand.size();j++)
            {
                String name="c"+state.hand.get(j).toString().toLowerCase();
                int resourceId = getResources().getIdentifier(name, "drawable",
                        getPackageName());
                CardViewFragment card=new CardViewFragment();
                card.setImageId(resourceId);
                card.setOnCardSelectedListener(cardPressedListener);
                fragmentManager.beginTransaction().add(playerViews[0].getId(),card).commitNow();
            }

            for(int j=0;j<opponentSize;j++)
            {
                CardBackFragment card=new CardBackFragment();
                fragmentManager.beginTransaction().add(playerViews[1].getId(),card).commitNow();
            }

            for(int j=0;j<state.surface.size();j++)
            {
                String name="c"+state.surface.get(j).toString().toLowerCase();
                int resourceId = getResources().getIdentifier(name, "drawable",
                        getPackageName());
                CardViewFragment card=new CardViewFragment();
                card.setImageId(resourceId);
                fragmentManager.beginTransaction().add(surface.getId(),card).commitNow();
            }

                String name="c"+state.briscola.toLowerCase();
                int resourceId = getResources().getIdentifier(name, "drawable",
                        getPackageName());
                CardViewFragment card=new CardViewFragment();
                card.setOnCardSelectedListener(null);
                card.setImageId(resourceId);
                fragmentManager.beginTransaction().add(briscolaCard.getId(),card).commitNow();

                isReady=true;
    }
    public void flushInterface(){
        if (!isActive)
            return;
        Log.i("Flushing Interface","Flushing");
        List<Fragment> al = getSupportFragmentManager().getFragments();
        if (al == null) {
            // code that handles no existing fragments
            return;
        }

        for (Fragment frag : al)
        {
            try{
                getSupportFragmentManager().beginTransaction().remove(frag).commitNow();
            }
            catch (Exception e){
                Log.i("Flush Error","Error flushing");
            }

        }
        Log.i("Flushing Interface","Flushed");
    }
    public LinearLayout[] getPlayerViews() {
        return playerViews;
    }
    public boolean isSinglePlayer(){
        return singlePlayer;
    }

    /**
     * Show animation of card from the deck to the player
     * @param card the card to be added
     * @param player the player that gets the card
     */
    public void drawCard(String card,int player){

    }

    /**
     * Show animation of playing card from hand to surface
     * @param card card played
     * @param player player that plays it
     */
    public  void playCard(String card,int player){

    }

    /**
     * Show animation of the cards moving from the surface to the winner of the round
     * @param winner the player that gets the cards
     */
    public void finishRound(int winner){

    }
}
