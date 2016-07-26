package com.finalproject.yourdoctor;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;

import com.finalproject.yourdoctor.library.JSONParser;
import com.finalproject.yourdoctor.library.UserFunctions;
import com.finalproject.yourdoctor.servicehandler.Location;
import com.finalproject.yourdoctor.servicehandler.ServiceHandler;


public class FixAppointment extends ActionBarActivity implements OnItemSelectedListener {
    private Spinner spinnerloc;
    EditText name,reason;
    String locationOf;
    Button bt;

    // array list for spinner adapter
    private ArrayList<Location> locationlist;
    ProgressDialog pDialog;
    private String URL_LOCATION = "http://yourdoctor.url.ph/Web-Service/Location/GetLocation.php";
    private String URL_SENDDATA = "http://yourdoctor.url.ph/Web-Service/ListDoctors/finddoctor.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix_appointment);
        name = (EditText) findViewById(R.id.Name);
        reason = (EditText) findViewById(R.id.Reason);
        bt=(Button)findViewById(R.id.button);

        spinnerloc = (Spinner) findViewById(R.id.spinner);
        locationlist = new ArrayList<Location>();
        new GetLocation().execute();

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pname = name.getText().toString();
                //preason =reason.getText().toString();
                locationOf = spinnerloc.getSelectedItem().toString();
                Intent i = new Intent(FixAppointment.this, SelectDoctor.class);
                i.putExtra("location", locationOf);
                startActivity(i);
            }
        });
    }
    /**
     * Adding spinner data
     **/
    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();
        //txtLocation.setText("");
        for (int i = 0; i < locationlist.size(); i++) {
            lables.add(locationlist.get(i).getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerloc.setAdapter(spinnerAdapter);
    }
    /**
     * Async task to create a new food category
     * */
    private class GetLocation extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FixAppointment.this);
            pDialog.setMessage("Fetching Locations..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(String... params) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(URL_LOCATION, ServiceHandler.GET);

            Log.e("Response: ", "> " + json);
            json = "{ \"names\" :"+json+"}";
            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray categories = jsonObj.getJSONArray("names");

                        for (int i = 0; i < categories.length(); i++) {
                            JSONObject catObj = (JSONObject) categories.get(i);
                            Location cat = new Location(catObj.getString("lastname"));
                            locationlist.add(cat);
                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinner();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fix_appointment, menu);

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
        else{
            startActivity(new Intent(FixAppointment.this,Doctor.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
