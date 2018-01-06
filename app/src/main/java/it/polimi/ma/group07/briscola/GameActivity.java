package it.polimi.ma.group07.briscola;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

import it.polimi.ma.group07.briscola.controller.CardPressedListener;
import it.polimi.ma.group07.briscola.controller.Coordinator;
import it.polimi.ma.group07.briscola.controller.DeckPressedListener;
import it.polimi.ma.group07.briscola.controller.GameController;
import it.polimi.ma.group07.briscola.controller.MoreButtonListener;
import it.polimi.ma.group07.briscola.controller.PileButtonListener;
import it.polimi.ma.group07.briscola.controller.RestartListener;
import it.polimi.ma.group07.briscola.controller.RevealButtonListener;
import it.polimi.ma.group07.briscola.controller.ServerCoordinator;
import it.polimi.ma.group07.briscola.controller.SettingsButtonListener;
import it.polimi.ma.group07.briscola.controller.SettingsController;
import it.polimi.ma.group07.briscola.controller.UndoListener;
import it.polimi.ma.group07.briscola.model.Briscola;

import it.polimi.ma.group07.briscola.model.PlayerState;
import it.polimi.ma.group07.briscola.view.CardViewFragment;

import static it.polimi.ma.group07.briscola.MainActivity.context;

/**
 * Represents the game view for both game modes
 * local and multiplayer
 */
public class GameActivity extends AppCompatActivity  {

    private static int ANIMATION_DURATION=1000;
    private static String MY_PREFERENCES="BRISCOLA_PREFERENCES";
    /**
     *References to different layouts in the game
     */
    RelativeLayout gameView;
    LinearLayout surface;
    LinearLayout[] playerViews;
    RelativeLayout deckView;
    RelativeLayout settingsLayout;
    LinearLayout briscolaCard;
    LinearLayout gameOptions;
    RelativeLayout deck;
    /**
     * Buttons of the interface
     */
    Button revealButton;
    Button restartButton;
    Button settingsButton;
    Button undoButton;
    Button moreButton;
    /**
     * reference to the listener to the cards that are played
     */
    CardPressedListener cardPressedListener;
    public boolean singlePlayer;
    FragmentManager fragmentManager;
    /**
     * Controller of the game
     * Can be:
     * Coordinator in case of local game
     * ServerCoordinator in case of multiplayer
     */
    public GameController controller;
    /**
     * Set to false when animations start
     * set back to true when the animations are finished
     * Done to avoid Exceptions created due to actions
     * taking place before the animations finish
     */
    public boolean isReady;
    /**
     * Reference to all the card fragments present in the interface
     * used below to play the animations of the cards
     */
    private ArrayList<CardViewFragment> surfaceFragments;
    private ArrayList<ArrayList<CardViewFragment>> playerFragments;
    private CardViewFragment deckFragment;
    private CardViewFragment briscolaFragment;
    /**
     * TextViews that display the scores of the players
     */
    private ArrayList<TextView> scoreViews;
    /**
     * Sound Effects sounds
     */
    private MediaPlayer playCardMusic;
    private MediaPlayer dealCardMusic;
    /**
     * State of the settings
     */
    private boolean backgroundMusicOn,soundEffectsOn;
    private String deckSkin;
    /**
     * String representation of the briscola suit
     */
    private String briscola;
    /**
     * Listener to the changes of the game settings
     */
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    /**
     * List of all the animations currently being played
     * Before the activity is destroyed all the animations currently active are ended to avoid
     * Exceptions caused by them
     */
    private ArrayList<ObjectAnimator> animations;
    private ArrayList<AnimatorSet> animationSets;
    /**
     * set to true when the activity is destroyed
     * now new animations will start if this flag is set to true
     */
    private boolean terminated;
    /**
     * used to make AI play online games
     */
    public boolean aiPlays;
    /**
     * Start the music if the settings are on
     * and load the saved settings
     */
    @Override
    public void onStart(){
        super.onStart();
        deckSkin=SettingsController.getInstance().getDeckSkin();
        backgroundMusicOn=SettingsController.getInstance().getBackgroundMusic();
        soundEffectsOn=SettingsController.getInstance().getSoundEffects();
        ANIMATION_DURATION=SettingsController.getInstance().getAnimationSpeed();
        if(backgroundMusicOn)
            MainActivity.startMusic();
        else
            MainActivity.stopMusic();
        Log.i("Game Activity onStart","backgroundMusic "+backgroundMusicOn);
        Log.i("Game Activity onStart","SoundEffects"+soundEffectsOn);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Log.i("Game Activity ","Restarting Interface");
        terminated=false;
        PlayerState state=controller.getState();
        buildInterface(state);
    }
    /**
     * Stop the music when the activity stops
     * Manages the cases when the activity is killed or put on background
     */
    @Override
    public void onStop(){
        stopAnimations();
        if(!controller.isRestarting())
            MainActivity.stopMusic();
        super.onStop();
    }

