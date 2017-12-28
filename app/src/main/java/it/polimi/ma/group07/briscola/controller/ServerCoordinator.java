package it.polimi.ma.group07.briscola.controller;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.MainActivity;
import it.polimi.ma.group07.briscola.R;
import it.polimi.ma.group07.briscola.controller.persistance.DataRepository;
import it.polimi.ma.group07.briscola.controller.persistance.DatabaseRepository;
import it.polimi.ma.group07.briscola.controller.persistance.OnlineGame;
import it.polimi.ma.group07.briscola.model.Brain;
import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.Card;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Parser;
import it.polimi.ma.group07.briscola.model.Player;
import it.polimi.ma.group07.briscola.model.PlayerState;

import static android.R.id.message;
import static android.R.id.switch_widget;
import static it.polimi.ma.group07.briscola.R.id.deck;

/**
 * Controller for Online games
 */
public class ServerCoordinator implements GameController {
    private static int TIME_DELAY=1200;
    /**
     * Representation of the state of the game
     * as seen from the user {@link PlayerState}
     */
    private PlayerState state;
    /**
     * the activity fo the game
     */
    private GameActivity activity;
    /**
     * reference to the responses of the API calls
     */
    private JSONObject resultJSON;
    private JSONObject errorJSON;
    /**
     * flag to represent if it's the turn of the user  or no
     */
    private boolean yourTurn;
    /**
     * Index of the player in te game {0 or 1}
     */
    private int playerIndex;
    /**
     * did the user start the current round or no
     */
    private boolean startedRound;
    /**
     * URL of the current game being played
     * used to perform the API calls
     */
    private String gameURL;
    /**
     * scores of the players
     */
    private int scores[];
    /**
     * Object that will apply the rules of the game
     * to determine the winners of each round {@link Brain}
     */
    Brain brain;
    /**
     * counter to how many cards have been played in the game
     */
    private int cardsPlayed;
    /**
     * is the game playable or no
     * set to true after all the animations of the game are performed
     */
    private boolean playable;
    /**
     * used in #onMovePerformed method
     */
    private String lastMove;
    private String response;
    private boolean winner;
    int indexPlayed;
    /**
     * true if the ai is gonna play online
     */
    private boolean aiPlays;

    private Handler handler;
    public ServerCoordinator(boolean aiPlays){
        /**
         * clear AI from previous games data
         */
        AIPlayer.clear();
        this.aiPlays=aiPlays;
        handler=new Handler();
    }
    /**
     * Performs the API call to join a game
     * The call is performed in the background
     * @param activity activity of the game
     * @throws IOException
     */
    public void startGame(GameActivity activity) throws IOException {
            this.activity=activity;
            CreateGameTask task=new CreateGameTask(activity);
            task.execute(activity.getResources().getString(R.string.start_game_endpoint));
    }
    /**
     * Polls the server for the next card of the opponent
     * Performs the API call to get the card played by the opponent
     * The call is performed in the background
     */
    public void pollServer(){
        state.playableState=false;
        GetCardTask getCardTask=new GetCardTask(activity);
        getCardTask.execute(gameURL);
    }

    /**
     * Finishes the current game
     * Performs the API call to get finish the current game
     * The call is performed in the background
     */
    public void finishGame(String reason){
        AIPlayer.clear();
        if(state!=null) {
            Log.i("Server Coordinator", "Terminating game");
            getRepository().saveOnlineGame(new OnlineGame(OnlineGame.TERMINATED));
            DeleteGameTask task = new DeleteGameTask(activity);
            task.execute(gameURL, reason);
        }
    }
    /**
     * Perform a move
     * @param activity reference to the activity of the game
     * @param index index of card tobe played
     */
    @Override
    public void onPerformMove(GameActivity activity, int index) {
        indexPlayed=index;
        String c=state.hand.remove(index);
        Log.i("Server Controller","Playing index "+index+" Card:"+c);
        this.activity=activity;
        state.surface.add(c);
        state.playableState=false;
        playable=false;
        Log.i("Post","About to post");
        /**
         * Perform the APi call on the background
         */
       PostCardTask postCardTask=new PostCardTask(activity);
        postCardTask.execute(gameURL,c);
    }


    @Override
    public DataRepository getRepository() {
        return DatabaseRepository.getInstance();
    }

