package com.example.shiwangi.dataplan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class OperatorNotFound extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    Spinner spinnerState , spinnerOperator;
    Button submit;

    private String[] state= {"Andra Pradesh","Arunachal Pradesh","Assam","Bihar","Haryana","Himachal Pradesh",
            "Jammu and Kashmir", "Jharkhand","Karnataka", "Kerala","Tamil Nadu"};

    private String[] operator= {"Andra Pradesh","Arunachal Pradesh","Assam","Bihar","Haryana","Himachal Pradesh",
            "Jammu and Kashmir", "Jharkhand","Karnataka", "Kerala","Tamil Nadu"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_not_found);

        spinnerState = (Spinner) findViewById(R.id.state);
        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, state);
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(adapter_state);


        spinnerOperator = (Spinner) findViewById(R.id.operator);
        ArrayAdapter<String> adapter_operator = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, operator);
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOperator.setAdapter(adapter_operator);

        submit  = (Button) findViewById(R.id.submitOp);
        submit.setOnClickListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
//        spinnerState.setSelection(position);
//        spinnerOperator.setSelection(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_operator_not_found, menu);
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

    @Override
    public void onClick(View v) {
        String selState = (String) spinnerState.getSelectedItem();
        String selOperator = (String) spinnerOperator.getSelectedItem();
        Intent intent = new Intent(getApplicationContext(), FetchCallTypeActivity.class);
        intent.putExtra("myState", selState);
        intent.putExtra("myOperator", selOperator);
        intent.putExtra("parent", "OperatorNotFound");
        startActivity(intent);
    }
}
