package it.polimi.ma.group07.briscola;

import android.app.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import it.polimi.ma.group07.briscola.controller.CardPressedListener;
import it.polimi.ma.group07.briscola.controller.Coordinator;
import it.polimi.ma.group07.briscola.controller.NewGameListener;
import it.polimi.ma.group07.briscola.controller.PileButtonListener;
import it.polimi.ma.group07.briscola.controller.RestartListener;
import it.polimi.ma.group07.briscola.controller.SendDataListener;
import it.polimi.ma.group07.briscola.model.Briscola;

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
        Briscola game=Briscola.getInstance();
        cardPressedListener=new CardPressedListener(GameActivity.this);
        StateBundle state=game.getGameState();
        String name="c"+state.briscola.toLowerCase();
        int resourceId = getResources().getIdentifier(name, "drawable",
                getPackageName());

        if(resourceId!=0){
            CardViewFragment card=new CardViewFragment();
            card.setImageId(resourceId);
            fragmentManager.beginTransaction().add(briscolaCard.getId(),card).commit();
        }
        buildInterface(state);
        Coordinator.createInstance(game.toString(),singlePlayer);
        Coordinator.getInstance().setState(GameActivity.this,state);
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

    public void buildInterface(StateBundle state){

        for(int j=0;j<state.hand1.size();j++)
        {
            String name="c"+state.hand1.get(j).toLowerCase();
            int resourceId = getResources().getIdentifier(name, "drawable",
                    getPackageName());
            CardViewFragment card=new CardViewFragment();
            card.setImageId(resourceId);
            card.setOnCardSelectedListener(cardPressedListener);
            fragmentManager.beginTransaction().add(playerViews[0].getId(),card).commitNow();
        }

        for(int j=0;j<state.hand2.size();j++)
        {
            if(singlePlayer){
                CardBackFragment card=new CardBackFragment();
                fragmentManager.beginTransaction().add(playerViews[1].getId(),card).commitNow();
            }

            else{
                String name="c"+state.hand2.get(j).toLowerCase();;
                int resourceId = getResources().getIdentifier(name, "drawable",
                        getPackageName());
                CardViewFragment card=new CardViewFragment();
                card.setImageId(resourceId);
                card.setOnCardSelectedListener(cardPressedListener);
                fragmentManager.beginTransaction().add(playerViews[1].getId(),card).commitNow();
            }
        }

        for(int j=0;j<state.surface.size();j++)
        {
            String name="c"+state.surface.get(j).toLowerCase();
            int resourceId = getResources().getIdentifier(name, "drawable",
                    getPackageName());
            CardViewFragment card=new CardViewFragment();
            card.setImageId(resourceId);
            fragmentManager.beginTransaction().add(surface.getId(),card).commitNow();
        }
        if(state.deckSize>1){
            CardBackFragment card=new CardBackFragment();
            fragmentManager.beginTransaction().add(deck.getId(),card).commitNow();
            Log.i("Build interfaace","Deck");
        }

        if(state.deckSize>0) {
            String name="c"+state.briscola.toLowerCase();
            int resourceId = getResources().getIdentifier(name, "drawable",
                    getPackageName());
            CardViewFragment card=new CardViewFragment();
            card.setOnCardSelectedListener(null);
            card.setImageId(resourceId);
            fragmentManager.beginTransaction().add(briscolaCard.getId(),card).commitNow();
            Log.i("Build interfaace","Briscola");
        }

    }
    public void flushInterface(){
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
}
