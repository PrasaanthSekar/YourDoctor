package com.finalproject.yourdoctor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.finalproject.yourdoctor.servicehandler.ServiceHandler;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Confirm extends ActionBarActivity {
    TextView docid, docname, pname, pemail, preason, page, tdate, ttime;
    String id, dname, name, email, reason, age, date, time;
    String json;
    private static String URL_LOCATION = "http://yourdoctor.url.ph/Web-Service/StoreData/Store.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Confirm");
        setContentView(R.layout.activity_confirm);

        docid = (TextView)findViewById(R.id.docid1);
        docname = (TextView)findViewById(R.id.docname1);
        pname = (TextView)findViewById(R.id.pname1);
        pemail = (TextView)findViewById(R.id.pemail1);
        preason = (TextView)findViewById(R.id.preason1);
        page = (TextView)findViewById(R.id.page1);
        tdate = (TextView)findViewById(R.id.pdate1);
        ttime = (TextView)findViewById(R.id.ptime1);

        Intent i = getIntent();
        id = i.getStringExtra("Id");
        dname = i.getStringExtra("DocName");
        name = i.getStringExtra("Name");
        email = i.getStringExtra("Email");
        reason = i.getStringExtra("Reason");
        age = i.getStringExtra("Age");
        date = i.getStringExtra("Date");
        time = i.getStringExtra("Time");

        docid.setText(id);
        docname.setText(dname);
        pname.setText(name);
        pemail.setText(email);
        preason.setText(reason);
        page.setText(age);
        tdate.setText(date);
        ttime.setText(time);

        AlertDialog.Builder alert = new AlertDialog.Builder(Confirm.this);
        //alert.setIcon(R.drawable.warning);
        alert.setTitle("Note");
        alert.setMessage("Please note the doctor id for checking your status");
        alert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Here actually start the GoLauncher
                        return;
                    }
                });
        alert.show();


        Button bt = (Button)findViewById(R.id.Fix);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NetCheck().execute();
            }
        });
    }

    private class NetCheck extends AsyncTask<String,Integer,Boolean>
    {
        private ProgressDialog nDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(Confirm.this);
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
                new SendDetails().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error in Network Connection.Please check your connection", Toast.LENGTH_SHORT ).show();
            }
        }
    }

    private class SendDetails extends AsyncTask<String,Void,Void> {
        /**
         * Defining Process dialog
         */
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Confirm.this);
            //pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(String... args) {
            List locparams = new ArrayList();
            locparams.add(new BasicNameValuePair("Did", id));
            locparams.add(new BasicNameValuePair("DName", dname));
            locparams.add(new BasicNameValuePair("PName", name));
            locparams.add(new BasicNameValuePair("PEmail", email));
            locparams.add(new BasicNameValuePair("Preason", reason));
            locparams.add(new BasicNameValuePair("PAge", age));
            locparams.add(new BasicNameValuePair("Date", date));
            locparams.add(new BasicNameValuePair("Time", time));

            ServiceHandler jsonParser = new ServiceHandler();
            json = jsonParser.makeServiceCall(URL_LOCATION, ServiceHandler.POST, locparams);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.e("Response: ", "> " + json);
            //json = "{ \"Data\" :"+json+"}";
            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        String res = jsonObj.getString("result");
                        //text_time.setText("Doctor is availabble between "+availTime+" hours");
                        if(Integer.parseInt(res) == 1){
                            Intent i = new Intent(Confirm.this, ViewStatus.class);
                            i.putExtra("Email", email);
                            i.putExtra("Id", id);
                            startActivity(i);
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Error Occurred. Please try again later", Toast.LENGTH_LONG).show();
                        //System.out.print(res);
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
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
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
