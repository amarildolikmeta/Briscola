package it.polimi.ma.group07.briscola.controller;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.controller.helper.HttpRequest;

/**
 * Created by amari on 31-Oct-17.
 */

public class SendDataListener implements View.OnClickListener {
    public static String CONFIGURATION_KEY="entry.1910876714";
    public static String MOVES_KEY="entry.1555000500";
    public static String RESULT_KEY="entry.14507673";
    public static String URL="https://docs.google.com/forms/d/e/1FAIpQLSeonYFOe2lJMWUeb0l6-HubT5j5uAqYYbGMicrFPkmaZUj7yw/formResponse";
    private GameActivity activity;

    public SendDataListener(GameActivity activity){
        this.activity=activity;
    }

    @Override
    public void onClick(View v) {
        //Create an object for PostDataTask AsyncTask
        PostDataTask postDataTask = new PostDataTask();

        //execute asynctask
        postDataTask.execute(URL);
    }
    //AsyncTask to send data as a http POST request
    private class PostDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... contactData) {
            Boolean result = true;
            String url = contactData[0];
            String postBody="";

            try {
                //all values must be URL encoded to make sure that special characters like & | ",etc.
                //do not cause problems
                postBody = CONFIGURATION_KEY+"=" + URLEncoder.encode(Coordinator.getInstance().getStartConfiguration(),"UTF-8") +
                        "&" + MOVES_KEY + "=" + URLEncoder.encode(Coordinator.getInstance().getMovesPerformed(),"UTF-8") +
                        "&" + RESULT_KEY + "=" + URLEncoder.encode(Briscola.getInstance().toString(),"UTF-8");
            } catch (UnsupportedEncodingException ex) {
                result=false;
            }


            try {
                HttpRequest httpRequest = new HttpRequest();
                String sendResponse=httpRequest.sendPost(url, postBody);
            }catch (Exception exception){
                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){
            //Print Success or failure message accordingly
            Toast.makeText(activity,result?"Message successfully sent!":"There was some error in sending message. Please try again after some time.",Toast.LENGTH_LONG).show();
        }

    }
}
