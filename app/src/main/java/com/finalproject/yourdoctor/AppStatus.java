package com.finalproject.yourdoctor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class AppStatus extends ActionBarActivity {
    String name, tag;
    TextView appstatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Status");
        setContentView(R.layout.activity_app_status);

        appstatus = (TextView)findViewById(R.id.appstatus);

        Intent a = getIntent();
        name = a.getStringExtra("Name");
        tag = a.getStringExtra("Tag");

        if(tag.equals("Accept"))
            appstatus.setText(name+" appointment has been accepted successfully");
        else
            appstatus.setText(name+" appointment has been rejected");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_status, menu);
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
