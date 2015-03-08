package com.example.shiwangi.dataplan.utils;

import org.json.JSONObject;

/**
 * Created by shiwangi on 5/3/15.
 */
public class PlanExpensePair implements Comparable<PlanExpensePair>{
    public JSONObject plan;
    double expense;
    public PlanExpensePair(JSONObject p, double c){
        plan = p;
        expense = c;
    }

    @Override
    public int compareTo(PlanExpensePair another) {
        return (int) (expense-another.expense);
    }
}