    /**
     * start a new Game
     * @param activity game activity
     */
    @Override
    public void onNewGame(GameActivity activity) {
            finishGame("abandon");
            activity.finish();
            MainActivity.getInstance().startNewOnlineGame(aiPlays);
    }
    /**
     * called after the animations in the view are finished
     * checks the state of the game and performs the next actions
     * accordingly
     * @param activity the activity of the game
     */
    @Override
    public void onMovePerformed(final GameActivity activity) {
        switch(lastMove){
            case "START_GAME":
                /**
                 * Poll server if it's not your turn to play
                 * otherwise make AI play if it's AI game
                 * or wait for input from user
                 */
                if(!yourTurn)
                    pollServer();
                else if(aiPlays){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onPerformMove(activity, AIPlayer.getMoveFromState(state));
                        }
                    }, TIME_DELAY);
                }
                break;
            case "POST":
                onPostFinished();
                break;
            case "POST_FINISH":
                if(cardsPlayed==40)
                    showWinner();
                else if(winner){
                    state.playableState=true;
                    playable=true;
                    /**
                     * AI makes next move
                     * otherwise wait for move from user
                     */
                    if(aiPlays) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onPerformMove(activity, AIPlayer.getMoveFromState(state));
                            }
                        }, TIME_DELAY);
                    }
                }
                else{
                    state.playableState=false;
                    playable=false;
                    if(cardsPlayed<40)
                        pollServer();
                }
                break;
            case "GET":
                onGetFinished();
                break;
            case "GET_FINISH":
                if(cardsPlayed==40)
                    showWinner();
                else if(winner){
                    state.playableState=true;
                    playable=true;
                    /**
                     * AI makes next move
                     * otherwise wait for move from user
                     */
                    if(aiPlays) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onPerformMove(activity, AIPlayer.getMoveFromState(state));
                            }
                        }, TIME_DELAY);
                    }
                }
                else{
                    state.playableState=false;
                    playable=false;
                    if(cardsPlayed<40)
                        pollServer();
                }
                break;
            default :
                Log.i("Server Controller","Shouldn't be here");

        }
    }

    /**
     * Method is called after the call to the GET API is performed
     * Method checks the state of the game at this moment
     * and finishes the round if necessary
     */
    private void onGetFinished() {
        String myCard=null;
        int winnerCard=0;
        if(state.surface.size()<2) {
            state.playableState = true;
            playable=true;
            state.currentPlayer=playerIndex;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onPerformMove(activity, AIPlayer.getMoveFromState(state));
                }
            }, TIME_DELAY);
        }
        else{
            /**
             * Same logic applied in the model
             * has to be applied again since the server
             * doesn't determine the winner of a round
             */
            state.playableState=false;
            playable=false;
            try {
                Log.i("GET","Determining Winner");
                winnerCard=brain.determineWinnerString(state.surface);
                ArrayList<String> s=new ArrayList<String>(state.surface);
                state.surface=new ArrayList<>();
                final ArrayList<String> cardsDealt=new ArrayList<>();
                try {
                    resultJSON=new JSONObject(response);
                    myCard=resultJSON.getString("card");

                } catch (JSONException e) {
                    Log.i("GET GAME","Card not in response");
                }
                if((startedRound&&winnerCard==0)||(!startedRound&&winnerCard==1)){
                    winner=true;
                    state.ownPile.addAll(s);
                    startedRound=true;
                    state.currentPlayer=playerIndex;
                    scores[0]+=brain.calculatePointsString(s);
                    if(myCard!=null){
                        cardsDealt.add(myCard);
                        cardsDealt.add("");
                    }
                }
                else{
                    winner=false;
                    scores[1]+=brain.calculatePointsString(s);
                    state.opponentPiles.get(0).addAll(s);
                    startedRound=false;
                    state.currentPlayer=(playerIndex+1)%2;
                    if(myCard!=null){
                        cardsDealt.add("");
                        cardsDealt.add(myCard);
                    }
                }
                state.playableState=false;
                playable=false;

                final boolean isLastDraw=state.deckSize<3;
                if(state.deckSize>0)
                    state.deckSize=state.deckSize-2;
                if(myCard!=null){
                    state.hand.add(myCard);
                    state.opponentHandSize[0]++;}
                int winningPlayer=0;
                if(!winner)
                    winningPlayer=1;
                Log.i("Round Finished","Round finished:Winner:"+winningPlayer);
                lastMove="GET_FINISH";
                final int arg0=winningPlayer;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        //play the animation of the round being finished
                        activity.setScores(scores);
                        activity.finishRound(arg0,cardsDealt,isLastDraw);
                    }
                },TIME_DELAY);
            } catch (InvalidCardDescriptionException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Method is called after the call to the POST API is performed
     * Method checks the state of the game at this moment
     * and finishes the round if necessary
     */
    private void onPostFinished() {
        String myCard=null;
        int winnerCard=0;
        if(state.surface.size()<2){
            state.currentPlayer=(state.currentPlayer+1)%2;
            pollServer();
        }
        else{
            //round finished
            try {
                Log.i("Post","Determining Winner");
                /**
                 * Same logic applied in the model
                 * has to be applied again since the server
                 * doesn't determine the winner of a round
                 */
                winnerCard=brain.determineWinnerString(state.surface);
                ArrayList<String> s=new ArrayList<String>(state.surface);
                state.surface=new ArrayList<>();
                final ArrayList<String> cardsDealt=new ArrayList<>();
                try{
                    resultJSON=new JSONObject(response);
                    myCard=resultJSON.getString("card");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String finalMyCard = myCard;
                if((startedRound&&winnerCard==0)||(!startedRound&&winnerCard==1)){
                    state.ownPile.addAll(s);
                    startedRound=true;
                    state.currentPlayer=playerIndex;
                    winner=true;
                    scores[0]+=brain.calculatePointsString(s);
                    if(myCard!=null){
                        cardsDealt.add(myCard);
                        cardsDealt.add("");}
                }
                else{
                    winner=false;
                    scores[1]+=brain.calculatePointsString(s);
                    state.opponentPiles.get(0).addAll(s);
                    startedRound=false;
                    state.currentPlayer=(playerIndex+1)%2;
                    if(myCard!=null){
                        cardsDealt.add("");
                        cardsDealt.add(myCard);}
                }
                state.playableState=false;
                playable=false;

                final boolean isLastDraw=state.deckSize<3;
                if(state.deckSize>0)
                    state.deckSize=state.deckSize-2;
                if(finalMyCard!=null){
                    state.hand.add(finalMyCard);
                    state.opponentHandSize[0]++;}
                int winningPlayer=0;
                if(!winner)
                    winningPlayer=1;
                Log.i("Round Finished","Round finished:Winner:"+winningPlayer);
                lastMove="POST_FINISH";
                final int arg0=winningPlayer;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        //show the animation of the round finishing
                        activity.setScores(scores);
                        activity.finishRound(arg0,cardsDealt,isLastDraw);
                    }
                },TIME_DELAY);


            } catch (InvalidCardDescriptionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isPlayable() {
        return playable;
    }

    @Override
    public void setAI(GameActivity activity,boolean aiPlays) {
        this.aiPlays=aiPlays;
        if(aiPlays||isPlayable()){
            onPerformMove(activity,AIPlayer.getMoveFromState(state));
        }
    }

    @Override
    public boolean getAI() {
        return aiPlays;
    }

    @Override
    public void suggestMove(GameActivity activity) {
        if(aiPlays)
            return;
        onPerformMove(activity,AIPlayer.getMoveFromState(state));
    }


    @Override
    public PlayerState getState() {
        return state;
    }

    @Override
    public void setState(PlayerState state) {
        this.state=state;
    }

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    /**
     * Asynchronous call to the API to create a new game
     */
    private class CreateGameTask extends AsyncTask<String, Void, Boolean> {
        ProgressDialog _progressDialog;
        HttpURLConnection urlConnection;
        URL url;
        private Handler handler ;
        private GameActivity activity;

        public CreateGameTask(GameActivity activity){
            this.activity=activity;
            handler=new Handler();
        }
        @Override
        protected void onPreExecute(){
            _progressDialog = ProgressDialog.show(
                    activity,
                    "Connecting to Server ",
                    "Waiting for other player",
                    true,
                    true,
                    new DialogInterface.OnCancelListener(){
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            CreateGameTask.this.cancel(true);
                            activity.finish();
                        }
                    }
            );
        }
        @Override
        protected Boolean doInBackground(String... args) {
            int count=0;
            String result;
            String urlString=args[0];
            boolean flag=true;
            while(flag) {
                if(isCancelled())
                    break;
                flag=false;
                try {
                    /**
                     * Perform the API call
                     */
                    if(urlConnection==null) {
                        url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.addRequestProperty("Authorization", "APIKey " + activity.getResources().getString(R.string.API_KEY));
                        urlConnection.setReadTimeout(30000 /* milliseconds */);
                        urlConnection.setConnectTimeout(35000/* milliseconds */);
                    }
                    int code=urlConnection.getResponseCode();
                    Log.i("ResponseCode",""+code);
                    /**
                     * Check the response codes and perform the
                     * appropriate actions
                     */
                    switch(code){
                        case 200:
                            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            br.close();
                            String jsonString = sb.toString();
                            System.out.println("JSON: " + jsonString);
                            //save the response to be used later
                            resultJSON=new JSONObject(jsonString);
                            return true;
                        case 401:
                            BufferedReader error = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                            StringBuilder s = new StringBuilder();
                            String l;
                            while ((l = error.readLine()) != null) {
                                s.append(l + "\n");
                            }
                            error.close();
                            String errorString = s.toString();
                            System.out.println("JSON: " + errorString);
                            //save the eror essage to be used later
                            errorJSON=new JSONObject(errorString);
                            return false;
                        default:
                                break;
                    }
                } catch (SocketTimeoutException e) {
                    flag=true;
                } catch(UnknownHostException e){
                    Log.i("UnknownHostException", e.getMessage());
                    errorJSON=new JSONObject();
                    try {
                        errorJSON.put("error","Connection Problem");
                        errorJSON.put("message","Check your internet Connection");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    return false;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Log.i("IOException", e.getMessage());
                    errorJSON=new JSONObject();
                    try {
                        errorJSON.put("error","Connection Problem");
                        errorJSON.put("message","Check your internet Connection");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    return false;
                }
            }
            return true;
        }

        /**
         * Executed when the call is finished and we have a reponse
         * @param result true if the responce was succesful
         */
        @Override
        protected void onPostExecute(Boolean result){
            _progressDialog.dismiss();
            /**
             * Case the call was succesfull
             */
            if(result){
                try {
                    /**
                     * Read the response and cuild the object
                     * representing the state
                     * and start the game based on the index you get
                     */
                    ArrayList<String> hand = new ArrayList<>(Parser.splitString(resultJSON.getString("cards"), 2));
                    ArrayList<String> surface = new ArrayList<>(Parser.splitString("", 2));
                    ArrayList<String> ownPile = new ArrayList<>(Parser.splitString("", 2));
                    ArrayList<ArrayList<String>> opponentPiles = new ArrayList<ArrayList<String>>();
                    Card briscola = new Card(resultJSON.getString("last_card"));
                    scores=new int[]{0,0};
                    cardsPlayed=0;
                    int opponentHandSizes[] = new int[1];
                    yourTurn=resultJSON.getBoolean("your_turn");
                    gameURL=resultJSON.getString("url");
                    for (int i = 0; i < 1; i++) {
                        opponentPiles.add(new ArrayList<String>());
                        opponentHandSizes[0] = 3;
                    }
                    /**
                     * set the index and the flag appropriately
                     */
                    if(yourTurn){
                        playerIndex=0;
                        startedRound=true;
                        Toast.makeText(activity,"You Start!!",Toast.LENGTH_LONG).show();
                        playable=true;
                    }
                    else {
                        playerIndex = 1;
                        startedRound=false;

                    }
                    /**
                     * construct the inner state
                     */
                    state = new PlayerState(hand, surface, ownPile, opponentPiles, briscola,
                            playerIndex, 34, opponentHandSizes, startedRound);
                    /**
                     * create the Rule applier and set the briscola
                     */
                    brain=new Brain();
                    brain.setTrumpSuit(briscola.getSuit());
                    /**
                     * Play the animations and display the scores
                     */
                    lastMove="START_GAME";
                    activity.startGame(state);
                    activity.setScores(scores);

                }catch (JSONException e){
                    e.printStackTrace();
                } catch (InvalidCardDescriptionException e) {
                    e.printStackTrace();
                }
            }
            else {
                /**
                 * Display the error that occurred and abandon the game after
                 */
                try {
                    String error=errorJSON.getString("error");
                    String message=errorJSON.getString("message");
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                    alert.setTitle(error);
                    alert.setMessage(message);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            activity.finish();
                        }
                    });

                    alert.show();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    /**
     * Asynchronous call to the API to perform a move
     */
    private class PostCardTask extends AsyncTask<String, Void, Boolean> {
        String playedCard;
        HttpURLConnection urlConnection;
        URL url;
        private Handler handler ;
        private GameActivity activity;

        public PostCardTask(GameActivity activity){
            this.activity=activity;
            handler=new Handler();
        }

        /**
         * Perform the API call
         * @param args
         * @return true if the call is successful
         */
        @Override
        protected Boolean doInBackground(String... args) {
            String urlString=args[0];
            String card=args[1];
            playedCard=card;
            boolean flag=true;
            Log.i("Post","Posting Card To Server");
            while(flag) {
                if(isCancelled())
                    break;
                flag=false;
                try {
                    /**
                     * perform the call
                     */
                    if(urlConnection==null) {
                        url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("POST");
                        urlConnection.addRequestProperty("Authorization", "APIKey " + activity.getResources().getString(R.string.API_KEY));
                        urlConnection.setRequestProperty("Content-Type", "text/plain");
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        urlConnection.setUseCaches(false);
                        urlConnection.setReadTimeout(30000 /* milliseconds */);
                        urlConnection.setConnectTimeout(35000/* milliseconds */);
                    }
                    /**
                     * Add the payload to the request
                     */
                    OutputStream os = urlConnection.getOutputStream();
                    os.write(card.getBytes("UTF-8"));
                    os.close();
                    urlConnection.connect();
                    int code=urlConnection.getResponseCode();
                    Log.i("ResponseCode",""+code);
                    /**
                     * Check the response
                     */
                    switch(code){
                        case 200:
                            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            br.close();
                            String jsonString = sb.toString();
                            System.out.println("JSON: " + jsonString);
                            /**
                             * save the results to be read later and return true
                             */
                            response=jsonString;
                            return true;
                        default:
                            /**
                             * construct the error message and return false
                             */
                            try {
                                BufferedReader error = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                                StringBuilder s = new StringBuilder();
                                String l;
                                while ((l = error.readLine()) != null) {
                                    s.append(l + "\n");
                                }
                                error.close();
                                String errorString = s.toString();
                                System.out.println("JSON: " + errorString);
                                errorJSON = new JSONObject(errorString);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            return false;
                    }
                } catch (SocketTimeoutException e) {
                    flag=true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("IOException", e.getMessage());
                    return false;
                }
            }
            return true;
        }
        /**
         * Executed when the call is finished and we have a reponse
         * @param result true if the responce was succesful
         */
        @Override
        protected void onPostExecute(Boolean result){

            if(result){
                /**
                 * show the animation of the card being played
                 * set the flags and apdate the card counter
                 * Control will be returned to OnPerformMove method to
                 * check the state of the game
                 */
                activity.playCard(playedCard,0,indexPlayed);
                cardsPlayed++;
                lastMove="POST";
            }
            else {
                /**
                 * Display the error that occurred and abandon the game after
                 */
                try {
                    String error=errorJSON.getString("error");
                    String message=errorJSON.getString("message");
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                    alert.setTitle(error);
                    alert.setMessage(message);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            activity.finish();
                        }
                    });

                    alert.show();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Asynchronous call to the GET API to retrieve the card
     * played by the opponent
     */
    private class GetCardTask extends AsyncTask<String, Void, Boolean> {

        HttpURLConnection urlConnection;
        URL url;
        private Handler handler ;
        private GameActivity activity;

        public GetCardTask(GameActivity activity){
            this.activity=activity;
            handler=new Handler();
        }

        /**
         * perform the task on the background
         * @param args
         * @return true if the call is successful
         */
        @Override
        protected Boolean doInBackground(String... args) {
            String urlString=args[0];
            boolean flag=true;
            Log.i("Poll","Polling Server");
            while(flag) {
                flag=false;
                /**
                 * perform the actual call
                 */
                try {
                    /**
                     * construct the request
                     */
                    if(urlConnection==null) {
                        url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.addRequestProperty("Authorization", "APIKey " + activity.getResources().getString(R.string.API_KEY));
                        urlConnection.setReadTimeout(30000 /* milliseconds */);
                        urlConnection.setConnectTimeout(35000/* milliseconds */);
                    }
                    int code=urlConnection.getResponseCode();
                    Log.i("ResponseCode",""+code);
                    /**
                     * Check the response
                     */
                    switch(code){
                        case 200:
                            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            br.close();
                            String jsonString = sb.toString();
                            System.out.println("JSON: " + jsonString);
                            /**
                             * save the result to be read later and return true
                             */
                            response=jsonString;
                            return true;
                        case 401:
                        case 410:
                            BufferedReader error = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                            StringBuilder s = new StringBuilder();
                            String l;
                            while ((l = error.readLine()) != null) {
                                s.append(l + "\n");
                            }
                            error.close();
                            String errorString = s.toString();
                            System.out.println("JSON: " + errorString);
                            errorJSON=new JSONObject(errorString);
                            /**
                             * save the error message and return false
                             */
                            return false;
                        default:
                            return false;
                    }
                } catch (SocketTimeoutException e) {
                    flag=true;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Log.i("IOException", e.getMessage());
                    return false;
                }
            }
            return true;
        }

        /**
         * Called after the call has finished
         * @param result true if the call was successful
         */
        @Override
        protected void onPostExecute(Boolean result){
            String opponentCard="";
            state.playableState=false;
            playable=false;

            if(result){
                /**
                 * update card counter and
                 * read the cards returned from the call
                 */
                cardsPlayed++;
                try {
                    resultJSON=new JSONObject(response);
                    opponentCard=resultJSON.getString("opponent");

                } catch (JSONException e) {
                    Log.i("GET GAME","Card not in response");
                }
                state.surface.add(opponentCard);
                state.opponentHandSize[0]--;
                state.playableState = false;
                lastMove="GET";
                /**
                 * play the animation of the card being played by opponent
                 * control will be returned to the onMovePerformed method afterwards
                 */
                activity.playCard(opponentCard,1,state.opponentHandSize[0]);
            }
            else {
                /**
                 * Display the error that occurred and abandon the game after
                 */
                try {
                    String error=errorJSON.getString("error");
                    String message=errorJSON.getString("message");
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                    alert.setTitle(error);
                    alert.setMessage(message);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            activity.finish();
                        }
                    });

                    alert.show();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Show the winner of the game
     * and save the data about the finished game to be used in
     * the statistics later
     */
    private void showWinner() {
        String message="You Won";
        String state=OnlineGame.WON;
        if(scores[0]<scores[1]) {
            message = "You Lost";
            state=OnlineGame.LOST;
        }
        else if(scores[0]==scores[1]) {
            message = "Draw";
            state=OnlineGame.DRAWN;
        }
        getRepository().saveOnlineGame(new OnlineGame(state));
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(message);
        alert.setMessage(scores[0]+" Points");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();
            }
        });

        alert.show();
    }

    /**
     * Asynchronous call to the API to delete a game
     */
    private class DeleteGameTask extends AsyncTask<String, Void, Boolean> {
        HttpURLConnection urlConnection;
        URL url;
        private GameActivity activity;

        public DeleteGameTask(GameActivity activity){
            this.activity=activity;
        }

        /**
         * performs the call in the background
         * @param args
         * @return true if the call is successful
         */
        @Override
        protected Boolean doInBackground(String... args) {
            String urlString=args[0];
            String reason=args[1];
            boolean flag=true;
            Log.i("Delete","Deleting Game");
            while(flag) {
                flag=false;
                /**
                 * perform the actual call
                 */
                try {
                    if(urlConnection==null) {
                        url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("DELETE");
                        urlConnection.addRequestProperty("Authorization", "APIKey " + activity.getResources().getString(R.string.API_KEY));
                        urlConnection.setRequestProperty("Content-Type", "text/plain");
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        urlConnection.setUseCaches(false);
                        urlConnection.setReadTimeout(30000 /* milliseconds */);
                        urlConnection.setConnectTimeout(35000/* milliseconds */);
                    }
                    OutputStream os = urlConnection.getOutputStream();
                    os.write(reason.getBytes("UTF-8"));
                    os.close();
                    urlConnection.connect();
                    int code=urlConnection.getResponseCode();
                    Log.i("ResponseCode",""+code);
                    /**
                     * Check the response and
                     * return true or false accordingly
                     */
                    switch(code){
                        case 200:
                            return true;
                        case 401:
                        case 410:
                            return false;
                        default:
                            return false;
                    }
                } catch (SocketTimeoutException e) {
                    flag=true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }


    }
}
