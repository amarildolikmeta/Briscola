package it.polimi.ma.group07.briscola.controller;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by amari on 22-Dec-17.
 */

public class SettingsButtonListener implements View.OnClickListener {
    private GameActivity activity;
    private ArrayList<Integer> skins;
    private ArrayList<String> skinNames;
    ArrayList<ImageView> images;
    int musicOnResourceId,musicOffResourceId,soundEffectOnResourceId,soundEffectOffResourceId;
    boolean soundEffectsOn,musicOn;
    public SettingsButtonListener(GameActivity activity){
        this.activity=activity;
        skins=new ArrayList<>();
        skinNames=new ArrayList<>();
        for(int i=1;i<5;i++){
            skins.add(activity.getResources().getIdentifier("back"+i, "drawable",
                    activity.getPackageName()));
            skinNames.add("back"+i);
        }
        musicOnResourceId=activity.getResources().getIdentifier("music_on", "drawable",
                activity.getPackageName());
        musicOffResourceId=activity.getResources().getIdentifier("music_off", "drawable",
                activity.getPackageName());
        soundEffectOnResourceId=activity.getResources().getIdentifier("effects_on", "drawable",
                activity.getPackageName());
        soundEffectOffResourceId=activity.getResources().getIdentifier("effects_off", "drawable",
                activity.getPackageName());
    }

    @Override
    public void onClick(View v) {
        // get a reference to the already created main layout
        RelativeLayout mainLayout = (RelativeLayout)
              activity.findViewById(R.id.gameView);

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.settings_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        LinearLayout gallery=(LinearLayout) popupView.findViewById(R.id.deck_skins);
        ImageView selectedImage=new ImageView(activity);
        ImageView image;
        int currentSkin=activity.getResources().getIdentifier(activity.getDeckSkin(), "drawable",
                activity.getPackageName());
        selectedImage.setImageResource(currentSkin);
        images=new ArrayList<>();
        gallery.addView(selectedImage);
        deckSkinListener listener=new deckSkinListener();
        for(int i=0;i<skins.size();i++){
            if(skins.get(i)!=currentSkin){
                image=new ImageView(activity);
                image.setImageResource(skins.get(i));
                gallery.addView(image);
                images.add(image);
                image.setOnClickListener(listener);
            }
            else {
                images.add(selectedImage);
                selectedImage.setOnClickListener(listener);
            }
        }
        final Button soundEffectButton=(Button)popupView.findViewById(R.id.sound_effects_button);
        final Button musicButton=(Button)popupView.findViewById(R.id.music_button);
        musicOn=activity.isMusicOn();
        soundEffectsOn=activity.isSoundEffectOn();
        if(musicOn)
            musicButton.setBackgroundResource(musicOnResourceId);
        else
            musicButton.setBackgroundResource(musicOffResourceId);
        if(soundEffectsOn)
            soundEffectButton.setBackgroundResource(soundEffectOnResourceId);
        else
            soundEffectButton.setBackgroundResource(soundEffectOffResourceId);
        soundEffectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soundEffectsOn) {
                    soundEffectsOn = false;
                    activity.setSoundEffects(soundEffectsOn);
                    soundEffectButton.setBackgroundResource(soundEffectOffResourceId);
                }
                else {
                    soundEffectsOn = true;
                    activity.setSoundEffects(soundEffectsOn);
                    soundEffectButton.setBackgroundResource(soundEffectOnResourceId);
                }
            }
        });
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicOn) {
                    musicOn = false;
                    activity.setMusic(musicOn);
                   musicButton.setBackgroundResource(musicOffResourceId);
                }
                else {
                    musicOn = true;
                    activity.setMusic(musicOn);
                    musicButton.setBackgroundResource(musicOnResourceId);
                }
            }
        });
        // show the popup window
        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
        Button dismiss=(Button) popupView.findViewById(R.id.dismissButton);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }
    private class deckSkinListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            int index=images.indexOf(v);
            activity.setDeckSkin(skinNames.get(index));
        }
    }
}
