package com.finalproject.yourdoctor;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.Toast;

import com.finalproject.yourdoctor.library.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DoctorRegister extends ActionBarActivity {
    /**
     *  JSON Response node names.
     **/
    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_USERNAME = "uname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_ERROR = "error";

    /**
     * Defining layout items.
     **/
    EditText inputFirstName;
    EditText inputLastName;
    EditText inputUsername;
    EditText inputEmail;
    EditText inputPassword;
    Button btnRegister;
    //TextView registerErrorMsg;
    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Sign Up");
        setContentView(R.layout.activity_doctor_register);
        /**
         * Defining all layout items
         **/
        inputFirstName = (EditText) findViewById(R.id.registerFirstName);
        inputLastName = (EditText) findViewById(R.id.registerLastName);
        inputUsername = (EditText) findViewById(R.id.registerUserName);
        inputEmail = (EditText) findViewById(R.id.registerEmail);
        inputPassword = (EditText) findViewById(R.id.registerPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        /**
         * Register Button click event.
         * A Toast is set to alert when the fields are empty.
         * Another toast is set to alert Username must be 5 characters.
         **/
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (  ( !inputUsername.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) && ( !inputFirstName.getText().toString().equals("")) && ( !inputLastName.getText().toString().equals("")) && ( !inputEmail.getText().toString().equals("")) )
                {
                    if ( inputUsername.getText().toString().length() > 4 ){
                        NetAsync(view);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),
                                "Username should be minimum 5 characters", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "One or more fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    /**
     * Async Task to check whether internet connection is working
     **/
    private class NetCheck extends AsyncTask<String,Integer,Boolean>
    {
        private ProgressDialog nDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(DoctorRegister.this);
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
                new ProcessRegister().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error in Network Connection.Please check your connection", Toast.LENGTH_SHORT ).show();
            }
        }
    }
    private class ProcessRegister extends AsyncTask<String,Integer,JSONObject> {
        /**
         * Defining Process dialog
         **/
        private ProgressDialog pDialog;
        String email,password,fname,lname,uname;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inputUsername = (EditText) findViewById(R.id.registerUserName);
            inputPassword = (EditText) findViewById(R.id.registerPassword);

            fname = inputFirstName.getText().toString();
            lname = inputLastName.getText().toString();
            email = inputEmail.getText().toString();
            uname= inputUsername.getText().toString();
            password = inputPassword.getText().toString();
            pDialog = new ProgressDialog(DoctorRegister.this);
            //pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Registering ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected JSONObject doInBackground(String... args) {
            UserFunctions userFunction = new UserFunctions();
            return userFunction.registerUser(fname, lname, email, uname, password);
        }

        protected void onPostExecute(JSONObject json) {
            JSONObject j = json;
            Log.d("JSON", json.toString());
            try {
                int res = json.getInt(KEY_SUCCESS);
                int red = json.getInt(KEY_ERROR);
                if ((res) == 1) {
                    pDialog.setTitle("Getting Data");
                    pDialog.setMessage("Loading Info");
                    Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                    finish();
                } else if ((red) == 2) {
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();
                } else if ((red) == 3) {
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Invalid Email-id", Toast.LENGTH_SHORT).show();
                } else {
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error occurred during registeration", Toast.LENGTH_LONG).show();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    public void NetAsync(View view){
        new NetCheck().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_doctor_register, menu);
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
