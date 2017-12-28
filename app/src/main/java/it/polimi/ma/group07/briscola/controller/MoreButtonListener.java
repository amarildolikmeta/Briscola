package it.polimi.ma.group07.briscola.controller;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by amari on 27-Dec-17.
 */

public class MoreButtonListener implements View.OnClickListener {
    GameActivity activity;
    public MoreButtonListener(GameActivity activity) {
        this.activity=activity;
    }

    @Override
    public void onClick(View v) {
        // get a reference to the already created main layout
        RelativeLayout mainLayout = (RelativeLayout)
                activity.findViewById(R.id.settingLayout);

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.single_player_more_layout, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        Log.i("MoreListener","Showing view");
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        // show the popup window
        popupWindow.setAnimationStyle(R.style.Animations_popup);
        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

        Button newGameButton=(Button) popupView.findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(new NewGameListener(activity));

        Button suggestMoveButton=(Button) popupView.findViewById(R.id.hintButton);
        suggestMoveButton.setOnClickListener(new SuggestMoveListener(activity,popupWindow));

        Button aiPlaysButton=(Button) popupView.findViewById(R.id.aiButton);
        aiPlaysButton.setOnClickListener(new AIPlaysListener(activity,popupWindow));
        TextView aiText=popupView.findViewById(R.id.aiText);
        boolean aiPlays=activity.controller.getAI();
        if(aiPlays) {
            aiPlaysButton.setBackgroundResource(R.drawable.person_icon);
            aiText.setText("Play");
        }
    }
}
