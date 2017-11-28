package it.polimi.ma.group07.briscola.controller;

import android.content.DialogInterface;
import android.os.AsyncTask;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.R;
import it.polimi.ma.group07.briscola.model.Card;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Parser;
import it.polimi.ma.group07.briscola.model.PlayerState;

import static android.R.id.message;
import static it.polimi.ma.group07.briscola.R.id.deck;

/**
 * Created by amari on 22-Nov-17.
 */

public class ServerCoordinator implements GameController {
    private static final String APISECRET = "81113e69-5276-4801-a1f0-e2185a27d2a5";
    private static final String START_ENDPOINT="http://mobile17.ifmledit.org/api/room/Group07";
    private static PlayerState state;
    private static GameActivity activity;
    private static JSONObject resultJSON;
    private boolean yourTurn;
    private int playerIndex;
    private String gameId;
    private String gameURL;
    public void startGame(GameActivity activity) throws IOException {
            System.out.println("testing");
            this.activity=activity;
            ConnectionTask task=new ConnectionTask();
            task.execute(activity.getResources().getString(R.string.start_game_endpoint),"GET");
    }
    @Override
    public void onPerformMove(GameActivity activity, int index) {

    }

    @Override
    public PlayerState getState() {
        return null;
    }

    @Override
    public PlayerState setState() {
        return null;
    }

    private class ConnectionTask extends AsyncTask<String, Void, Boolean> {

        HttpURLConnection urlConnection;

        @Override
        protected Boolean doInBackground(String... args) {
            int count=0;
            String result;
            boolean flag=true;
            while(flag) {
                flag=false;
                try {
                    result = getJSONObjectFromURL(args[0], args[1]);
                    resultJSON=new JSONObject(result);
                } catch (SocketTimeoutException e) {
                    count++;
                    if(count>4)
                        return false;
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
            if(result){
                try {
                    activity.dismissDialog();
                    ArrayList<String> hand = new ArrayList<>(Parser.splitString(resultJSON.getString("cards"), 2));
                    ArrayList<String> surface = new ArrayList<>(Parser.splitString("", 2));
                    ArrayList<String> ownPile = new ArrayList<>(Parser.splitString("", 2));
                    ArrayList<ArrayList<String>> opponentPiles = new ArrayList<ArrayList<String>>();
                    Card briscola = new Card(resultJSON.getString("last_card"));
                    int opponentHandSizes[] = new int[1];
                    yourTurn=resultJSON.getBoolean("your_turn");
                    gameId=resultJSON.getString("game");
                    gameURL=resultJSON.getString("url");
                    for (int i = 0; i < 1; i++) {
                        opponentPiles.add(new ArrayList<String>());
                        opponentHandSizes[0] = 3;
                    }
                    if(yourTurn)
                        playerIndex=0;
                    else
                        playerIndex=1;
                    state = new PlayerState(hand, surface, ownPile, opponentPiles, briscola,
                            playerIndex, 34, opponentHandSizes, true);
                    activity.startGame(state);
                }catch (JSONException e){
                    e.printStackTrace();
                } catch (InvalidCardDescriptionException e) {
                    e.printStackTrace();
                }
            }
            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setTitle(result?"Game Created":"Failed to create Game");
            alert.setMessage(result?resultJSON.toString():"Error!!");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            alert.show();
        }

    }
    private class GetGameTask extends AsyncTask<String, Void, Boolean> {

        HttpURLConnection urlConnection;

        @Override
        protected Boolean doInBackground(String... args) {
            int count=0;
            String result;
            boolean flag=true;
            while(flag) {
                flag=false;
                try {
                    result = getJSONObjectFromURL(args[0], args[1]);
                    resultJSON=new JSONObject(result);
                } catch (SocketTimeoutException e) {
                    count++;
                    if(count>4)
                        return false;
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
            if(result){
                try {
                    activity.dismissDialog();
                    ArrayList<String> hand = new ArrayList<>(Parser.splitString(resultJSON.getString("cards"), 2));
                    ArrayList<String> surface = new ArrayList<>(Parser.splitString("", 2));
                    ArrayList<String> ownPile = new ArrayList<>(Parser.splitString("", 2));
                    ArrayList<ArrayList<String>> opponentPiles = new ArrayList<ArrayList<String>>();
                    Card briscola = new Card(resultJSON.getString("last_card"));
                    int opponentHandSizes[] = new int[1];
                    yourTurn=resultJSON.getBoolean("your_turn");
                    for (int i = 0; i < 1; i++) {
                        opponentPiles.add(new ArrayList<String>());
                        opponentHandSizes[0] = 3;
                    }
                    if(yourTurn)
                        playerIndex=0;
                    else
                        playerIndex=1;
                    state = new PlayerState(hand, surface, ownPile, opponentPiles, briscola,
                            playerIndex, 34, opponentHandSizes, true);
                    activity.startGame(state);
                }catch (JSONException e){
                    e.printStackTrace();
                } catch (InvalidCardDescriptionException e) {
                    e.printStackTrace();
                }
            }
            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setTitle(result?"Game Created":"Failed to create Game");
            alert.setMessage(result?resultJSON.toString():"Error!!");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            alert.show();
        }

    }
    public  String getJSONObjectFromURL(String urlString,String method) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(method);
        urlConnection.addRequestProperty("Authorization","APIKey "+activity.getResources().getString(R.string.start_game_endpoint));
        urlConnection.setReadTimeout(30000 /* milliseconds */ );
        urlConnection.setConnectTimeout(35000/* milliseconds */ );
        Log.i("Connection","Connecting");
        Log.i("Connection","Connected");
        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();
        System.out.println("JSON: " + jsonString);
        Log.i("Connection","Returning");
        Log.i("request","Request OK");
        return jsonString;
    }
}
