package it.polimi.ma.group07.briscola.controller;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Handler for the undo button
 */

public class UndoListener implements View.OnClickListener {
    private GameActivity activity;

    public UndoListener(GameActivity activity){
        this.activity=activity;
    }
    @Override
    public void onClick(View v) {
        /**
         * Don't undo moves if AI is playing
         */
        if(activity.controller.getAI()){
            Toast.makeText(activity,"Disable AI First",Toast.LENGTH_SHORT).show();
            return;}
        /**
         * wait if the player doesn't have the turn
         * avoids exceptions due to animations being performed
         */
        if(!activity.isReady||!activity.controller.isPlayable()){
            Toast.makeText(activity,"Wait Your Turn",Toast.LENGTH_SHORT).show();
            return;}
        /**
         * Handle the case there are no moves to undo
         */
        if(((Coordinator)activity.controller).getMovesPerformed().length()==0)
            Toast.makeText(activity,"No moves to undo",Toast.LENGTH_SHORT).show();
        else
        ((Coordinator)activity.controller).onUndo(activity);
    }
}
