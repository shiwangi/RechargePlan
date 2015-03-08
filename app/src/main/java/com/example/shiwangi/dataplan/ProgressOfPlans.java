package com.example.shiwangi.dataplan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shiwangi.dataplan.utils.CallType;
import com.example.shiwangi.dataplan.utils.GetLog;
import com.example.shiwangi.dataplan.utils.MashapeUtilities;
import com.example.shiwangi.dataplan.utils.PlanExpensePair;
import com.example.shiwangi.dataplan.utils.RechargePlans;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProgressOfPlans.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProgressOfPlans#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressOfPlans extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ProgressDialog pd;
    InputStream is = null;
    JSONObject jObj = null;
    String json = "";
    String result = "";
    private Context mContext;
    private GetLog mlog;
    ProgressBar pBar;
    private RechargePlans localPlans, stdPlans;
    private CallType localCall, stdCalls;
    private String myState, myOperator;
    ArrayList<Integer> timeDurationList_Sec;
    public static ArrayList<PlanExpensePair> RatecuttersPE;
    public static ArrayList<JSONObject> topUps;
    public JSONArray topups;
    TextView t1,t2,t3,t4;
    Button b;

    ArrayList<Integer> timeDurationList_Min;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProgressOfPlans.
     */
    // TODO: Rename and change types and number of parameters
    public static ProgressOfPlans newInstance(String param1, String param2) {
        ProgressOfPlans fragment = new ProgressOfPlans();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProgressOfPlans() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Context context = getActivity();

        mContext = context;
        mlog = (com.example.shiwangi.dataplan.utils.GetLog) ((GetCallDetails) getActivity()).getIntent().getExtras().getSerializable("logData");

        localCall = (com.example.shiwangi.dataplan.utils.CallType) ((GetCallDetails) getActivity()).getIntent().getExtras().getSerializable("callDetailslocal");

        stdCalls = (com.example.shiwangi.dataplan.utils.CallType) ((GetCallDetails) getActivity()).getIntent().getExtras().getSerializable("callDetailsStd");

        myOperator = (String) (((GetCallDetails) getActivity()).getIntent().getExtras().getSerializable("MyOperator"));

        myState = (String) ((GetCallDetails) getActivity()).getIntent().getExtras().getSerializable("MyState");
        timeDurationList_Sec = new ArrayList<>();
        timeDurationList_Sec.add(localCall.sameOperator.seconds);
        timeDurationList_Sec.add(localCall.allCalls.seconds);

        timeDurationList_Sec.add(stdCalls.sameOperator.seconds);
        timeDurationList_Sec.add(stdCalls.allCalls.seconds);


        timeDurationList_Min = new ArrayList<>();
        timeDurationList_Min.add(localCall.sameOperator.minutes);
        timeDurationList_Min.add(localCall.allCalls.minutes);

        timeDurationList_Min.add(stdCalls.sameOperator.minutes);
        timeDurationList_Min.add(stdCalls.allCalls.minutes);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_progress_of_plans, container, false);
        pBar = (ProgressBar) V.findViewById(R.id.linearProgressBar);

        b = (Button) V.findViewById(R.id.showplans);
        t1 = (TextView) V.findViewById(R.id.card1);
        t2 = (TextView) V.findViewById(R.id.card2);
        t3 = (TextView)V.findViewById(R.id.card3);
        t4 = (TextView) V.findViewById(R.id.card4);

        Drawable img = V.getResources().getDrawable( R.drawable.std_all);
        img.setBounds(0, 0, 120, 120);
        t2.setCompoundDrawables(img, null, null, null);



        V.getResources().getDrawable( R.drawable.local_same);
        img.setBounds(0, 0, 120, 120);
        t1.setCompoundDrawables(img, null, null, null);

        img = V.getResources().getDrawable( R.drawable.std_all);
        img.setBounds(0, 0, 120, 120);
        t1.setCompoundDrawables(img, null, null, null);

        img = V.getResources().getDrawable( R.drawable.std_same);
        img.setBounds(0, 0, 120, 120);
        t2.setCompoundDrawables(img, null, null, null);


        img = V.getResources().getDrawable( R.drawable.local_all);
        img.setBounds(0, 0, 120, 120);
        t3.setCompoundDrawables(img, null, null, null);


        img = V.getResources().getDrawable( R.drawable.local_same);
        img.setBounds(0, 0, 120, 120);
        t4.setCompoundDrawables(img, null, null, null);
        t1.setText("Std Duration : " + stdCalls.allCalls.minutes + " minutes");
        t2.setText("Std Same Operator Duration : " + stdCalls.sameOperator.minutes+ " minutes");
        t3.setText("Local Duration : " + localCall.allCalls.minutes + " minutes");
        t4.setText("Local Same Operator Duration : " + localCall.sameOperator.minutes + " minutes");
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slidein );
        t1.startAnimation(animation);
        t3.startAnimation(animation);
        Animation animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.slideinright);
        t2.startAnimation(animation2);
        t4.startAnimation(animation2);
        b.setVisibility(View.INVISIBLE);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something

                Intent intent = new Intent(getActivity(), ScreenSlideActivity.class);
                intent.putExtra("logData", mlog);
                startActivity(intent);


            }
        });
        computeFetchPlan();
        return V;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    public void computeFetchPlan() {
        AsyncTask<String, String, Void> task = new AsyncTask<String, String, Void>() {


            protected void onPreExecute() {


                pBar.setIndeterminate(true);
                pBar.setVisibility(View.VISIBLE);

            }

            @Override
            protected Void doInBackground(String... params) {

                try {

                    //check for topup plans
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
                    Toast.makeText(mContext, "Looks like your Internet Connection is shaky!", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(mContext, NoInternet.class);
                    mContext.startActivity(intent);
                    e.printStackTrace();
                } catch (JSONException e) {
                    Toast.makeText(mContext, "Looks like your Internet Connection is shaky!", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(mContext, NoInternet.class);
                    mContext.startActivity(intent);
                    e.printStackTrace();
                }


//ratecutters
                String url_select = "http://datayuge-prod.apigee.net/v3/rechargeplans/?apikey=XMWutQqSknlAm0p0zAnjeJ5JO5FPgbxs&operatorid=" + myOperator.toLowerCase() + "&circleid=" + "andhra pradesh" + "&recharge_type=special";

                url_select = url_select.replace(" ", "%20");
                try {
                    // defaultHttpClient
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(url_select);
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();
                    json = sb.toString();
                    Log.d("FetchPlans", "special plans: \n" + json);
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }
                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(json);

                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }


                return null;
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
                        if (description.contains("Only applicable after") || description.contains("SMS")
                                && !(validity.contains("Days") || validity.contains("Weeks")))
                            continue;

                        if (LocalCallHelper(description)) {
                            if (SameOpRecharge(description)) {
                                localPlans.sameOp.add(recharge);
                            } else
                                localPlans.all.add(recharge);
                        }
                        if (STDCallHelper(description)) {
                            if (SameOpRecharge(description)) {
                                stdPlans.sameOp.add(recharge);
                            } else
                                stdPlans.all.add(recharge);
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
                        } else {
                            // stdPlans.sameOperator.freeMinutes.add(stdPlans.sameOp.get(i));
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
                JSONArray recharges = null;
                pBar.setVisibility(View.INVISIBLE);
                b.setVisibility(View.VISIBLE);
                try {
                    recharges = new JSONArray(jObj.getString("data"));

                    parseRecharges(recharges);
                    //this.progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }


        };
       task.execute();
    }



}

