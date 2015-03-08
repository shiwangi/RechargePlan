package com.example.shiwangi.dataplan.utils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shiwangi on 5/3/15.
 */
public class PlanType {
    ArrayList<JSONObject> rateCutter;
    ArrayList<JSONObject> freeMinutes;


    JSONObject bestRateCutter_sameOperator;

    JSONObject bestRateCutter_forallCalls;
    public PlanType(ArrayList<JSONObject> rc,ArrayList<JSONObject> fm){
        rateCutter = rc;
        freeMinutes = fm;

    }
}
