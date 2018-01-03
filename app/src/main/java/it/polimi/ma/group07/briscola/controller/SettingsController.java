package it.polimi.ma.group07.briscola.controller;

import android.content.SharedPreferences;

import it.polimi.ma.group07.briscola.MainActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Manages writing and reading the game settings
 */

public class SettingsController {
    public  static int ANIMATION_SLOW=1200;
    public  static int ANIMATION_MEDIUM=800;
    public  static int ANIMATION_FAST=400;

    private static String MY_PREFERENCES="BRISCOLA_PREFERENCES";
    private static String MUSIC_PREFERENCES="MUSIC";
    private static String SOUND_EFFECTS_PREFERENCES="SOUND_EFFECTS";
    private static String DECK_SKIN_PREFERENCES="DECK_SKIN";
    private static String ANIMATION_SPEED_PREFERENCES="ANIMATION_SPEED";
    /**
     * internal states
     */
    private static boolean backgroundMusicOn;
    private static boolean soundEffectsOn;
    private static String deckSkin;
    private static int animationSpeed;

    private SharedPreferences.Editor editor;
    private static  SettingsController instance;

    public static SettingsController getInstance(){
        if(instance==null)
            instance=new SettingsController();
        return instance;
    }

    /**
     * Load the settings when the object is created
     */
    public SettingsController(){
        SharedPreferences prefs = MainActivity.context.getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        backgroundMusicOn = prefs.getBoolean(MUSIC_PREFERENCES, true);
        soundEffectsOn= prefs.getBoolean(SOUND_EFFECTS_PREFERENCES, true);
        deckSkin=prefs.getString(DECK_SKIN_PREFERENCES, "back1");
        animationSpeed=prefs.getInt(ANIMATION_SPEED_PREFERENCES,ANIMATION_MEDIUM);
    }

    /**
     * Getters for all the states
     */
    public boolean getBackgroundMusic(){
        return backgroundMusicOn;
    }

    public boolean getSoundEffects(){
        return soundEffectsOn;
    }

    public String getDeckSkin(){
        return deckSkin;
    }

    public int getAnimationSpeed(){
        return animationSpeed;
    }
    /**
     * Setters for all the state
     * They all change the internal state of the controller
     * and the saved state in the shared preferences
     */
    public void setDeckSkin(String newSkin){
        deckSkin=newSkin;
        editor = MainActivity.context.getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE).edit();
        editor.putString(DECK_SKIN_PREFERENCES,deckSkin);
        editor.apply();
    }

    public void setBackgroundMusic(boolean mode){
        backgroundMusicOn=mode;
        editor = MainActivity.context.getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE).edit();
        editor.putBoolean(MUSIC_PREFERENCES,backgroundMusicOn);
        editor.apply();
    }

    public void setSoundEffects(boolean mode){
        soundEffectsOn=mode;
        editor = MainActivity.context.getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE).edit();
        editor.putBoolean(SOUND_EFFECTS_PREFERENCES,soundEffectsOn);
        editor.apply();
    }
    public void setAnimationSpeed(int speed){
        animationSpeed=speed;
        editor = MainActivity.context.getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE).edit();
        editor.putInt(ANIMATION_SPEED_PREFERENCES,animationSpeed);
        editor.apply();
    }
}
