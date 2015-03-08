package com.example.shiwangi.dataplan.utils;

import org.json.JSONArray;

/**
 * Created by shiwangi on 5/3/15.
 */
public class SuggestingPlans {

    public SuggestingPlans(){


    }

    public static JSONArray suggestingLocalPlans(){


        JSONArray bestRc = bestRateCutter();

        JSONArray bestFm = bfreeMinutesPlans();

        JSONArray bestPlansLocal = chooseBest(bestRc,bestFm);

        return bestPlansLocal;
    }

    private static JSONArray chooseBest(JSONArray bestRc, JSONArray bestFm) {
        return null;
    }

    private static JSONArray bfreeMinutesPlans() {

        return null;
    }

    private static JSONArray bestRateCutter() {
        return null;
    }

    public static void suggestingStdPlans(){

    }



    public static void freeMinutesPlans(){

    }


}