    /**
     * Build the interface and create the controllers
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Game Activity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        fragmentManager=getSupportFragmentManager();

        terminated=false;
        aiPlays=false;
        gameView=(RelativeLayout) findViewById(R.id.gameView);
        /**
         * Keep screen from turning off automatically
         */
        gameView.setKeepScreenOn(true);

        singlePlayer=getIntent().getExtras().getBoolean("singlePlayer");
        briscolaCard=(LinearLayout) findViewById(R.id.briscolaCard);
        deck=(RelativeLayout) findViewById(R.id.deck);
        deckView=(RelativeLayout) findViewById(R.id.deckView);

        settingsButton=(Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new SettingsButtonListener(GameActivity.this));
        revealButton=(Button) findViewById(R.id.revealButton);
        revealButton.setOnClickListener(new RevealButtonListener(GameActivity.this));
        undoButton=(Button) findViewById(R.id.undoButton);
        undoButton.setOnClickListener(new UndoListener(GameActivity.this));
        restartButton=(Button) findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new RestartListener(GameActivity.this));
        moreButton=(Button) findViewById(R.id.moreButton);
        moreButton.setOnClickListener(new MoreButtonListener(GameActivity.this));

        playerViews=new LinearLayout[2];
        gameOptions=(LinearLayout) findViewById(R.id.gameOptions);
        /**
         * Get the references to the point displayer
         * and add the listeners to show the card collected
         */
        scoreViews=new ArrayList<>();
        scoreViews.add((TextView) findViewById(R.id.score1));
        scoreViews.get(0).setOnClickListener(new PileButtonListener(GameActivity.this));
        scoreViews.add((TextView) findViewById(R.id.score2));
        scoreViews.get(1).setOnClickListener(new PileButtonListener(GameActivity.this));

        playerViews[0]=(LinearLayout)findViewById(R.id.player1View);
        playerViews[1]=(LinearLayout)findViewById(R.id.player2View);

        settingsLayout=(RelativeLayout) findViewById(R.id.settingLayout);

        surface=(LinearLayout) findViewById(R.id.surface);
        Log.i("Game Activity","Views found");
        cardPressedListener=new CardPressedListener(GameActivity.this);
        playerFragments=new ArrayList<>();
        playCardMusic= MediaPlayer.create(context, R.raw.play_card);
        dealCardMusic= MediaPlayer.create(context, R.raw.deal_card);

        animations=new ArrayList<>();
        animationSets=new ArrayList<>();

        deckSkin="back1";
        deckSkin=SettingsController.getInstance().getDeckSkin();
        backgroundMusicOn=SettingsController.getInstance().getBackgroundMusic();
        soundEffectsOn=SettingsController.getInstance().getBackgroundMusic();
        /**
         * listen to changes to shared preferences and set the sounds and skin appropriately
         */
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                String oldSkin=deckSkin;
                deckSkin=SettingsController.getInstance().getDeckSkin();
                backgroundMusicOn=SettingsController.getInstance().getBackgroundMusic();
                soundEffectsOn=SettingsController.getInstance().getSoundEffects();
                ANIMATION_DURATION=SettingsController.getInstance().getAnimationSpeed();
                if(!(oldSkin==deckSkin))
                    changeCardsSkin(deckSkin);
                if(backgroundMusicOn)
                    MainActivity.startMusic();
                else
                    MainActivity.stopMusic();
            }
        };
        //register listener
        getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE).registerOnSharedPreferenceChangeListener(listener);

        PlayerState state;
        //game just started
        if(savedInstanceState == null){
            if(singlePlayer) {
                /**
                 * Case it's single player
                 * Get the parameters set by the calling activity
                 * and create the controller for the new game
                 */
                Briscola game=Briscola.getInstance();
                state=game.getPlayerState(0);
                String startConfiguration=getIntent().getExtras().getString("startConfiguration");
                controller = Coordinator.createInstance(startConfiguration, singlePlayer);
                Coordinator.getInstance().setState(GameActivity.this, state);
                String movesPerformed=getIntent().getExtras().getString("movesPerformed");
                Coordinator.getInstance().setMoves(movesPerformed);
                int scores[]=Briscola.getInstance().getScores();
                Log.i("Single Player","Starting Game ");
                /**
                 * Lay down the interface
                 */
                startGame(state);
                Log.i("Single Player","Game Started");
                setScores(scores);

            }
            else{
                /**
                 * remove navigations that apply to local game only
                 * undo or restart game don't apply to multiplayer games
                 */
                gameView.removeView(gameOptions);
                LinearLayout bottomView=(LinearLayout) findViewById(R.id.player1);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)bottomView.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                aiPlays = false;
                controller = new ServerCoordinator(aiPlays);
                try {
                    /**
                     * Wait for the new game to start
                     */
                    ((ServerCoordinator) controller).startGame(GameActivity.this);
                    Log.i("Game Activity Online", "Starting Online Game");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            Log.i("GameActivity","Rebuilding Interface; terminated:"+terminated);
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

    /**
     * Ask Confirmation before leaving the game
     */
    @Override
    public void onBackPressed(){

        AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
        alert.setTitle("Quit");
        alert.setMessage("Are you sure you want to quit the game?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },1000);*/
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

    /**
     * Call the controller finishGame method before exiting
     */
    @Override
    public void onDestroy(){
        /**
         * stop the Music
         * stop listening to the changes of the settings
         * set the terminated flag to true
         * and stop all animations currently running
         */
        terminated=true;
        if(!controller.isRestarting())
            MainActivity.stopMusic();
        getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(listener);
        stopAnimations();
        controller.finishGame("abandon");
        Log.i("Game Activity","Destroying Activity");
        super.onDestroy();
    }

    private void stopAnimations() {
        for(ObjectAnimator oa:animations) {
            oa.removeAllListeners();
            if(oa.isRunning())
                oa.cancel();
        }
        for(AnimatorSet as:animationSets) {
            as.removeAllListeners();
            if(as.isRunning())
                as.cancel();
        }
    }

    /**
     * Build an interface without the starting animations
     * used when moves are undone or when the activity comes from being in background
     * @param state State of the game
     */
    public void buildInterface(PlayerState state) {
        if(terminated)
            return;
        int backCardId = getResources().getIdentifier(deckSkin, "drawable",
                getPackageName());
        isReady=false;
        flushInterface();
        int opponentSize=state.opponentHandSize[0];
        playerFragments=new ArrayList<>();
        playerFragments.add(new ArrayList<CardViewFragment>());
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
        playerFragments.add(new ArrayList<CardViewFragment>());
        for (int j = 0; j < opponentSize; j++) {
                CardViewFragment card = new CardViewFragment();
                card.setImageId(backCardId);
                fragmentManager.beginTransaction().add(playerViews[1].getId(), card).commitNow();
                playerFragments.get(1).add(card);
        }
        surfaceFragments=new ArrayList<>();
        for (int j = 0; j < state.surface.size(); j++) {
            String name = "c" + state.surface.get(j).toString().toLowerCase();
            int resourceId = getResources().getIdentifier(name, "drawable",
                    getPackageName());
            CardViewFragment card = new CardViewFragment();
            card.setImageId(resourceId);
            fragmentManager.beginTransaction().add(surface.getId(), card).commitNow();
            surfaceFragments.add(card);
        }
        if(state.deckSize>1){
        CardViewFragment c=new CardViewFragment();
        c.setImageId(backCardId);
        c.setOnCardSelectedListener(new DeckPressedListener(GameActivity.this));
        fragmentManager.beginTransaction().add(deck.getId(),c).commitNow();
        deckFragment=c;
        }

        if (state.deckSize > 0) {
            String name = "c" + state.briscola.toLowerCase();
            int resourceId = getResources().getIdentifier(name, "drawable",
                    getPackageName());
            CardViewFragment card = new CardViewFragment();
            card.setOnCardSelectedListener(null);
            card.setImageId(resourceId);
            fragmentManager.beginTransaction().add(briscolaCard.getId(), card).commitNow();
            briscolaFragment=card;
        }
        isReady=true;
        if(!state.playableState)
        {
            controller.onMovePerformed(GameActivity.this);
        }
    }

    /**
     * Start a game with the opening animation of dealing the cards
     * @param state state of the game
     */
        public void startGame(PlayerState state) {
            if(terminated)
                return;
            if(backgroundMusicOn)
                MainActivity.startMusic();
            isReady=false;
            int opponentSize=state.opponentHandSize[0];
            Log.i("deck skin","Current skin:"+deckSkin);
            int backCardId = getResources().getIdentifier(deckSkin, "drawable",
                    getPackageName());
            CardViewFragment c=new CardViewFragment();
            c.setImageId(backCardId);
            c.setOnCardSelectedListener(new DeckPressedListener(GameActivity.this));
            fragmentManager.beginTransaction().add(deck.getId(),c).commitNow();
            deckFragment=c;
            playerFragments.add(new ArrayList<CardViewFragment>());
            playerFragments.add(new ArrayList<CardViewFragment>());
            surfaceFragments=new ArrayList<>();
            ArrayList<String> cards=new ArrayList<>();
            for(int i=0;i<Math.max(state.hand.size(),state.opponentHandSize[0]);i++){
                if(state.currentPlayer==0){
                    cards.add(state.hand.get(i));
                    if(state.opponentHandSize[0]>i)
                    cards.add("");
                }
                else{
                    cards.add("");
                    if(state.hand.size()>i)
                        cards.add(state.hand.get(i));
                }
            }
            briscola=state.briscola.toLowerCase();
            drawCard(cards,state.currentPlayer,false,true);
            fillSurface(state.surface);

    }

    /**
     * Add cards to the surface
     * Used when the game is resumed from a saved state
     * @param surface list of cards in the surface
     */
    private void fillSurface(final ArrayList<String> surface) {
        if(terminated)
            return;
        if(surface.size()==0)
            return;
        String name="";
        name="c"+surface.get(0).toLowerCase();
        Log.i("Dealing","Dealing "+surface.get(0).toLowerCase());
        final CardViewFragment frag;
        frag = new CardViewFragment();
        final int resourceId = getResources().getIdentifier(deckSkin, "drawable", getPackageName());
        frag.setImageId(resourceId);
        final CardViewFragment newFrag=new CardViewFragment();
        final int cardId = getResources().getIdentifier(name, "drawable", getPackageName());
        newFrag.setImageId(cardId);
        getSupportFragmentManager().beginTransaction().add(deck.getId(),frag).commitNow();
        final View view=frag.getView();
        int deckHeight,surfaceHeight,deckWidth,surfaceWidth;
        int[] locationDeck = new int[2];
        deck.getLocationOnScreen(locationDeck);
        int[] locationSurface=new int[2];
        this.surface.getLocationOnScreen(locationSurface);
        surfaceHeight = this.surface.getHeight();
        deckHeight = deck.getHeight();
        surfaceWidth = this.surface.getWidth();
        deckWidth = deck.getWidth();
        final double translationY=(surfaceHeight/2+locationSurface[1])-(deckHeight/2+locationDeck[1]);
        final double translationX=(surfaceWidth*0.75+locationSurface[0])-(deckWidth/2+locationDeck[0]);
        final ObjectAnimator animationY = ObjectAnimator.ofFloat(view, "translationY", 0.F, (int) translationY);
        final ObjectAnimator animationX = ObjectAnimator.ofFloat(view, "translationX", 0.F, (int) translationX);
        final AnimatorSet translation = new AnimatorSet();
        translation.play(animationY).with(animationX);
        translation.setDuration(ANIMATION_DURATION);
        translation.addListener(new AnimatorListenerAdapter()
        {@Override
        public void onAnimationEnd(Animator animation) {
            animationSets.remove(translation);
            getSupportFragmentManager().beginTransaction().remove(frag).commit();
            getSupportFragmentManager().beginTransaction().add(GameActivity.this.surface.getId(), newFrag).commit();
            surfaceFragments.add(newFrag);
            surface.remove(0);
            if(surface.size()>0) {
                Log.i("Dealing","Will Deal "+surface.toString());
                fillSurface(surface);
            }
        }
        });
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
            oa1.setInterpolator(new DecelerateInterpolator());
            oa1.setDuration(ANIMATION_DURATION/4);
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animations.remove(oa1);
                    frag.changeImageResource(cardId);
                    final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
                    oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                    oa2.setDuration(ANIMATION_DURATION/4);
                    oa2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            animations.remove(oa2);
                        }
                    });
                    if(!terminated) {
                        animations.add(oa2);
                        oa2.start();
                    }
                }
            });
        if(!terminated) {
            animations.add(oa1);
            oa1.start();
            animationSets.add(translation);
            translation.start();
            onDealCardEffect();
        }
    }

    /**
     * Delete all the fragments in the interface , emptying it
     */
    public void flushInterface(){
        if(terminated)
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


    /**
     * Show animation of card from the deck to the player
     * @param cards the cards to be dealt
     * @param player the player that gets the card
     */
    public void drawCard(final ArrayList<String> cards, final int player, final boolean isLastDraw, final boolean isStartGame){
        if(terminated)
            return;
        String name="";
        if(player==0){
            name="c"+cards.get(0).toLowerCase();
            Log.i("Dealing","Dealing "+cards.get(0).toLowerCase());
        }
        else
            name=deckSkin;
        final CardViewFragment frag;
        if(!isLastDraw) {
             frag = new CardViewFragment();
            final int resourceId = getResources().getIdentifier(deckSkin, "drawable", getPackageName());
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
        /**
         * draw last card from the briscola
         */
        if(isLastDraw&&cards.size()==1){
            briscolaCard.getLocationOnScreen(locationDeck);
        }
        final double translationY=(playerHeight/2+locationPlayer[1])-(deckHeight/2+locationDeck[1]);
        final double translationX=(playerWidth*0.75+locationPlayer[0])-(deckWidth/2+locationDeck[0]);
        final ObjectAnimator animationY = ObjectAnimator.ofFloat(view, "translationY", 0.F, (int) translationY);
        final ObjectAnimator animationX = ObjectAnimator.ofFloat(view, "translationX", 0.F, (int) translationX);
        final AnimatorSet translation = new AnimatorSet();
        translation.play(animationY).with(animationX);
        translation.setDuration(ANIMATION_DURATION);
        translation.addListener(new AnimatorListenerAdapter()
            {@Override
            public void onAnimationEnd(Animator animation) {
                animationSets.remove(translation);
                Log.i("Dealing","Card Dealt");
                getSupportFragmentManager().beginTransaction().remove(frag).commit();
                getSupportFragmentManager().beginTransaction().add(playerViews[player].getId(), newFrag).commit();
                playerFragments.get(player).add(newFrag);
                if(player==0)
                    newFrag.setOnCardSelectedListener(cardPressedListener);
                cards.remove(0);
                if(cards.size()>0) {
                    Log.i("Dealing","Will Deal "+cards.toString());
                    drawCard(cards, (player + 1) % 2, isLastDraw,isStartGame);
                }
                else if(!isStartGame){
                    isReady = true;
                    /**
                     * After the animation is done
                     * give back control to the controller
                     */
                    controller.onMovePerformed(GameActivity.this);
                }
                else{
                    isReady = true;
                    drawBriscola();
                }
            }
        });
        if(player==0&&!isLastDraw){
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
            oa1.setInterpolator(new DecelerateInterpolator());
            oa1.setDuration(ANIMATION_DURATION/4);
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animations.remove(oa1);
                    frag.changeImageResource(cardId);
                    final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
                    oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                    oa2.setDuration(ANIMATION_DURATION/4);
                    oa2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            animations.remove(oa2);
                        }
                    });
                    if(!terminated) {
                        animations.add(oa2);
                        oa2.start();
                    }
                }
            });
            if(!terminated) {
                animations.add(oa1);
                oa1.start();
            }
        }
        if(!terminated) {
            Log.i("Dealing", "Starting Animation");
            animationSets.add(translation);
            translation.start();
            onDealCardEffect();
        }
    }

    /**
     * Show animation of the briscola card being drawn from the deck
     */
    private void drawBriscola() {
        if(terminated)
            return;
       String name="c"+briscola;
        final CardViewFragment frag;
        frag = new CardViewFragment();
        final int resourceId = getResources().getIdentifier(deckSkin, "drawable", getPackageName());
        frag.setImageId(resourceId);
        final CardViewFragment newFrag=new CardViewFragment();
        final int cardId = getResources().getIdentifier(name, "drawable", getPackageName());
        newFrag.setImageId(cardId);
        getSupportFragmentManager().beginTransaction().add(deck.getId(),frag).commitNow();

        final View view=frag.getView();
        int deckHeight,destinationHeight,deckWidth,destinationWidth;
        int[] locationDeck = new int[2];
        deck.getLocationOnScreen(locationDeck);
        int[] locationDestination=new int[2];
        briscolaCard.getLocationOnScreen(locationDestination);
        destinationHeight = briscolaCard.getHeight();
        deckHeight = deck.getHeight();

        final double translationY=(destinationHeight/2+locationDestination[1])-(deckHeight/2+locationDeck[1]);
        final ObjectAnimator animationY = ObjectAnimator.ofFloat(view, "translationY", 0.F, (int) translationY);
        final AnimatorSet translation = new AnimatorSet();
        translation.play(animationY);
        translation.setDuration(ANIMATION_DURATION);
        translation.addListener(new AnimatorListenerAdapter()
        {@Override
        public void onAnimationEnd(Animator animation) {
            animationSets.remove(translation);
            getSupportFragmentManager().beginTransaction().remove(frag).commit();
            getSupportFragmentManager().beginTransaction().add(briscolaCard.getId(), newFrag).commit();
            briscolaFragment=newFrag;
            isReady=true;
            controller.onMovePerformed(GameActivity.this);
        }
        });
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
            oa1.setInterpolator(new DecelerateInterpolator());
            oa1.setDuration(ANIMATION_DURATION/4);
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animations.remove(oa1);
                    frag.changeImageResource(cardId);
                    final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
                    oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                    oa2.setDuration(ANIMATION_DURATION/4);
                    oa2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            animations.remove(oa2);
                        }
                    });
                    if(!terminated) {
                        animations.add(oa2);
                        oa2.start();
                    }
                }
            });
        if(!terminated) {
            animations.add(oa1);
            oa1.start();
            animationSets.add(translation);
            translation.start();
            onDealCardEffect();
        }
    }

    /**
     * Show animation of playing card from hand to surface
     * @param card card played
     * @param player player that plays it
     */
    public  void playCard(final String card, int player,int cardIndex ) {
        if(terminated)
            return;
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
        fragment.getView().getLocationOnScreen(locationPlayer);
        playerHeight = playerViews[player].getHeight();
        surfaceHeight = surface.getHeight();
        playerWidth = fragment.getView().getWidth();
        surfaceWidth = surface.getWidth();
        final double translationY=(surfaceHeight/2+locationSurface[1])-(playerHeight/2+locationPlayer[1]);
        double translationX=(surfaceWidth/2+locationSurface[0])-(playerWidth/2+locationPlayer[0]);
        //case its second card played
        if(surfaceFragments.size()>0)
            translationX+=playerWidth;
        final ObjectAnimator animationY = ObjectAnimator.ofFloat(view, "translationY", 0.F, (int) translationY);
        final ObjectAnimator animationX = ObjectAnimator.ofFloat(view, "translationX", 0.F, (int) translationX);
        final AnimatorSet translation = new AnimatorSet();
        translation.setDuration(ANIMATION_DURATION);
        translation.play(animationY).with(animationX);
        translation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animationSets.remove(translation);
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    getSupportFragmentManager().beginTransaction().add(surface.getId(), newFrag).commit();
                    surfaceFragments.add(newFrag);
                    isReady=true;
                    /**
                     * After the animation is done
                     * give back control to the controller
                     */
                    controller.onMovePerformed(GameActivity.this);
                }
            });
        if(player==1){
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
            oa1.setInterpolator(new DecelerateInterpolator());
            oa1.setDuration(ANIMATION_DURATION/4);
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animations.remove(oa1);
                    fragment.changeImageResource(resourceId);
                    final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
                    oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                    oa2.setDuration(ANIMATION_DURATION/4);
                    oa2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            animations.remove(oa2);
                        }
                    });
                    if(!terminated) {
                        animations.add(oa2);
                        oa2.start();
                    }
                }
            });
            if(!terminated) {
                animations.add(oa1);
                oa1.start();
            }
        }
        if(!terminated) {
            animationSets.add(translation);
            translation.start();
            onPlayCardEffect();
        }
    }

    /**
     * Show animation of the cards moving from the surface to the winner of the round
     * @param winner the player that gets the cards
     */
    public void finishRound(final int winner, final ArrayList<String> dealtCards, final boolean isLastDraw){
        if(terminated)
            return;
        if(surfaceFragments.size()<2)
            return;
        isReady=false;
        int translationY;
        if(winner==0)
            translationY=900;
        else
            translationY=-900;

        final ObjectAnimator animation1 = ObjectAnimator.ofFloat(surfaceFragments.get(0).getView(), "translationY", 0.F, (int) translationY);
        final ObjectAnimator animation2 = ObjectAnimator.ofFloat(surfaceFragments.get(1).getView(), "translationY", 0.F, (int) translationY);
        final AnimatorSet translation = new AnimatorSet();
        translation.setDuration(ANIMATION_DURATION);
        translation.play(animation1).with(animation2);
        translation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animationSets.remove(translation);
                for(int i=0;i<surfaceFragments.size();i++)
                    getSupportFragmentManager().beginTransaction().remove(surfaceFragments.get(i)).commit();
                surfaceFragments.clear();
                if(dealtCards.size()>0)
                    GameActivity.this.drawCard(dealtCards,winner,isLastDraw,false);
                else {
                    isReady = true;
                    /**
                     * After the animation is done
                     * give back control to the controller
                     */
                    controller.onMovePerformed(GameActivity.this);
                }
            }
        });
        if(!terminated) {
            animationSets.add(translation);
            translation.start();
            onPlayCardEffect();
        }
    }

    /**
     * Get the index of a given fragment
     * used by the controller to determine the index of the card just played
     * @param card fragment
     * @return index of fragment in the player hand
     */
    public int indexOfFragment(CardViewFragment card) {
        return playerFragments.get(0).indexOf(card);
    }

    /**
     * Play the sound effect of the card being played
     */
    private void onPlayCardEffect() {
        if(!soundEffectsOn)
            return;
        if(playCardMusic!=null)
            playCardMusic.release();
        playCardMusic= MediaPlayer.create(context, R.raw.play_card);
        playCardMusic.start();
    }

    /**
     * Play sound effect of card being drawn from deck
     */
    private void onDealCardEffect() {
        if(!soundEffectsOn)
            return;
        if(dealCardMusic!=null)
            dealCardMusic.release();
        dealCardMusic= MediaPlayer.create(context, R.raw.deal_card);
        dealCardMusic.start();
    }

    /**
     * Display the scores of the player
     * @param scores the scores of the players
     */
    public void setScores(int scores[]){
        for(int i=0;i<scoreViews.size();i++)
            scoreViews.get(i).setText(scores[i]+"");
    }

    /**
     * Change the skin of the cards that are reversed
     * @param newSkin the name of the new skin
     */
    public void changeCardsSkin(String newSkin){
        deckSkin=newSkin;
        //load the resource Id
        int backCardId = getResources().getIdentifier(deckSkin, "drawable",
                getPackageName());
        /**
         * Change the skin of the cards of the opponent
         * and the deck if there are cards on the deck
         */
        for(CardViewFragment c:playerFragments.get(1))
            c.changeImageResource(backCardId);
        if(deckFragment!=null)
            deckFragment.changeImageResource(backCardId);
    }

    public void revealCards(ArrayList<String> opponentCards) {
        isReady=false;
        for(int i=0;i<playerFragments.get(1).size();i++){
            revealCard(i,opponentCards.get(i));
        }
    }
    private void revealCard(int index,String card){
        final CardViewFragment fragment=playerFragments.get(1).get(index);
        final int resourceId=getResources().getIdentifier("c"+card.toLowerCase(), "drawable",
                getPackageName());
        final View view=fragment.getView();
        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
        oa1.setInterpolator(new DecelerateInterpolator());
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animations.remove(oa1);
                fragment.changeImageResource(resourceId);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
                oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                oa2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animations.remove(oa2);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideCard(fragment);
                            }
                        },500);
                    }
                });
                if(!terminated) {
                    animations.add(oa2);
                    oa2.start();
                }
            }
        });
        if(!terminated) {
            animations.add(oa1);
            oa1.start();
        }
    }
    private void hideCard(final CardViewFragment fragment) {
        final int resourceId=getResources().getIdentifier(deckSkin, "drawable",
                getPackageName());
        final View view=fragment.getView();
        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
        oa1.setInterpolator(new DecelerateInterpolator());
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animations.remove(oa1);
                fragment.changeImageResource(resourceId);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
                oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                oa2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animations.remove(oa2);
                        isReady=true;
                    }
                });
                if(!terminated) {
                    animations.add(oa2);
                    oa2.start();
                }
            }
        });
        if(!terminated) {
            animations.add(oa1);
            oa1.start();
        }
    }

    public int getPileIndex(View v) {
        return scoreViews.indexOf(v);
    }
}
