package it.polimi.ma.group07.briscola.controller;

import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import java.util.ArrayList;


import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.MainActivity;
import it.polimi.ma.group07.briscola.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Displays  a popup window to change the app settings
 */

public class SettingsButtonListener implements View.OnClickListener {
    private final AlphaAnimation buttonClick;
    /**
     * Activity from where it's called
     * can be Game Activity or MainActivity
     */
    private AppCompatActivity activity;
    /**
     * List of the possible skins of the cards
     * both as resource id and as skinNames
     */
    private ArrayList<Integer> skins;
    private ArrayList<String> skinNames;
    /**
     * List of the images of cards in the gallery
     */
    ArrayList<ImageView> images;
    /**
     * Id of the different icons for the music and sound
     */
    int musicOnResourceId,musicOffResourceId,soundEffectOnResourceId,soundEffectOffResourceId;
    boolean soundEffectsOn,musicOn;
    /**
     * Two different dimensions for the cards to be displayed
     * the selected card is slightly bigger to emphasize the
     * fact it's selected
     */
    private int dimensionInDp;
    private int selectedDimensionInDp;
    private int selectedSpeedDimensionInDp;
    private int speedDimensionInDp;
    /**
     * Buttons to change the animation speed
     *
     */
    private ArrayList<Button> animationButtons;
    private ArrayList<Integer> animationSpeeds;
    public SettingsButtonListener(AppCompatActivity activity){
        this.activity= activity;
        skins=new ArrayList<>();
        skinNames=new ArrayList<>();
        buttonClick = new AlphaAnimation(1F, 0.8F);
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
        animationButtons=new ArrayList<>();
        animationSpeeds=new ArrayList<>();
    }
    @Override
    public void onClick(View v) {
        /**
         * Play simple animation and sound
         */
        v.startAnimation(buttonClick);
        SettingsController.getInstance().playButtonClickSound();
        // get a reference to the already created main layout
        RelativeLayout mainLayout = (RelativeLayout)
              activity.findViewById(R.id.settingLayout);

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
        int currentSkin=activity.getResources().getIdentifier(SettingsController.getInstance().getDeckSkin(), "drawable",
                activity.getPackageName());

        selectedImage.setImageResource(currentSkin);
        images=new ArrayList<>();
        gallery.addView(selectedImage);
        int dimensionInPixel = 100;
        int selectedDimensionInPixel=120;
        int speedDimensionInPixel=40;
        int selectedSpeedDimensionInPixel=60;
        dimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimensionInPixel, activity.getResources().getDisplayMetrics());
        selectedDimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, selectedDimensionInPixel, activity.getResources().getDisplayMetrics());
        speedDimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, speedDimensionInPixel, activity.getResources().getDisplayMetrics());
        selectedSpeedDimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, selectedSpeedDimensionInPixel, activity.getResources().getDisplayMetrics());

        selectedImage.getLayoutParams().height = selectedDimensionInDp;
        selectedImage.getLayoutParams().width = selectedDimensionInDp;
        selectedImage.requestLayout();
        deckSkinListener listener=new deckSkinListener();

        for(int i=0;i<skins.size();i++){
            if(skins.get(i)!=currentSkin){
                image=new ImageView(activity);
                image.setImageResource(skins.get(i));
                gallery.addView(image);
                images.add(image);
                image.getLayoutParams().height = dimensionInDp;
                image.getLayoutParams().width = dimensionInDp;
                image.requestLayout();
                image.setOnClickListener(listener);
            }
            else {
                images.add(selectedImage);
                selectedImage.setOnClickListener(listener);
            }
        }
        final Button soundEffectButton=(Button)popupView.findViewById(R.id.sound_effects_button);
        final Button musicButton=(Button)popupView.findViewById(R.id.music_button);
        musicOn=SettingsController.getInstance().getBackgroundMusic();
        soundEffectsOn=SettingsController.getInstance().getSoundEffects();
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
                    SettingsController.getInstance().setSoundEffects(soundEffectsOn);
                    soundEffectButton.setBackgroundResource(soundEffectOffResourceId);
                }
                else {
                    soundEffectsOn = true;
                    SettingsController.getInstance().setSoundEffects(soundEffectsOn);
                    soundEffectButton.setBackgroundResource(soundEffectOnResourceId);
                }
            }
        });
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicOn) {
                    musicOn = false;
                    SettingsController.getInstance().setBackgroundMusic(musicOn);
                   musicButton.setBackgroundResource(musicOffResourceId);
                }
                else {
                    musicOn = true;
                    SettingsController.getInstance().setBackgroundMusic(musicOn);
                    musicButton.setBackgroundResource(musicOnResourceId);
                }
            }
        });
        Log.i("Settings","HandlingAnimations");
        //handle animationButtons
        animationButtons.add((Button)popupView.findViewById(R.id.animation_slow_button));
        animationButtons.add((Button)popupView.findViewById(R.id.animation_medium_button));
        animationButtons.add((Button)popupView.findViewById(R.id.animation_fast_button));
        Log.i("Settings","Loaded Buttons");
        animationSpeeds.add(SettingsController.ANIMATION_SLOW);
        animationSpeeds.add(SettingsController.ANIMATION_MEDIUM);
        animationSpeeds.add(SettingsController.ANIMATION_FAST);
        Log.i("Settings","Loaded speeds");
        AnimationListener animationListener=new AnimationListener();

        for(Button b:animationButtons)
            b.setOnClickListener(animationListener);
        Log.i("Settings","Set Listeners");
        Button selectedButton=animationButtons.get(
                animationSpeeds.indexOf(SettingsController.getInstance().getAnimationSpeed()));
        selectedButton.getLayoutParams().height = selectedSpeedDimensionInDp;
        selectedButton.getLayoutParams().width = selectedSpeedDimensionInDp;
        selectedButton.requestLayout();

        Log.i("Settings","Showing PopUp Window");
        // show the popup window
        popupWindow.setAnimationStyle(R.style.Animations_popup);
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
            ImageView im=(ImageView) v;
            im.getLayoutParams().height = selectedDimensionInDp;
            im.getLayoutParams().width = selectedDimensionInDp;
            im.requestLayout();
            for(ImageView i:images){
                if(im!=i){
                    i.getLayoutParams().height = dimensionInDp;
                    i.getLayoutParams().width = dimensionInDp;
                    i.requestLayout();
                }

            }
            int index=images.indexOf(v);
            SettingsController.getInstance().setDeckSkin(skinNames.get(index));
        }
    }
    private class AnimationListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int index=animationButtons.indexOf((Button)v);
            int speed=animationSpeeds.get(index);
            SettingsController.getInstance().setAnimationSpeed(speed);
            for(Button b:animationButtons){
                if(b==(Button)v){
                    b.getLayoutParams().height = selectedSpeedDimensionInDp;
                    b.getLayoutParams().width = selectedSpeedDimensionInDp;
                    b.requestLayout();
                }
                else{
                    b.getLayoutParams().height = speedDimensionInDp;
                    b.getLayoutParams().width = speedDimensionInDp;
                    b.requestLayout();
                }
            }
        }
    }
}
