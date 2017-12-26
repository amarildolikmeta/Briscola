package it.polimi.ma.group07.briscola.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import it.polimi.ma.group07.briscola.MainActivity;
import it.polimi.ma.group07.briscola.R;
import it.polimi.ma.group07.briscola.controller.persistance.DatabaseRepository;
import it.polimi.ma.group07.briscola.controller.persistance.LocalGame;
import it.polimi.ma.group07.briscola.controller.persistance.OnlineGame;

/**
 * Displays statistics about the games played
 */
public class StatisticsFragment extends Fragment {
    String mGameType;
    TextView gamesWon;
    TextView gamesLost;
    TextView gamesPlayed;
    TextView gamesDrawn;
    TextView gamesTerminated;
    public StatisticsFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGameType = getArguments().getString("mGameType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Load the saved state (the list of images and list index) if there is one
        if(savedInstanceState != null) {
            mGameType = savedInstanceState.getString("mGameType");
        }

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        gamesWon=(TextView) rootView.findViewById(R.id.games_won);
        gamesLost=(TextView) rootView.findViewById(R.id.games_lost);
        gamesPlayed=(TextView) rootView.findViewById(R.id.games_played);
        gamesDrawn=(TextView) rootView.findViewById(R.id.games_drawn);
        gamesTerminated=(TextView) rootView.findViewById(R.id.games_terminated);
        int won,played,lost,draw,terminated;
        /**
         * Load and display the appropriate statistics
         */
        if(mGameType != null){
                if(mGameType=="Local"){
                    Log.i("Statistics","Reading Local");
                    won= DatabaseRepository.getInstance().getNrLocalGames(LocalGame.WON);
                    lost= DatabaseRepository.getInstance().getNrLocalGames(LocalGame.LOST);
                    draw= DatabaseRepository.getInstance().getNrLocalGames(LocalGame.DRAWN);
                    terminated=DatabaseRepository.getInstance().getNrLocalGames(LocalGame.TERMINATED);
                    played=DatabaseRepository.getInstance().getNrLocalGames();
                    Log.i("Statistics","Read Local Statistics");
                    }
               else if(mGameType=="Online") {
                    Log.i("Statistics", "Reading Online");
                    won = DatabaseRepository.getInstance().getNrOnlineGames(OnlineGame.WON);
                    lost = DatabaseRepository.getInstance().getNrOnlineGames(OnlineGame.LOST);
                    draw = DatabaseRepository.getInstance().getNrOnlineGames(OnlineGame.DRAWN);
                    terminated=DatabaseRepository.getInstance().getNrOnlineGames(OnlineGame.TERMINATED);
                    played = DatabaseRepository.getInstance().getNrOnlineGames();
                    Log.i("Statistics","Read Online Statistics");

                }
                else{
                    won= 0;
                    lost= 0;
                    draw= 0;
                    played=0;
                    terminated=0;
                    Log.i("Statistics","Failed to read Statistics");
            }
            gamesWon.setText(""+won);
            gamesLost.setText(""+lost);
            gamesDrawn.setText(""+draw);
            gamesPlayed.setText(""+played);
            gamesTerminated.setText(""+terminated);
        }
        return rootView;
    }

    public void setGameType(String mGameType) {
        this.mGameType = mGameType;
    }


    /**
     * Save the current state of this fragment
     */
    @Override
    public void onSaveInstanceState(Bundle currentState) {
        if(mGameType!=null)
            currentState.putString("mGameType", mGameType);
    }

}
