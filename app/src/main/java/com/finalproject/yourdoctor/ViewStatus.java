package com.finalproject.yourdoctor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.finalproject.yourdoctor.servicehandler.ServiceHandler;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ViewStatus extends ActionBarActivity {
    String email, id, json;
    private static String URL_LOCATION = "http://yourdoctor.url.ph/Web-Service/GetStatus/Status.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("View Status");
        setContentView(R.layout.activity_view_status);

        Intent i = getIntent();
        email = i.getStringExtra("Email");
        id = i.getStringExtra("Id");

        new NetCheck().execute();
    }

    private class NetCheck extends AsyncTask<String,Integer,Boolean>
    {
        private ProgressDialog nDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(ViewStatus.this);
            nDialog.setMessage("Loading..");
            //nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
            System.out.println("Executed");
        }
        protected Boolean doInBackground(String... args) {
/**
 * Gets current device state and checks for working internet connection by trying Google.
 **/
            System.out.println("Executed");
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;
        }
        protected void onPostExecute(Boolean th){
            if(th){
                nDialog.dismiss();
                new ShowStatus().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error in Network Connection.Please check your connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ShowStatus extends AsyncTask<String,Void,Void> {
        /**
         * Defining Process dialog
         */
        TextView pen = (TextView)findViewById(R.id.pending);
        TextView acc = (TextView)findViewById(R.id.accepted);
        TextView rej = (TextView)findViewById(R.id.rejected);

        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ViewStatus.this);
            //pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(String... args) {
            List locparams = new ArrayList();
            locparams.add(new BasicNameValuePair("Email", email));
            locparams.add(new BasicNameValuePair("Id", id));

            ServiceHandler jsonParser = new ServiceHandler();
            json = jsonParser.makeServiceCall(URL_LOCATION, ServiceHandler.POST, locparams);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.e("Response: ", "> " + json);
            json = "{ \"status\" :"+json+"}";
            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray st = jsonObj.getJSONArray("status");
                        JSONObject stObj = (JSONObject) st.get(0);
                        String res = stObj.getString("status");
                        //System.out.print(res);
                        if(res.equals("Pending...")){
                            pen.setText(res);
                        }
                        else if(res.equals("Accepted") ){
                            acc.setText(res);
                        }
                        else
                            rej.setText(res);
//                        else
//                            pen.setText("Please check details you have entered");
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
