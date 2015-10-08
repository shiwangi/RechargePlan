package com.example.shiwangi.dataplan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shiwangi.dataplan.utils.CallType;
import com.example.shiwangi.dataplan.utils.GetLog;
import com.example.shiwangi.dataplan.utils.MashapeUtilities;
import com.example.shiwangi.dataplan.utils.NetworkCalls;
import com.example.shiwangi.dataplan.utils.PlanExpensePair;
import com.example.shiwangi.dataplan.utils.RechargePlans;
import com.example.shiwangi.dataplan.utils.Values;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FetchCallTypeActivity extends Activity implements OnClickListener {

    String result = "";
    JSONArray ratecut;
    private RechargePlans localPlans, stdPlans;
    private CallType localCall, stdCalls;
    ArrayList<Integer> timeDurationList_Sec;
    ArrayList<Integer> timeDurationList_Min;
    public static ArrayList<PlanExpensePair> RatecuttersPE;
    public static ArrayList<JSONObject> topUps;
    public JSONArray topups;
    JSONObject jObj;

    ArcProgress pBar, pBar2;
    ProgressBar progBar;
    ProgressBar lp, lp2;
    private Context mContext;
    public static String myOperator, myState;
    private DatePickerDialog fromDatePickerDialog;
    private static GetLog mlog;
    Button pressed, nonPressed, fetchButton;
    CallType local, std;
    static int flag = 0;
    private SimpleDateFormat dateFormatter;
    TextView fromDate;
    static String phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_call_type);
        Bundle extra = getIntent().getExtras();
        String prevIntent = extra.getString("parent");


        fromDate = (TextView)findViewById(R.id.fromDate);
        fromDate.setOnClickListener(this);
        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);

        phoneNumber = settings.getString("phoneNumber", null);

        String date = settings.getString("fromDate", "03-09-2015");
        fromDate.setText("Your calls since " + date);
        mlog = new GetLog(getApplicationContext(), phoneNumber,date);
        mContext = this;

        local = new CallType(new Values(0, 0), new Values(0, 0));
        std = new CallType(new Values(0, 0), new Values(0, 0));

        pressed = (Button) findViewById(R.id.pressed_btn);
        nonPressed = (Button) findViewById(R.id.nonpressed_btn);

        fetchButton = (Button) findViewById(R.id.fetch_plan);

        nonPressed.setOnClickListener(this);
        setFromDate();
    }

    private void setFromDate() {
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDate.setText("Your calls since " + dateFormatter.format(newDate.getTime()));
                SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0); // 0 - for private mode
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("fromDate",dateFormatter.format(newDate.getTime()));
                // Commit the edits!
                editor.commit();

                mlog = new GetLog(getApplicationContext(), phoneNumber,dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_fetch_call_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.change_number:
                SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0); // 0 - for private mode
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("hasLoggedIn", false);
                editor.commit();
                Intent intent = new Intent(FetchCallTypeActivity.this, PhoneNumber.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void operatorNotFound(){
        Intent intent = new Intent(FetchCallTypeActivity.this , OperatorNotFound.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v == fromDate){
            fromDatePickerDialog.show();
            return;
        }

        AsyncTask<String, String, Void> task = new AsyncTask<String, String, Void>() {

            protected void onPreExecute() {

                super.onPreExecute();

                // set the drawable as progress drawavle

                progBar = (ProgressBar) findViewById(R.id.lp);
                progBar.setVisibility(View.VISIBLE);
                progBar.setIndeterminate(true);
                nonPressed.setEnabled(false);
                nonPressed.setVisibility(View.INVISIBLE);
                fetchButton.setEnabled(false);
                fetchButton.setVisibility(View.INVISIBLE);

                pressed.setVisibility(View.VISIBLE);


                pBar = (ArcProgress) findViewById(R.id.arc_progress);
                pBar2 = (ArcProgress) findViewById(R.id.arc_progress2);
                lp = (ProgressBar) findViewById(R.id.linearProgressBar1);
                lp2 = (ProgressBar) findViewById(R.id.linearProgressBar2);
                pBar.setMax((int) mlog.totalCallDuration);
                pBar.setProgress(0);
                pBar.setVisibility(View.VISIBLE);
                pBar.setBottomTextSize(40);

                lp.setIndeterminate(false);
                lp.setMax((int) mlog.totalCallDuration);
                lp.setVisibility(View.VISIBLE);
                lp.setProgress(0);
                lp.setTag("Same Operator");
                pBar2.setMax((int) mlog.totalCallDuration);
                pBar2.setProgress(0);
                pBar2.setVisibility(View.VISIBLE);
                pBar2.setBottomTextSize(30);
                pBar2.setSuffixText("mins");
                pBar.setSuffixText("mins");

                lp2.setIndeterminate(false);
                lp2.setMax((int) mlog.totalCallDuration);
                lp2.setVisibility(View.VISIBLE);
                lp2.setProgress(0);
                lp2.setTag("Same Operator");

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

                    HashMap<String, Integer> map = new HashMap<String, Integer>();
                    int i = 0;
                    if(myOperator != null && myState != null){
                        i=1;
                    }
                    for (; i < sz; i++) {
                        Boolean stateCheck = false, operatorCheck = false;
                        String phNumber = mlog.callList.get(i).phoneNumber;
                        if (!map.containsKey(phNumber)) {
                            com.mashape.unirest.http.HttpResponse<String> stdISD = Unirest
                                    .get("https://sphirelabs-mobile-number-portability-india-operator-v1.p.mashape.com/index.php?number="
                                            + phNumber)
                                    .header("X-Mashape-Key", "1mxGUdc3Vbmsh7K4Cg5phB7LCRtXp1GzmIZjsnRankGm7Z8oL5")
                                    .header("Accept", "application/json")
                                    .asString();
                            Log.d("FetchPlans", "Type of Call: " + mlog.callList.get(i).phoneNumber
                                    + ": " + stdISD.getBody() + "\n");
                            JSONObject jobj = new JSONObject(stdISD.getBody());

                            if (i == 0) {
                                myOperator = jobj.getString("Operator");
                                myState = jobj.getString("Telecom circle");
                            }
                            operatorCheck = jobj.getString("Operator").equals(myOperator);
                            stateCheck = jobj.getString("Telecom circle").equals(myState);
                            if(myOperator.equals("false") || myState.equals("false")) {

                                Thread.currentThread().interrupt();
                                operatorNotFound();
                                break;
                            }

                        }
                        if (i > 0) {
                            if (stateCheck) {
                                if (operatorCheck) {
                                    local.sameOperator.minutes += Math.ceil(mlog.callList.get(i).callDuration / 60.0);
                                    publishProgress(new String[]{"0",
                                            String.valueOf((int)Math.ceil(mlog.callList.get(i).callDuration / 60.0))});
                                    local.sameOperator.seconds += (mlog.callList.get(i).callDuration);
                                    map.put(phNumber, 3);
                                } else {
                                    map.put(phNumber, 2);
                                    publishProgress(new String[]{"1",
                                            String.valueOf((int)Math.ceil(mlog.callList.get(i).callDuration / 60.0))});
                                }
                                local.allCalls.minutes += Math.ceil(mlog.callList.get(i).callDuration / 60.0);
                                local.allCalls.seconds += (mlog.callList.get(i).callDuration);


                            } else {
                                if (operatorCheck) {
                                    std.sameOperator.minutes += Math.ceil(mlog.callList.get(i).callDuration / 60.0);
                                    std.sameOperator.seconds += (mlog.callList.get(i).callDuration);
                                    map.put(phNumber, 1);
                                    publishProgress(new String[]{"2",
                                            String.valueOf((int)Math.ceil(mlog.callList.get(i).callDuration / 60.0))});
                                } else {
                                    publishProgress(new String[]{"3",
                                            String.valueOf((int)Math.ceil(mlog.callList.get(i).callDuration / 60.0))});
                                    map.put(phNumber, 0);
                                }
                                std.allCalls.minutes += Math.ceil(mlog.callList.get(i).callDuration / 60.0);
                                std.allCalls.seconds += (mlog.callList.get(i).callDuration);

                            }
                        }


                    }
                    flag = 0;

                } catch (Exception e) {
//            Toast.makeText(mContext,"Looks like your Internet Connection is shaky!",Toast.LENGTH_SHORT);
                    flag = 1;

                    Thread.currentThread().interrupt();
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
                if (values[0].equals("0")) {
                    pBar.setProgress(pBar.getProgress() + Integer.parseInt(values[1]));
                    lp.setProgress(lp.getProgress() + Integer.parseInt(values[1]));
                } else if (values[0].equals("2")) {
                    pBar2.setProgress(pBar2.getProgress() + Integer.parseInt(values[1]));
                    lp2.setProgress(lp2.getProgress() + Integer.parseInt(values[1]));
                } else if (values[0].equals("1")) {
                    pBar.setProgress(pBar.getProgress() + Integer.parseInt(values[1]));
                } else
                    pBar2.setProgress(pBar2.getProgress() + Integer.parseInt(values[1]));

            }

            protected void onPostExecute(Void v) {
                //parse JSON data
                if (flag == 0) {
                    progBar.setVisibility(View.INVISIBLE);
                    timeDurationList_Sec = new ArrayList<>();
                    timeDurationList_Sec.add(local.sameOperator.seconds);
                    timeDurationList_Sec.add(local.allCalls.seconds);

                    timeDurationList_Sec.add(std.sameOperator.seconds);
                    timeDurationList_Sec.add(std.allCalls.seconds);


                    timeDurationList_Min = new ArrayList<>();
                    timeDurationList_Min.add(local.sameOperator.minutes);
                    timeDurationList_Min.add(local.allCalls.minutes);

                    timeDurationList_Min.add(std.sameOperator.minutes);
                    timeDurationList_Min.add(std.allCalls.minutes);

                    pressed.setVisibility(View.INVISIBLE);
                    pressed.setEnabled(false);
                    fetchButton.setVisibility(View.VISIBLE);
                    fetchButton.setEnabled(true);
                    fetchButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AsyncTask<String, String, Void> task = new AsyncTask<String, String, Void>() {


                                protected void onPreExecute() {
                                    fetchButton.setVisibility(View.INVISIBLE);
                                    fetchButton.setEnabled(false);
                                    pressed.setVisibility(View.VISIBLE);
                                    pressed.setText("Fetching Plans...");


                                    progBar.setIndeterminate(true);
                                    progBar.setVisibility(View.VISIBLE);

                                }

                                @Override
                                protected Void doInBackground(String... params) {

                                    try {

                                        com.mashape.unirest.http.HttpResponse<String> topup = Unirest
                                                .get("https://sphirelabs-indian-telecom-data-recharge-plans-v1.p.mashape.com/telecomdata/v1/get/index.php?circle=" +
                                                        MashapeUtilities.getTelecomCircle(myState) +
                                                        "&opcode=" + MashapeUtilities.getOperatorCode(myOperator) + "&type=Topup")
                                                .header("X-Mashape-Key", "lLTnQ74ANcmshmQUctqadKqW6Zidp1eUYcNjsnr6zt1WR8bSRp")
                                                .header("Accept", "text/plain")
                                                .asString();
                                        topUps = new ArrayList<>();
                                        topups = new JSONArray(topup.getBody());
                                        for (int i = 0; i < topups.length(); i++) {
                                            JSONObject topUp = (JSONObject) topups.get(i);
                                            topUps.add(topUp);
                                            Log.d("FetchPlans", "Plans  " + "recharge_amount : " + topUp.get("recharge_amount") + " "
                                                    + "recharge_talktime: " + topUp.get("recharge_talktime") + "recharge_validity: " + topUp.get("recharge_validity"));
                                            if ((topUp.getDouble("recharge_amount")) == (topUp.getDouble("recharge_talktime"))) {
                                                topUps.remove(i);
                                                topUps.add(0, topUp);
                                            }

                                        }
                                    } catch (UnirestException e) {
                                      //  Toast.makeText(mContext, "Looks like your Internet Connection is shaky!", Toast.LENGTH_SHORT);
                                        Intent intent = new Intent(mContext, NoInternet.class);
                                        mContext.startActivity(intent);
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                      //  Toast.makeText(mContext, "Looks like your Internet Connection is shaky!", Toast.LENGTH_SHORT);

                                        Thread.currentThread().interrupt();
                                        Intent intent = new Intent(mContext, NoInternet.class);
                                        mContext.startActivity(intent);
                                        e.printStackTrace();
                                    }

                                    //RateCutters

                                    String url_select = "http://datayuge-prod.apigee.net/v3/rechargeplans/?apikey=XMWutQqSknlAm0p0zAnjeJ5JO5FPgbxs&operatorid=" + myOperator.toLowerCase() + "&circleid=" + "andhra pradesh" + "&recharge_type=special";

                                    NetworkCalls net = new NetworkCalls();
                                    jObj = net.getJsonObjectFrom(url_select);
                                    return  null;
                                }


                                private void parseRecharges(JSONArray recharges) {
                                    int len = recharges.length();
                                    try {
                                        localPlans = new RechargePlans();
                                        stdPlans = new RechargePlans();
                                        for (int i = 0; i < len; i++) {
                                            JSONObject recharge = (JSONObject) recharges.get(i);
                                            String description = recharge.getString("recharge_longdesc");
                                            String validity = recharge.getString("recharge_validity");
                                            if (description.contains("Only applicable after") || description.contains("SMS") && !(validity.contains("Days") || validity.contains("Weeks")))
                                                continue;
                                            if (LocalCallHelper(description)) {
                                                if (SameOpRecharge(description)) {
                                                    localPlans.sameOp.add(recharge);
                                                } else localPlans.all.add(recharge);
                                            }
                                            if (STDCallHelper(description)) {
                                                if (SameOpRecharge(description)) {
                                                    stdPlans.sameOp.add(recharge);
                                                } else stdPlans.all.add(recharge);
                                            }
                                        }
                                        ArrayList<PlanExpensePair> localSameOpPE = lookForRatecutters(localPlans.sameOp, 0);
                                        ArrayList<PlanExpensePair> localAllPE = lookForRatecutters(localPlans.all, 1);
                                        ArrayList<PlanExpensePair> stdSameOpPE = lookForRatecutters(stdPlans.sameOp, 2);
                                        ArrayList<PlanExpensePair> stdAllPE = lookForRatecutters(stdPlans.all, 3);
                                        RatecuttersPE = new ArrayList<>();
                                        for (int i = 0; i < localSameOpPE.size(); i++) {
                                            RatecuttersPE.add(localSameOpPE.get(i));
                                        }
                                        for (int i = 0; i < localAllPE.size(); i++) {
                                            RatecuttersPE.add(localAllPE.get(i));
                                        }
                                        for (int i = 0; i < stdSameOpPE.size(); i++) {
                                            RatecuttersPE.add(stdSameOpPE.get(i));
                                        }
                                        for (int i = 0; i < stdAllPE.size(); i++) {
                                            RatecuttersPE.add(stdAllPE.get(i));
                                        }
                                        Collections.sort(RatecuttersPE);
                                        Log.d("Ratecutter", "Sorted : Add debug point here to check all the 3 segments formed");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                private boolean STDCallHelper(String description) {
                                    return description.contains("STD") || description.contains("std");
                                }

                                private boolean LocalCallHelper(String description) {
                                    return description.contains("Local") || description.contains("local");
                                }

                                private boolean SameOpRecharge(String description) {
                                    return description.contains(myOperator) || description.contains(myOperator.charAt(0) + "to" + myOperator.charAt(0)) ||
                                            description.contains(myOperator.charAt(0) + " to " + myOperator.charAt(0)) ||
                                            description.contains(myOperator.charAt(0) + "2" + myOperator.charAt(0)) ||
                                            description.contains(myOperator.charAt(0) + " 2 " + myOperator.charAt(0));
                                }

                                public int parseToDays(String s) {
                                    String token[] = s.split(" ");
                                    if (token[1].equals("Days") || token[1].equals("days"))
                                        return Integer.parseInt(token[0]);

                                    return Integer.parseInt(token[0]) * 7;


                                }

                                public double computeCost(double basicCost, String s) {
                                    Log.d("BasicCost", String.valueOf(basicCost));
                                    return basicCost * (30.0 / parseToDays(s));
                                }

                                public ArrayList<PlanExpensePair> lookForRatecutters(ArrayList<JSONObject> planList, int index) {
                                    ArrayList<PlanExpensePair> PlanPE = new ArrayList<>();
                                    int len = planList.size();
                                    try {
                                        for (int i = 0; i < len; i++) {

                                            String desc = (planList.get(i).getString("recharge_longdesc"));

                                            String pattern = "([0-9]*)(.[0-9]*)?(p)(\\/|/)([0-9]*)?(sec|s|min|mins|minutes|minute|secs)";
                                            Pattern patt = Pattern.compile(pattern);
                                            Matcher matcher = patt.matcher(desc);
                                            if (matcher.find()) {
                                                Log.d("Ratecutter", matcher.group());
                                                String st = matcher.group();
                                                st = st.replaceAll("\\s+", "");
                                                String tokens[] = st.split("p/");
                                                try {
                                                    if (tokens.length != 2)
                                                        continue;

                                                    Double costPerUnit = Double.parseDouble(tokens[0]);
                                                    Double rechargeValue = (planList.get(i).getDouble("recharge_amount"));
                                                    String s = planList.get(i).getString("recharge_validity");
                                                    Double rechargeCost = computeCost(rechargeValue, s);
                                                    if (tokens[1].contains("min") || tokens[1].contains("mins")
                                                            || tokens[1].contains("minute") || tokens[1].contains("minutes")) {
                                                        // findForLocal

                                                        double expectedExpense = timeDurationList_Min.get(index) * costPerUnit + rechargeCost;
                                                        PlanPE.add(new PlanExpensePair(planList.get(i), expectedExpense));
                                                    } else {

                                                        int numSec = 1;
                                                        if (tokens[1].charAt(0) > '0' && tokens[1].charAt(0) < '9') {
                                                            numSec = tokens[1].charAt(0) - '0';
                                                        }
                                                        double expectedExpense = (timeDurationList_Sec.get(index) * costPerUnit) / numSec + rechargeCost;
                                                        PlanPE.add(new PlanExpensePair(planList.get(i), expectedExpense));

                                                    }

                                                }catch(Exception e){

                                                }

                                            }
                                        }
                                        Log.d("Ratecutter", "Found all among same Operator Plans");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Collections.sort(PlanPE);
                                    return PlanPE;

                                }

                                protected void onPostExecute(Void v) {
                                    // pBar.setVisibility(View.INVISIBLE);
                                    // b.setVisibility(View.VISIBLE);
                                    JSONArray recharges = null;

                                    try {
                                        recharges = new JSONArray(jObj.getString("data"));

                                        parseRecharges(recharges);
                                        //this.progressDialog.dismiss();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(getApplicationContext(), ScreenSlideActivity.class);
                                    intent.putExtra("logData", mlog);
                                    startActivity(intent);

                                }


                            };
                            task.execute();
                        }
                    });


                }
            }

        };

        task.execute();
    }

}