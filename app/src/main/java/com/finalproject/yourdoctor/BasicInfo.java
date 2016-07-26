package com.finalproject.yourdoctor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class BasicInfo extends ActionBarActivity  {
    EditText name,email,reason,age;
    String nameEdit, reasonEdit, ageEdit, emailEdit;
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Basic Info");
        setContentView(R.layout.activity_basic_info);

        name = (EditText) findViewById(R.id.name);
        email = (EditText)findViewById(R.id.email);
        reason = (EditText) findViewById(R.id.reason);
        age = (EditText)findViewById(R.id.age);

        bt=(Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(name.getText().length() == 0 || email.getText().length() == 0 || reason.getText().length() == 0 || age.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(),"One or more fields empty", Toast.LENGTH_LONG).show();
                }
                else{
                    nameEdit = name.getText().toString();
                    emailEdit = email.getText().toString();
                    reasonEdit = reason.getText().toString();
                    ageEdit = age.getText().toString();

                    Intent i = new Intent(BasicInfo.this, SelectDoctor.class);
                    i.putExtra("Name",nameEdit);
                    i.putExtra("Email",emailEdit);
                    i.putExtra("Reason",reasonEdit);
                    i.putExtra("Age",ageEdit);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_basic_info, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);

        int id = item.getItemId();
        Log.d("menu check","clicked: "+id);
        Log.d("menu check","doc id: "+R.id.doc);
        Log.d("menu check","stat id:"+R.id.stat);
        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.doc: {
                startActivity(new Intent(BasicInfo.this, Doctor.class));
                finish();
                break;
            }
            case R.id.stat: {
                startActivity(new Intent(BasicInfo.this, LastStatus.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
