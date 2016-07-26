package com.finalproject.yourdoctor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.finalproject.yourdoctor.servicehandler.ServiceHandler;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Appointments extends ActionBarActivity {
    private static String url = "http://yourdoctor.url.ph/Web-Service/GetAppointments/Appointments.php";
    ArrayList<HashMap<String, String>> applist = new ArrayList<HashMap<String, String>>();
    ListView list;
    String email, json;
    ProgressDialog pDialog;
    private static final String TAG_NAME = "pname";
    private static final String TAG_REASON = "preson";
    private static final String TAG_EMAIL = "pemail";
    private static final String TAG_AGE = "page";
    private static final String TAG_DATE = "appdate";
    private static final String TAG_TIME = "apptime";
    private static final String MY_PREFS = "DocEmail";
    private static final String TAG_ID = "did";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Appointments");
        setContentView(R.layout.activity_appointments);
        deleteCache(getApplicationContext());

//        Intent i = getIntent();
//        email = i.getStringExtra("Email");
        SharedPreferences mySharedPreferences = getApplicationContext().getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE);
        email = mySharedPreferences.getString("username","");
        new NetCheck().execute();
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {

        }
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private class NetCheck extends AsyncTask<String,Integer,Boolean>
    {
        private ProgressDialog nDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(Appointments.this);
            nDialog.setMessage("Loading...");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
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
                new GetList().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error in Network Connection.Please check your connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetList extends AsyncTask<String,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Appointments.this);
            pDialog.setMessage("Loading Appointments..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(String... params) {
            List locparams = new ArrayList();
            locparams.add(new BasicNameValuePair("Email", email));

            ServiceHandler jsonParser = new ServiceHandler();
            json = jsonParser.makeServiceCall(url, ServiceHandler.POST, locparams);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.e("Response: ", "> " + json);
            if(json!=null){
                try{
                    JSONObject obj = new JSONObject(json);
                    JSONArray appArray = obj.getJSONArray("appointments");
                    for(int i = 0; i < appArray.length();i++){
                        JSONObject c = appArray.getJSONObject(i);
                        String id = c.getString("did");
                        String name = c.getString("pname");
                        String email= c.getString("pemail");
                        String reason = c.getString("preson");
                        String age = c.getString("page");
                        String date = c.getString("appdate");
                        String time = c.getString("apptime");

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_ID, id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_EMAIL,  email);
                        map.put(TAG_REASON,  reason);
                        map.put(TAG_AGE, age );
                        map.put(TAG_DATE, date);
                        map.put(TAG_TIME,  time);

                        applist.add(map);

                        list=(ListView)findViewById(R.id.listView);

                        ListAdapter adapter = new SimpleAdapter(Appointments.this, applist,
                                R.layout.activity_appointments,
                                new String[] { "pname" , "preson"},
                                new int[] {R.id.name , R.id.reason});
                        list.setAdapter(adapter);

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id)
                            {
                                //Toast.makeText(SelectDoctor.this, "You Clicked at "+doclist.get(+position).get("name"), Toast.LENGTH_SHORT).show();
                                String docid = applist.get(+position).get("did");
                                String pname = applist.get(+position).get("pname");
                                String pemail = applist.get(+position).get("pemail");
                                String preson = applist.get(+position).get("preson");
                                String page = applist.get(+position).get("page");
                                String date = applist.get(+position).get("appdate");
                                String time = applist.get(+position).get("apptime");

                                Intent i = new Intent(Appointments.this , Response.class);
                                i.putExtra("Did", docid);
                                i.putExtra("PName", pname);
                                i.putExtra("PReason" , preson);
                                i.putExtra("PEmail" , pemail);
                                i.putExtra("Age" , page);
                                i.putExtra("Date" , date);
                                i.putExtra("Time" , time);
                                startActivity(i);
                            }
                        });
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "No Appointments", Toast.LENGTH_LONG).show();
            }
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appointments, menu);
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
