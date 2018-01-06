package it.polimi.ma.group07.briscola.controller;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.R;
import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.PlayerState;
import it.polimi.ma.group07.briscola.model.StateBundle;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Displays the cards collected by each player
 */

public class PileButtonListener implements View.OnClickListener {
    private GameActivity activity;

    public PileButtonListener(GameActivity activity){
        this.activity=activity;
    }

    @Override
    public void onClick(View v) {
        /**
         * Play Button Sound
         */
        SettingsController.getInstance().playButtonClickSound();
        Log.i("PileListener","Displaying player Piles");
        ArrayList<String> pile;
        PlayerState state=activity.controller.getState();
        int index=activity.getPileIndex(v);
        if(index==0)
            pile=state.ownPile;
        else
            pile=state.opponentPiles.get(0);
        if(pile.size()==0)
        {
            Toast.makeText(activity,"No cards to show",Toast.LENGTH_SHORT).show();
            return;
        }
        // get a reference to the already created main layout
        RelativeLayout mainLayout = (RelativeLayout)
                activity.findViewById(R.id.settingLayout);

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.deck_popup, null);
        TextView textView=popupView.findViewById(R.id.cardsLabel);
        textView.setText("Your Card Pile");
        if(index==1)
            textView.setText("Opponent Card pile");
        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        /**
         * lets taps outside the popup also dismiss it
         */
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        LinearLayout gallery=popupView.findViewById(R.id.deck_cards);
        int dimensionInPixel = 100;
        int dimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimensionInPixel, activity.getResources().getDisplayMetrics());
        ImageView image;
        int imageId;
        for(int i=0;i<pile.size();i++){
            image=new ImageView(activity);
            imageId=activity.getResources().getIdentifier("c"+pile.get(i).toLowerCase(), "drawable",
                    activity.getPackageName());
            image.setImageResource(imageId);
            gallery.addView(image);
            image.getLayoutParams().height = dimensionInDp;
            image.getLayoutParams().width = dimensionInDp;
            image.requestLayout();
        }
        // show the popup window
        popupWindow.setAnimationStyle(R.style.Animations_popup);
        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
    }
}
