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
import java.util.ArrayList;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.R;
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
 * Created by amari on 22-Nov-17.
 */

public class ServerCoordinator implements GameController {
    private PlayerState state;
    private GameActivity activity;
    private JSONObject resultJSON;
    private JSONObject errorJSON;
    private boolean yourTurn;
    private int playerIndex;
    private boolean startedRound;
    private String gameURL;
    private int score;
    Brain brain;
    private int cardsPlayed;

    public void startGame(GameActivity activity) throws IOException {
            this.activity=activity;
            CreateGameTask task=new CreateGameTask(activity);
            task.execute(activity.getResources().getString(R.string.start_game_endpoint));
    }
    @Override
    public void onPerformMove(GameActivity activity, int index) {
        if(!state.playableState)
            return;
        String c=state.hand.remove(index);
        this.activity=activity;
        state.surface.add(c);
        state.playableState=false;
        activity.buildInterface(state);
        Log.i("Post","About to post");
       PostCardTask postCardTask=new PostCardTask(activity);
        postCardTask.execute(gameURL,c);
    }
    public void pollServer(){
        state.playableState=false;
        GetCardTask getCardTask=new GetCardTask(activity);
        getCardTask.execute(gameURL);
    }
    public void finishGame(String reason){
        if(state!=null) {
            Log.i("DELETE", "Terminating game");
            DeleteGameTask task = new DeleteGameTask(activity);
            task.execute(gameURL, reason);
        }
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
                            errorJSON=new JSONObject(errorString);
                            return false;
                        default:
                                break;
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

        @Override
        protected void onPostExecute(Boolean result){
            _progressDialog.dismiss();
            if(result){
                try {
                    ArrayList<String> hand = new ArrayList<>(Parser.splitString(resultJSON.getString("cards"), 2));
                    ArrayList<String> surface = new ArrayList<>(Parser.splitString("", 2));
                    ArrayList<String> ownPile = new ArrayList<>(Parser.splitString("", 2));
                    ArrayList<ArrayList<String>> opponentPiles = new ArrayList<ArrayList<String>>();
                    Card briscola = new Card(resultJSON.getString("last_card"));
                    score=0;
                    cardsPlayed=0;
                    int opponentHandSizes[] = new int[1];
                    yourTurn=resultJSON.getBoolean("your_turn");
                    gameURL=resultJSON.getString("url");
                    for (int i = 0; i < 1; i++) {
                        opponentPiles.add(new ArrayList<String>());
                        opponentHandSizes[0] = 3;
                    }
                    if(yourTurn){
                        playerIndex=0;
                        startedRound=true;
                        Toast.makeText(activity,"You Start!!",Toast.LENGTH_LONG).show();
                    }
                    else {
                        playerIndex = 1;
                        startedRound=false;

                    }
                    state = new PlayerState(hand, surface, ownPile, opponentPiles, briscola,
                            playerIndex, 34, opponentHandSizes, startedRound);
                    brain=new Brain();
                    brain.setTrumpSuit(briscola.getSuit());
                    activity.startGame(state);
                    if(!yourTurn)
                        pollServer();
                }catch (JSONException e){
                    e.printStackTrace();
                } catch (InvalidCardDescriptionException e) {
                    e.printStackTrace();
                }
            }
            else {
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

    private class PostCardTask extends AsyncTask<String, Void, Boolean> {
        String response;
        String playedCard;
        HttpURLConnection urlConnection;
        URL url;
        private Handler handler ;
        private GameActivity activity;

        public PostCardTask(GameActivity activity){
            this.activity=activity;
            handler=new Handler();
        }
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
                    OutputStream os = urlConnection.getOutputStream();
                    os.write(card.getBytes("UTF-8"));
                    os.close();
                    urlConnection.connect();
                    int code=urlConnection.getResponseCode();
                    Log.i("ResponseCode",""+code);
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
                            response=jsonString;
                            return true;
                        default:
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

        @Override
        protected void onPostExecute(Boolean result){
            String opponentCard="",myCard=null;
            int winnerCard=0;
            if(result){
                cardsPlayed++;
                if(state.surface.size()<2){
                    state.currentPlayer=(state.currentPlayer+1)%2;
                        pollServer();
                }
                else{
                    //round finished
                    try {
                        Log.i("GET","Determining Winner");
                        final boolean winner;
                        winnerCard=brain.determineWinnerString(state.surface);
                        ArrayList<String> s=new ArrayList<String>(state.surface);
                        state.surface=new ArrayList<>();
                        if(startedRound&winnerCard==0||!startedRound&&winnerCard==1){
                            state.ownPile.addAll(s);
                            startedRound=true;
                            state.currentPlayer=playerIndex;
                            winner=true;
                            score+=brain.calculatePointsString(s);
                        }
                        else{
                            winner=false;
                            state.opponentPiles.get(0).addAll(s);
                            startedRound=false;
                            state.currentPlayer=(playerIndex+1)%2;
                        }
                        state.playableState=false;
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                activity.buildInterface(state);
                            }
                        },1000);
                        try{
                            resultJSON=new JSONObject(response);
                            myCard=resultJSON.getString("card");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final String finalMyCard = myCard;
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                if(state.deckSize>0)
                                    state.deckSize--;
                                if(finalMyCard!=null&&winner)
                                    state.hand.add(finalMyCard);
                                else if(finalMyCard!=null&&!winner)
                                    state.opponentHandSize[0]++;
                                activity.buildInterface(state);
                            }
                        },2000);
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                if(state.deckSize>0)
                                    state.deckSize--;
                                if(finalMyCard!=null&&winner)
                                    state.opponentHandSize[0]++;
                                else if(finalMyCard!=null&&!winner)
                                    state.hand.add(finalMyCard);
                                activity.buildInterface(state);

                                if(winner){
                                    state.playableState=true;
                                }
                                else{
                                    state.playableState=false;
                                    if(cardsPlayed<40)
                                        pollServer();
                                }

                                if(cardsPlayed==40)
                                    showWinner();
                            }
                        },3000);
                    } catch (InvalidCardDescriptionException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
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
    private class GetCardTask extends AsyncTask<String, Void, Boolean> {
        String response;
        HttpURLConnection urlConnection;
        URL url;
        private Handler handler ;
        private GameActivity activity;

        public GetCardTask(GameActivity activity){
            this.activity=activity;
            handler=new Handler();
        }
        @Override
        protected Boolean doInBackground(String... args) {
            String urlString=args[0];
            boolean flag=true;
            Log.i("Poll","Polling Server");
            while(flag) {
                flag=false;
                try {
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

        @Override
        protected void onPostExecute(Boolean result){
            String opponentCard="",myCard=null;
            int winnerCard=0;
            state.playableState=false;
            if(result){
                cardsPlayed++;
                try {
                    resultJSON=new JSONObject(response);
                    opponentCard=resultJSON.getString("opponent");
                    myCard=resultJSON.getString("card");

                } catch (JSONException e) {
                    Log.i("GET GAME","Card not in response");
                }
                state.surface.add(opponentCard);
                state.opponentHandSize[0]--;
                state.playableState = false;
                activity.buildInterface(state);
                Log.i("GET","Updating interface");
                if(state.surface.size()<2) {
                    state.playableState = true;
                    state.currentPlayer=playerIndex;
                }
                else
                    state.playableState=false;
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Log.i("GET","Inside Post Delayed");

                        activity.buildInterface(state);
                    }
                },500);
                //round has finished
                if(state.surface.size()==2){
                    try {
                        Log.i("GET","Determining Winner");
                        final boolean winner;
                        winnerCard=brain.determineWinnerString(state.surface);
                        ArrayList<String> s=new ArrayList<String>(state.surface);
                        state.surface=new ArrayList<>();
                        if(startedRound&winnerCard==0||!startedRound&&winnerCard==1){
                            winner=true;
                            state.ownPile.addAll(s);
                            startedRound=true;
                            state.currentPlayer=playerIndex;
                            score+=brain.calculatePointsString(s);
                        }
                        else{
                            winner=false;
                            state.opponentPiles.get(0).addAll(s);
                            startedRound=false;
                            state.currentPlayer=(playerIndex+1)%2;
                        }
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                activity.buildInterface(state);
                            }
                        },1000);
                        final String finalMyCard = myCard;
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                if(state.deckSize>0)
                                    state.deckSize--;
                                if(finalMyCard!=null&&winner)
                                    state.hand.add(finalMyCard);
                                else if(finalMyCard!=null&&!winner)
                                    state.opponentHandSize[0]++;
                                activity.buildInterface(state);
                            }
                        },2000);
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                if(state.deckSize>0)
                                    state.deckSize--;
                                if(finalMyCard!=null&&winner)
                                    state.opponentHandSize[0]++;
                                else if(finalMyCard!=null&&!winner)
                                    state.hand.add(finalMyCard);
                                activity.buildInterface(state);
                                if(winner)
                                    state.playableState=true;
                                else{
                                    state.playableState=false;
                                    if(cardsPlayed<40)
                                        pollServer();
                                }
                                if(cardsPlayed==40)
                                    showWinner();
                            }
                        },3000);
                    } catch (InvalidCardDescriptionException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
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

    private void showWinner() {
        String message="You Won";
        if(score<60)
            message="You Lost";
        else if(score==60)
            message="Draw";
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(message);
        alert.setMessage(score+" Points");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();
            }
        });

        alert.show();
    }

    private class DeleteGameTask extends AsyncTask<String, Void, Boolean> {
        HttpURLConnection urlConnection;
        URL url;
        private GameActivity activity;

        public DeleteGameTask(GameActivity activity){
            this.activity=activity;
        }
        @Override
        protected Boolean doInBackground(String... args) {
            String urlString=args[0];
            String reason=args[1];
            boolean flag=true;
            Log.i("Delete","Deleting Game");
            while(flag) {
                flag=false;
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
