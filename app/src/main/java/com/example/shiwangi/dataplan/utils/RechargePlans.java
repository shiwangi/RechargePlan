package com.example.shiwangi.dataplan.utils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shiwangi on 5/3/15.
 */
public class RechargePlans {
    PlanType sameOperator;

    PlanType everywhere;
    public ArrayList<JSONObject> sameOp;
    public ArrayList<JSONObject> all;

    public RechargePlans(){
        sameOperator = new PlanType(new ArrayList<JSONObject>(),new ArrayList<JSONObject>());
        everywhere = new PlanType(new ArrayList<JSONObject>(),new ArrayList<JSONObject>());
        sameOp =new ArrayList<>();
        all = new ArrayList<>();
    }

}
