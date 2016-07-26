package com.finalproject.yourdoctor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.finalproject.yourdoctor.library.JSONParserList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SelectDoctor extends ActionBarActivity {
    ProgressDialog pDialog;
    private static String url = "http://yourdoctor.url.ph/Web-Service/ListDoctors/finddoctor.php";
    ArrayList<HashMap<String, String>> doclist = new ArrayList<HashMap<String, String>>();
    ListView list;
    Button bt;
    String nameEdit, reasonEdit, ageEdit, emailEdit;
    private static final String TAG_NAME = "firstname";
    private static final String TAG_LOC = "lastname";
    private static final String TAG_DID = "did";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Choose Doctor");
        setContentView(R.layout.activity_select_doctor);

        Intent i = getIntent();
        nameEdit = i.getStringExtra("Name");
        emailEdit = i.getStringExtra("Email");
        reasonEdit = i.getStringExtra("Reason");
        ageEdit = i.getStringExtra("Age");

        new NetCheck().execute();
    }

    private class NetCheck extends AsyncTask<String,Integer,Boolean>
    {
        private ProgressDialog nDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(SelectDoctor.this);
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
                new GetDoctors().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error in Network Connection.Please check your connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetDoctors extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SelectDoctor.this);
            pDialog.setMessage("Loading Doctor's List..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected JSONObject doInBackground(String... params) {
            JSONParserList jParser = new JSONParserList();
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            return json;
        }

        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                JSONArray android = json.getJSONArray("doctors");
                for(int i = 0; i < android.length(); i++){
                    JSONObject c = android.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String did = c.getString("did");
                    String name = c.getString("firstname");
                    String loc = c.getString("lastname");
                    //System.out.print(name);
                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_NAME, name);
                    map.put(TAG_LOC,loc);
                    map.put(TAG_DID,did);
                    doclist.add(map);
                    list=(ListView)findViewById(R.id.listView);

                    ListAdapter adapter = new SimpleAdapter(SelectDoctor.this, doclist,
                            R.layout.activity_select_doctor,
                            new String[] { "firstname" , "lastname"},
                            new int[] {R.id.names , R.id.loc});
                    list.setAdapter(adapter);

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id)
                        {
                            //Toast.makeText(SelectDoctor.this, "You Clicked at "+doclist.get(+position).get("name"), Toast.LENGTH_SHORT).show();
                            String id_doc = doclist.get(+position).get("did");
                            String doc_name = doclist.get(+position).get("firstname");
                            Intent i = new Intent(SelectDoctor.this , DateAndTime.class);
                            i.putExtra("id" , id_doc);
                            i.putExtra("DocName" , doc_name);
                            i.putExtra("Name" , nameEdit);
                            i.putExtra("Email" , emailEdit);
                            i.putExtra("Reason" , reasonEdit);
                            i.putExtra("Age" , ageEdit);
                            startActivity(i);
                            finish();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
