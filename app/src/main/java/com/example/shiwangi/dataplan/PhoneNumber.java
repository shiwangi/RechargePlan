package com.example.shiwangi.dataplan;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class PhoneNumber extends Activity implements View.OnClickListener {
    String phoneNumber;EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
        if(hasLoggedIn)
        {
            Intent intent = new Intent(this , FetchCallTypeActivity.class);
            intent.putExtra("parent", "PhoneNumber");
            startActivity(intent);
        }
        setContentView(R.layout.activity_phone_number);
        et =(EditText) findViewById(R.id.editText1);
        Button b =(Button) findViewById(R.id.submit);
        b.setOnClickListener( this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_phone_number, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.submit:phoneNumber = String.valueOf(et.getText());
                                            if(phoneNumber.length() != 10){
                                                Toast toast = Toast.makeText(getApplicationContext(), "Incorrect Phone number", Toast.LENGTH_SHORT);
                                                toast.show();
                                                break;
                                            }
                                            SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0); // 0 - for private mode
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putBoolean("hasLoggedIn", true);
                                            editor.putString("phoneNumber",phoneNumber);

                                            editor.commit();    // Commit the edits!
                                            Intent intent = new Intent(this , FetchCallTypeActivity.class);
                                            intent.putExtra("parent", "PhoneNumber");
                                            startActivity(intent);
                                            break;
        }


    }
}
