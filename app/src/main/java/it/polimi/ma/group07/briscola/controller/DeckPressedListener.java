package it.polimi.ma.group07.briscola.controller;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.R;
import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.view.CardViewFragment;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Handles the pressing the deck to show the cards in the deck
 */

public class DeckPressedListener implements CardViewFragment.OnCardSelectedListener {
    GameActivity activity;
    public DeckPressedListener(GameActivity activity) {
        this.activity=activity;
    }

    @Override
    public void onCardSelected(CardViewFragment card) {
        if(!activity.singlePlayer)
            return;
        ArrayList<String> deck= Briscola.getInstance().getDeckAsStrings();
        // get a reference to the already created main layout
        RelativeLayout mainLayout = (RelativeLayout)
                activity.findViewById(R.id.settingLayout);

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.deck_popup, null);

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
        for(int i=0;i<deck.size();i++){
            image=new ImageView(activity);
            imageId=activity.getResources().getIdentifier("c"+deck.get(i).toLowerCase(), "drawable",
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
