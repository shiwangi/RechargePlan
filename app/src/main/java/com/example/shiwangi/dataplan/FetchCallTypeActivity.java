package com.example.shiwangi.dataplan;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shiwangi.dataplan.utils.CallType;
import com.example.shiwangi.dataplan.utils.GetLog;
import com.example.shiwangi.dataplan.utils.Values;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.mashape.unirest.http.Unirest;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

public class FetchCallTypeActivity extends Activity{
   ArcProgress pBar,pBar2;
    private Context mContext;
    public static String myOperator, myState;
    private static GetLog mlog;
    CallType local, std;
    static int flag = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_call_type);

        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);

        String phoneNumber = settings.getString("phoneNumber",null);
        mlog = new GetLog(getApplicationContext(),phoneNumber);
        mContext = this;

        local = new CallType(new Values(0, 0), new Values(0, 0));
        std = new CallType(new Values(0, 0), new Values(0, 0));

        Toast.makeText(mContext, "Fetching Call Records", Toast.LENGTH_SHORT);
        computeCallTypeFetch();
    }


    public void computeCallTypeFetch() {
        AsyncTask<String , String, Void> task = new AsyncTask<String, String , Void>() {

            protected void onPreExecute() {

                super.onPreExecute();

                // set the drawable as progress drawavle

               // pBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("03a9f4", PorterDuff.Mode.SRC_IN)

                pBar = (ArcProgress)findViewById(R.id.arc_progress);
                pBar2 = (ArcProgress)findViewById(R.id.arc_progress2);
                pBar.setMax((int) mlog.totalCallDuration);
                pBar.setProgress(0);
                pBar.setVisibility(View.VISIBLE);
                pBar.setBottomTextSize(60);
                pBar2.setMax((int) mlog.totalCallDuration);
                pBar2.setProgress(0);
                pBar2.setVisibility(View.VISIBLE);
                pBar2.setBottomTextSize(20);
                pBar2.setSuffixText("mins");
                pBar.setSuffixText("mins");



//                DialogInterface.OnCancelListener  OnCancel = new DialogInterface.OnCancelListener(){
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//                        cancel(true);
//                        FetchCallType.this.cancel(true);
//
//                    }
//                };
                //   dialog.setOnCancelListener(OnCancel);


            }

            @Override
            protected Void doInBackground(String... params) {
                getCallLogDetails();
                return null;
            }

            private void getCallLogDetails() {
                //check STD/ISD
                int sz = mlog.callList.size();
                try {

                    HashMap<String , Integer> map = new HashMap<String , Integer>();

                    for (int i = 0; i < sz; i++) {
                        Boolean stateCheck = false,operatorCheck = false;
                        String phNumber = mlog.callList.get(i).phoneNumber;
                        if(!map.containsKey(phNumber)) {
                            com.mashape.unirest.http.HttpResponse<String> stdISD = Unirest
                                    .get("https://sphirelabs-mobile-number-portability-india-operator-v1.p.mashape.com/index.php?number="
                                            + phNumber)
                                    .header("X-Mashape-Key", "1mxGUdc3Vbmsh7K4Cg5phB7LCRtXp1GzmIZjsnRankGm7Z8oL5")
                                    .header("Accept", "application/json")
                                    .asString();
                            Log.d("FetchPlans", "Type of Call: " + mlog.callList.get(i).phoneNumber
                                    + ": " + stdISD.getBody() + "\n");
                            JSONObject jobj = new JSONObject(stdISD.getBody());
                            operatorCheck = jobj.getString("Operator").equals(myOperator);
                            stateCheck = jobj.getString("Telecom circle").equals(myState);
                            if (i == 0) {
                                myOperator = jobj.getString("Operator");
                                myState = jobj.getString("Telecom circle");
                            }

                        }

                        else{
                            stateCheck = (map.get(phNumber)/2 == 1)?true:false;
                            operatorCheck = (map.get(phNumber)%2 == 1)?true:false;
                        }
                        if(i > 0 ) {
                            if (stateCheck) {
                                if (operatorCheck) {
                                    local.sameOperator.minutes += Math.ceil(mlog.callList.get(i).callDuration / 60);

                                    local.sameOperator.seconds += (mlog.callList.get(i).callDuration);
                                    map.put(phNumber , 3);
                                }

                                else map.put(phNumber , 2);
                                local.allCalls.minutes += Math.ceil(mlog.callList.get(i).callDuration / 60);
                                local.allCalls.seconds += (mlog.callList.get(i).callDuration);
                                publishProgress("0");

                            } else {
                                if (operatorCheck) {
                                    std.sameOperator.minutes += Math.ceil(mlog.callList.get(i).callDuration / 60);
                                    std.sameOperator.seconds += (mlog.callList.get(i).callDuration);
                                    map.put(phNumber , 1);
                                }
                                else map.put(phNumber , 0);
                                std.allCalls.minutes += Math.ceil(mlog.callList.get(i).callDuration / 60);
                                std.allCalls.seconds += (mlog.callList.get(i).callDuration);
                                publishProgress("1");
                            }
                           }



                    }
                    flag = 0;

                } catch (Exception e) {
//            Toast.makeText(mContext,"Looks like your Internet Connection is shaky!",Toast.LENGTH_SHORT);
                    flag = 1;
                    Intent intent = new Intent(mContext, NoInternet.class);
                    mContext.startActivity(intent);
                    e.printStackTrace();

                }
                Log.d("FetchPlans", "total Local  Same Operator calls: " + local.sameOperator.minutes + " " + local.sameOperator.seconds);
                Log.d("FetchPlans", "total STD  Same Operator calls: " + std.sameOperator.minutes + " " + std.sameOperator.seconds);
                Log.d("FetchPlans", "total Local  all Operator calls: " + local.allCalls.minutes + " " + local.allCalls.seconds);
                Log.d("FetchPlans", "total STD  all Operator calls: " + std.allCalls.minutes + " " + std.allCalls.seconds);


            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                if(values[0].equals("0")){
                    pBar2.setProgress(pBar2.getProgress()+1);
                }
                else
                    pBar.setProgress(pBar.getProgress()+1);

            }

            protected void onPostExecute(Void v) {
                //parse JSON data
                if(flag==0) {
                    Intent intent = new Intent(getApplicationContext(), GetCallDetails.class);
                    intent.putExtra("logData", mlog);
                    intent.putExtra("callDetailslocal", local);
                    intent.putExtra("callDetailsStd", std);
                    intent.putExtra("MyOperator" , myOperator);
                    intent.putExtra("MyState" , myState);
                    mContext.startActivity(intent);
                }
            }

        };

        task.execute();
    }

}