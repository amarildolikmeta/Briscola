package it.polimi.ma.group07.briscola;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
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
    RelativeLayout deck;
    CardPressedListener cardPressedListener;
    boolean singlePlayer;
    FragmentManager fragmentManager;
    public GameController controller;
    private static boolean isActive;
    public boolean isReady;
    public CardViewFragment testFragment;
    private ArrayList<CardViewFragment> surfaceFragments;
    private ArrayList<ArrayList<CardViewFragment>> playerFragments;
    private CardViewFragment deckFragment;
    private CardViewFragment briscolaFragment;


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
        deck=(RelativeLayout) findViewById(R.id.deck);
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
        playerFragments=new ArrayList<>();

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
        int backCardId = getResources().getIdentifier("back1", "drawable",
                getPackageName());
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
            playerFragments.get(0).add(card);
        }

        for (int j = 0; j < opponentSize; j++) {
                CardViewFragment card = new CardViewFragment();
                card.setImageId(backCardId);
                fragmentManager.beginTransaction().add(playerViews[1].getId(), card).commitNow();
                playerFragments.get(1).add(card);
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
            int backCardId = getResources().getIdentifier("back1", "drawable",
                    getPackageName());
            CardViewFragment c=new CardViewFragment();
            c.setImageId(backCardId);
            fragmentManager.beginTransaction().add(deck.getId(),c).commitNow();
            deckFragment=c;
            playerFragments.add(new ArrayList<CardViewFragment>());
            for(int j=0;j<state.hand.size();j++)
            {
                String name="c"+state.hand.get(j).toString().toLowerCase();
                int resourceId = getResources().getIdentifier(name, "drawable",
                        getPackageName());
                CardViewFragment card=new CardViewFragment();
                card.setImageId(resourceId);
                card.setOnCardSelectedListener(cardPressedListener);
                fragmentManager.beginTransaction().add(playerViews[0].getId(),card).commitNow();
                playerFragments.get(0).add(card);
            }
            playerFragments.add(new ArrayList<CardViewFragment>());
            for(int j=0;j<opponentSize;j++)
            {
                CardViewFragment card=new CardViewFragment();
                card.setImageId(backCardId);
                fragmentManager.beginTransaction().add(playerViews[1].getId(),card).commitNow();
                playerFragments.get(1).add(card);
            }
            surfaceFragments=new ArrayList<>();
            for(int j=0;j<state.surface.size();j++)
            {
                String name="c"+state.surface.get(j).toString().toLowerCase();
                int resourceId = getResources().getIdentifier(name, "drawable",
                        getPackageName());
                CardViewFragment card=new CardViewFragment();
                card.setImageId(resourceId);
                fragmentManager.beginTransaction().add(surface.getId(),card).commitNow();
                surfaceFragments.add(card);
            }

                String name="c"+state.briscola.toLowerCase();
                int resourceId = getResources().getIdentifier(name, "drawable",
                        getPackageName());
                CardViewFragment card=new CardViewFragment();
                card.setOnCardSelectedListener(null);
                card.setImageId(resourceId);
                fragmentManager.beginTransaction().add(briscolaCard.getId(),card).commitNow();
                briscolaFragment=card;
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
        for(int i=0;i<playerFragments.size();i++){
            playerFragments.get(i).clear();
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
     * @param cards the cards to be dealt
     * @param player the player that gets the card
     */
    public void drawCard(final ArrayList<String> cards, final int player,final boolean isLastDraw){
        String name="";
        if(player==0){
            name="c"+cards.get(0).toLowerCase();
            Log.i("Dealing","Dealing "+cards.get(0).toLowerCase());
        }
        else
            name="back1";
        final CardViewFragment frag;
        if(!isLastDraw) {
             frag = new CardViewFragment();
            final int resourceId = getResources().getIdentifier("back1", "drawable", getPackageName());
            frag.setImageId(resourceId);
        }
        else
        {
            if(cards.size()==2){
                frag = deckFragment;
            }
            else{
                 frag = briscolaFragment;
            }
        }
        final CardViewFragment newFrag=new CardViewFragment();
        final int cardId = getResources().getIdentifier(name, "drawable", getPackageName());
        newFrag.setImageId(cardId);
        if(!isLastDraw)
            getSupportFragmentManager().beginTransaction().add(deck.getId(),frag).commitNow();
        final View view=frag.getView();
        int deckHeight,playerHeight,deckWidth,playerWidth;
        int[] locationDeck = new int[2];
        deck.getLocationOnScreen(locationDeck);
        int[] locationPlayer=new int[2];
        playerViews[player].getLocationOnScreen(locationPlayer);
        playerHeight = playerViews[player].getHeight();
        deckHeight = deck.getHeight();
        playerWidth = playerViews[player].getWidth();
        deckWidth = deck.getWidth();
        final double translationY=(playerHeight/2+locationPlayer[1])-(deckHeight/2+locationDeck[1]);
        final double translationX=(playerWidth*0.75+locationPlayer[0])-(deckWidth/2+locationDeck[0]);
        final ObjectAnimator animationY = ObjectAnimator.ofFloat(view, "translationY", 0.F, (int) translationY);
        final ObjectAnimator animationX = ObjectAnimator.ofFloat(view, "translationX", 0.F, (int) translationX);
        final AnimatorSet translation = new AnimatorSet();
        translation.play(animationY).with(animationX);
        translation.setDuration(600);
        translation.addListener(new AnimatorListenerAdapter()
            {@Override
            public void onAnimationEnd(Animator animation) {
                getSupportFragmentManager().beginTransaction().remove(frag).commit();
                getSupportFragmentManager().beginTransaction().add(playerViews[player].getId(), newFrag).commit();
                playerFragments.get(player).add(newFrag);
                if(player==0)
                    newFrag.setOnCardSelectedListener(cardPressedListener);
                cards.remove(0);
                if(cards.size()>0) {
                    Log.i("Dealing","Will Deal "+cards.toString());
                    drawCard(cards, (player + 1) % 2, isLastDraw);
                }
                else {
                    isReady = true;
                    controller.onMovePerformed(GameActivity.this);
                }
            }
        });
        if(player==0&&!isLastDraw){
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
            oa1.setInterpolator(new DecelerateInterpolator());
            oa1.setDuration(150);
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    frag.changeImageResource(cardId);
                    final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
                    oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                    oa2.setDuration(150);
                    oa2.start();
                }
            });
            oa1.start();
        }
        translation.start();
    }

    /**
     * Show animation of playing card from hand to surface
     * @param card card played
     * @param player player that plays it
     */
    public  void playCard(final String card, int player,int cardIndex ) {
        isReady=false;
        final CardViewFragment fragment=playerFragments.get(player).get(cardIndex);
        playerFragments.get(player).remove(fragment);
        final View view=fragment.getView();
        final CardViewFragment newFrag=new CardViewFragment();
        final int resourceId = getResources().getIdentifier("c"+card.toLowerCase(), "drawable",
                    getPackageName());
        newFrag.setImageId(resourceId);
        int surfaceHeight,playerHeight,surfaceWidth,playerWidth;
        int[] locationSurface = new int[2];
        surface.getLocationOnScreen(locationSurface);
        int[] locationPlayer=new int[2];
        playerViews[player].getLocationOnScreen(locationPlayer);

        playerHeight = playerViews[player].getHeight();
        surfaceHeight = surface.getHeight();
        playerWidth = playerViews[player].getWidth();
        surfaceWidth = surface.getWidth();
        final double translationY=(surfaceHeight/2+locationSurface[1])-(playerHeight/2+locationPlayer[1]);
        final double translationX=(surfaceWidth/2+locationSurface[0])-(playerWidth/2+locationPlayer[0]);
        final ObjectAnimator animationY = ObjectAnimator.ofFloat(view, "translationY", 0.F, (int) translationY);
        final ObjectAnimator animationX = ObjectAnimator.ofFloat(view, "translationX", 0.F, (int) translationX);
        final AnimatorSet translation = new AnimatorSet();
        translation.play(animationY).with(animationX);
        translation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    getSupportFragmentManager().beginTransaction().add(surface.getId(), newFrag).commit();
                    surfaceFragments.add(newFrag);
                    isReady=true;
                    controller.onMovePerformed(GameActivity.this);
                }
            });
        if(player==1){
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
            oa1.setInterpolator(new DecelerateInterpolator());
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    fragment.changeImageResource(resourceId);
                    final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
                    oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                    oa2.start();
                }
            });
            oa1.start();
        }
            translation.start();
    }

    /**
     * Show animation of the cards moving from the surface to the winner of the round
     * @param winner the player that gets the cards
     */
    public void finishRound(final int winner, final ArrayList<String> dealtCards, final boolean isLastDraw){
        if(surfaceFragments.size()<2)
            return;
        isReady=false;
        int translationY;
        if(winner==0)
            translationY=600;
        else
            translationY=-600;
        final ObjectAnimator animation1 = ObjectAnimator.ofFloat(surfaceFragments.get(0).getView(), "translationY", 0.F, (int) translationY);
        final ObjectAnimator animation2 = ObjectAnimator.ofFloat(surfaceFragments.get(1).getView(), "translationY", 0.F, (int) translationY);
        final AnimatorSet translation = new AnimatorSet();
        translation.play(animation1).with(animation2);
        translation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                for(int i=0;i<surfaceFragments.size();i++)
                    getSupportFragmentManager().beginTransaction().remove(surfaceFragments.get(i)).commit();
                surfaceFragments.clear();
                if(dealtCards.size()>0)
                    GameActivity.this.drawCard(dealtCards,winner,isLastDraw);
                else {
                    isReady = true;
                    controller.onMovePerformed(GameActivity.this);
                }
            }
        });
        translation.start();
    }


    public int indexOfFragment(CardViewFragment card) {
        return playerFragments.get(0).indexOf(card);
    }
}
