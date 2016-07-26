package com.finalproject.yourdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.finalproject.yourdoctor.servicehandler.ServiceHandler;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class DateAndTime extends ActionBarActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TIME_PATTERN = "HH:mm";
    String docId, json, docName;
    String nameEdit, reasonEdit, ageEdit, emailEdit, date , time;
    Button bt;
    TextView lblDate, lblTime, text_time;
    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private static String URL_LOCATION = "http://yourdoctor.url.ph/Web-Service/GetTime/Time.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Date And Time");
        setContentView(R.layout.activity_date_and_time);

        text_time = (TextView)findViewById(R.id.time);
        Intent i = getIntent();
        docId = i.getStringExtra("id");
        docName = i.getStringExtra("DocName");
        nameEdit = i.getStringExtra("Name");
        emailEdit = i.getStringExtra("Email");
        reasonEdit = i.getStringExtra("Reason");
        ageEdit = i.getStringExtra("Age");

        new GetTime().execute();

        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

        lblDate = (TextView) findViewById(R.id.lblDate);
        lblTime = (TextView) findViewById(R.id.lblTime);

        update();

        bt = (Button)findViewById(R.id.sub);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = lblDate.getText().toString();
                time = lblTime.getText().toString();
                Intent i = new Intent(DateAndTime.this , Confirm.class);
                i.putExtra("Id", docId);
                i.putExtra("DocName", docName);
                i.putExtra("Name", nameEdit);
                i.putExtra("Email", emailEdit);
                i.putExtra("Reason", reasonEdit);
                i.putExtra("Age", ageEdit);
                i.putExtra("Date", date);
                i.putExtra("Time", time);
                startActivity(i);
                finish();
            }
        });


    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDatePicker:
                DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");
                break;
            case R.id.btnTimePicker:
                TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
                break;
        }
    }

    private void update() {
        lblDate.setText(dateFormat.format(calendar.getTime()));
        lblTime.setText(timeFormat.format(calendar.getTime()));
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        update();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        update();
    }

    private class GetTime extends AsyncTask<String,Void,Void> {
        /**
         * Defining Process dialog
         */
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(DateAndTime.this);
            //pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(String... args) {
            List locparams = new ArrayList();
            locparams.add(new BasicNameValuePair("did", docId));

            ServiceHandler jsonParser = new ServiceHandler();
            json = jsonParser.makeServiceCall(URL_LOCATION, ServiceHandler.POST, locparams);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.e("Response: ", "> " + json);
            json = "{ \"names\" :"+json+"}";
            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray time = jsonObj.getJSONArray("names");
                        JSONObject timeObj = (JSONObject) time.get(0);
                        String availTime = timeObj.getString("username");
                        text_time.setText("Doctor is available between "+availTime+" hours");
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


}
