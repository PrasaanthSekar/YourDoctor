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


public class Response extends ActionBarActivity {
    TextView pname1,pemail1,preason1,page1,date1,time1;
    String docid, name, email, reason, age, dateString, timeString, StatusString, json, tag = "";
    Button accept, reject;
    ProgressDialog pDialog;
    private static String url = "http://yourdoctor.url.ph/Web-Service/UpdateStatus/Update.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        pname1 = (TextView)findViewById(R.id.pname1);
        pemail1 = (TextView)findViewById(R.id.pemail1);
        preason1 = (TextView)findViewById(R.id.preason1);
        page1 = (TextView)findViewById(R.id.age1);
        date1 = (TextView)findViewById(R.id.date1);
        time1 = (TextView)findViewById(R.id.time1);

        Intent i = getIntent();
        docid = i.getStringExtra("Did");
        name = i.getStringExtra("PName");
        email = i.getStringExtra("PEmail");
        reason = i.getStringExtra("PReason");
        age = i.getStringExtra("Age");
        dateString = i.getStringExtra("Date");
        timeString = i.getStringExtra("Time");

        pname1.setText(name);
        pemail1.setText(email);
        preason1.setText(reason);
        page1.setText(age);
        date1.setText(dateString);
        time1.setText(timeString);

        accept = (Button)findViewById(R.id.accept);
        accept.setTag("Accept");
        reject = (Button)findViewById(R.id.reject);
        reject.setTag("Reject");
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusString = "Accepted";
                tag = (String)v.getTag();
                new NetCheck().execute();
//                Intent accept = new Intent(Response.this, AppStatus.class);
//                accept.putExtra("Name", name);
//                accept.putExtra("Tag", tag);
//                startActivity(accept);
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusString = "Rejected";
                tag = (String)v.getTag();
                new NetCheck().execute();
//                Intent reject = new Intent(Response.this, AppStatus.class);
//                reject.putExtra("Name", name);
//                reject.putExtra("Tag", tag);
//                startActivity(reject);
            }
        });

    }

    private class NetCheck extends AsyncTask<String,Integer,Boolean>
    {
        private ProgressDialog nDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(Response.this);
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
                new UpdateStatus().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error in Network Connection.Please check your connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateStatus extends AsyncTask<String,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Response.this);
            pDialog.setMessage("Updating Status..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(String... params) {
            List locparams = new ArrayList();
            locparams.add(new BasicNameValuePair("Status", StatusString));
            locparams.add(new BasicNameValuePair("Email", email));
            locparams.add(new BasicNameValuePair("Did", docid));

            ServiceHandler jsonParser = new ServiceHandler();
            json = jsonParser.makeServiceCall(url, ServiceHandler.POST, locparams);
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
                            Intent i = new Intent(Response.this, AppStatus.class);
                            i.putExtra("Name", name);
                            i.putExtra("Tag", tag);
                            startActivity(i);
                            finish();
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
        getMenuInflater().inflate(R.menu.menu_response, menu);
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
