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
import it.polimi.ma.group07.briscola.controller.persistance.DatabaseRepository;
import it.polimi.ma.group07.briscola.controller.persistance.LocalGame;
import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;

public class MainActivity extends AppCompatActivity {
    private Button multiplayerButton;
    private Button singlePlayerButton;
    private Button statisticsButton;
    public static Context context;
    public static MediaPlayer backgroundMusic;
    private AlphaAnimation buttonClick;
    private static boolean musicStopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        buttonClick = new AlphaAnimation(1F, 0.8F);
        multiplayerButton = (Button) findViewById(R.id.testButton);
        singlePlayerButton = (Button) findViewById(R.id.singlePlayerButton);
        statisticsButton = (Button) findViewById(R.id.statisticsButton);

        multiplayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("singlePlayer", false);
                startActivity(intent);
            }
        });
        singlePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Briscola.deleteInstance();
                final Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("singlePlayer", true);
                final LocalGame game = DatabaseRepository.getInstance().getCurrentGame();
                Log.i("Main Activity", "Current Game Loaded");
                if (game == null)
                    startActivity(intent);
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("Saved Game");
                    alert.setMessage("Continue Previous Game?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Briscola.createInstance();
                                Briscola.getInstance().moveTest(game.startConfiguration, game.moves);
                                intent.putExtra("movesPerformed", game.moves);
                                intent.putExtra("startConfiguration", game.startConfiguration);
                                startActivity(intent);
                            } catch (InvalidCardDescriptionException e) {
                                e.printStackTrace();
                            } catch (InvalidGameStateException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    });

                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Briscola.createInstance();
                            DatabaseRepository.getInstance().deleteCurrentGame();
                            intent.putExtra("movesPerformed", "");
                            intent.putExtra("startConfiguration", Briscola.getInstance().toString());
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });

                    alert.show();
                }
            }
        });
        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });
    }

    public static void stopMusic() {
        backgroundMusic.pause();
        musicStopped = true;
    }

    public static void startMusic() {
        backgroundMusic.seekTo(0);
        backgroundMusic.start();
        musicStopped = false;
    }

    public static boolean isMusicStopped() {
        return musicStopped;
    }

    @Override
    public void onStart() {
        if (backgroundMusic == null) {
            backgroundMusic = MediaPlayer.create(context, R.raw.background_music);
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.15f, 0.15f);
        }
        musicStopped=true;
        super.onStart();
    }

}
