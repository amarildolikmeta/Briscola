package it.polimi.ma.group07.briscola;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

import java.util.logging.Handler;

import it.polimi.ma.group07.briscola.controller.Coordinator;
import it.polimi.ma.group07.briscola.controller.SettingsButtonListener;
import it.polimi.ma.group07.briscola.controller.persistance.DatabaseRepository;
import it.polimi.ma.group07.briscola.controller.persistance.LocalGame;
import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;

/**
 * Represents the main menu of the game
 * The user can choose to start a new local game
 * to start a multiplayer game using the web server
 * to change the settings of the game
 * or to see statistics about the games played
 */
public class MainActivity extends AppCompatActivity {
    /**
     * The buttons the user can press
     */
    private Button multiplayerButton;
    private Button singlePlayerButton;
    private Button statisticsButton;
    private Button settingsButton;
    /**
     * Context of the application made available statically to be
     * referenced from other classes of the application
     */
    public static Context context;
    /**
     * The background music which can be played or paused from other classes also
     */
    public static MediaPlayer backgroundMusic;
    /**
     * Animation added to the buttons when they are pressed
     * since the background of the buttons is changed
     * they won't animate automatically when they are pressed
     */
    private AlphaAnimation buttonClick;
    private static boolean musicStopped;
    /**
     * static reference to the instance of this activity
     * referenced from controller classes to call methods of this class
     */
    private static MainActivity instance;
    private String deckSkin;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        instance=this;
        buttonClick = new AlphaAnimation(1F, 0.8F);
        musicStopped=true;
        /**
         * load the buttons to add the listeners for each of them
         */
        multiplayerButton = (Button) findViewById(R.id.testButton);
        singlePlayerButton = (Button) findViewById(R.id.singlePlayerButton);
        statisticsButton = (Button) findViewById(R.id.statisticsButton);
        settingsButton=(Button) findViewById(R.id.settingsButton);
        /**
         * Start a multiplayer game
         * setting the #singlePlayer paramaeter
         * of the Intent to false
         */
        multiplayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("singlePlayer", false);
                startActivity(intent);
            }
        });
        /**
         * Start a single player game
         * setting all the parameters of the game apropriately
         */
        singlePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Briscola.deleteInstance();
                /**
                 * Check if there is a saved game to resume
                 */
                final LocalGame game = DatabaseRepository.getInstance().getCurrentGame();
                if (game == null || game.startConfiguration == null || game.moves == null){
                    if(game!=null)
                        Log.i("Main Activity", "Saved Game is Malformed");
                    startNewGame();
            }
                else {
                    /**
                     * If there is a saved game ask the user if they want to resume it
                     */
                    Log.i("Main Activity", "Current Game Loaded");
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("Saved Game");
                    alert.setMessage("Continue Previous Game?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                Log.i("Main Activity", "Resuming Game");
                                Log.i("Main Activity", "Game:"+game.startConfiguration);
                                resumeGame(game.startConfiguration,game.moves);
                            dialog.dismiss();
                        }
                    });

                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("Main Activity", "Starting New Game");
                            /**
                             * delete the saved game and save it's data as terminated
                             */
                            game.state=LocalGame.TERMINATED;
                            DatabaseRepository.getInstance().deleteCurrentGame();
                            DatabaseRepository.getInstance().saveLocalGame(game);
                            startNewGame();
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            }
        });
        /**
         * Open the statistics view
         */
        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });
        /**
         * Open the settings PopUp
         */
        settingsButton.setOnClickListener(new SettingsButtonListener(MainActivity.this));
    }

    /**
     * Stop the background music
     */
    public static void stopMusic() {
        if(backgroundMusic.isPlaying())
            backgroundMusic.pause();
        musicStopped = true;
    }

    /**
     * Start the background music
     */
    public static void startMusic() {
        if(backgroundMusic.isPlaying())
            return;
        backgroundMusic.seekTo(0);
        backgroundMusic.start();
        musicStopped = false;
    }

    public static boolean isMusicStopped() {
        return musicStopped;
    }

    /**
     * Load the background music
     */
    @Override
    public void onStart() {
        if (backgroundMusic != null)
            backgroundMusic.release();
        backgroundMusic = MediaPlayer.create(context, R.raw.background_music);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.15f, 0.15f);

        super.onStart();
    }

    /**
     * Start a new game and pass the appropriate parameters to the Game Activity
     */
    public void startNewGame(){
        if (backgroundMusic != null)
            backgroundMusic.release();
        backgroundMusic = MediaPlayer.create(context, R.raw.background_music);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.15f, 0.15f);
        final Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("singlePlayer", true);
        Briscola.createInstance();
        intent.putExtra("movesPerformed", "");
        intent.putExtra("startConfiguration", Briscola.getInstance().toString());
        startActivity(intent);
    }

    /**
     * Resume a saved game
     * @param startConfiguration String representing the starting state of the game
     * @param movesPerformed String representing the moves performed until now
     */
    public void resumeGame(String startConfiguration,String movesPerformed){
        if (backgroundMusic != null)
            backgroundMusic.release();
        backgroundMusic = MediaPlayer.create(context, R.raw.background_music);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.15f, 0.15f);
        final Intent intent = new Intent(MainActivity.this, GameActivity.class);
        /**
         * Create a new game and apply the moves
         */
        Briscola.createInstance();
        try {
            Briscola.getInstance().moveTest(startConfiguration, movesPerformed);
        } catch (InvalidCardDescriptionException e) {
            e.printStackTrace();
        } catch (InvalidGameStateException e) {
            e.printStackTrace();
        }
        /**
         * pass the parameters and start the game
         */
        intent.putExtra("singlePlayer", true);
        intent.putExtra("movesPerformed", movesPerformed);
        intent.putExtra("startConfiguration", startConfiguration);
        startActivity(intent);
    }

}
