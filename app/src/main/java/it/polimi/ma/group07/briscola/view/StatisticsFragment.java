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
import android.widget.ProgressBar;
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
    ProgressBar gamesWonProgress;
    ProgressBar gamesLostProgress;
    ProgressBar gamesDrawnProgress;
    ProgressBar gamesTerminatedProgress;
    TextView gamesWonPercentage;
    TextView gamesLostPercentage;
    TextView gamesDrawnPercentage;
    TextView gamesTerminatedPercentage;
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

        gamesWonProgress=(ProgressBar) rootView.findViewById(R.id.progress_won);
        gamesLostProgress=(ProgressBar) rootView.findViewById(R.id.progress_lost);
        gamesDrawnProgress=(ProgressBar) rootView.findViewById(R.id.progress_drawn);
        gamesTerminatedProgress=(ProgressBar) rootView.findViewById(R.id.progress_terminated);

        gamesWonPercentage=(TextView) rootView.findViewById(R.id.percentage_games_won);
        gamesLostPercentage=(TextView) rootView.findViewById(R.id.percentage_games_lost);
        gamesDrawnPercentage=(TextView) rootView.findViewById(R.id.percentage_games_drawn);
        gamesTerminatedPercentage=(TextView) rootView.findViewById(R.id.percentage_games_terminated);

        float won,played,lost,draw,terminated;
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
            gamesWon.setText(""+(int)won);
            gamesLost.setText(""+(int)lost);
            gamesDrawn.setText(""+(int)draw);
            gamesPlayed.setText(""+(int)played);
            gamesTerminated.setText(""+(int)terminated);

            gamesWonProgress.setMax((int)played);
            gamesWonProgress.setProgress((int)won);
            gamesLostProgress.setMax((int)played);
            gamesLostProgress.setProgress((int)lost);
            gamesWonProgress.setMax((int)played);
            gamesDrawnProgress.setProgress((int)draw);
            gamesTerminatedProgress.setMax((int)played);
            gamesTerminatedProgress.setProgress((int)terminated);

            int percentageWon,percentageLost,percentageDrawn,percentageTerminated;
            percentageWon=(int)(((won/played))*100);
            percentageLost=(int)((lost/played)*100);
            percentageDrawn=(int)((((float)draw/(float)played))*100);
            percentageTerminated=100-(percentageWon+percentageDrawn+percentageLost);

            gamesWonPercentage.setText(percentageWon+"%");
            gamesLostPercentage.setText(percentageLost+"%");
            gamesDrawnPercentage.setText(percentageDrawn+"%");
            gamesTerminatedPercentage.setText(percentageTerminated+"%");
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
