
package com.example.shiwangi.dataplan;

        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.net.Uri;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentActivity;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;

        import com.example.shiwangi.dataplan.utils.CallType;
        import com.example.shiwangi.dataplan.utils.GetLog;


public class GetCallDetails extends FragmentActivity implements ProgressOfPlans.OnFragmentInteractionListener{
    GetLog mlog;
    CallType local, std;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_calldetails);
        Intent intent = getIntent();

        mlog = (GetLog) intent.getExtras().getSerializable("logData");
        local = (CallType) intent.getExtras().getSerializable("callDetailslocal");

        std = (CallType) intent.getExtras().getSerializable("callDetailsStd");



    }

    public GetLog getLog() {
        return mlog;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_plan, menu);
        return true;
    }



    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.change_number:
                SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0); // 0 - for private mode
                SharedPreferences.Editor editor = settings.edit();

                //Set "hasLoggedIn" to true
                editor.putBoolean("hasLoggedIn", false);

                editor.commit();
                Intent intent = new Intent(GetCallDetails.this , PhoneNumber.class);
                startActivity(intent);
                break;
        }
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}