package com.example.shiwangi.dataplan.utils;

import android.util.Log;

/**
 * Created by shiwangi on 28/2/15.
 */
public class MashapeUtilities {
    public static String getTelecomCircle(String state){
        String code = "";
        switch (state){
            case "Assam" : code = "as";break;
            case "Bihar" : code = "br";break;
            case "Chennai": code = "ch";break;
            case "Delhi" : code = "dl";break;
            case "Himachal Pradesh" : code = "hp";break;
            case "Karnataka" : code = "ka";break;
            case "Kolkata" : code = "kk";break;
            case "Maharashtra" : code = "mh";break;
            case "Mumbai" : code = "mb";break;
            case "North East" : code = "ne";break;
            case "Orissa" : code = "or";break;
            case "Punjab" : code = "pb";break;
            case "Rajasthan" : code = "rj";break;
            case "Uttar Pradesh E" : code = "upe";break;
            case "Uttar Pradesh W" : code = "upw";break;
            case "West Bengal" : code = "wb";break;
            case "Jammu"  : code = "jk";break;
            case "Andhra Pradesh"  : code = "ap";break;
            case "Gujarat"  : code = "gj";break;
            case "Haryana"  : code = "hr";break;
            case "Kerala"  : code = "kl";break;
            case "Madhya Pradesh"  : code = "mp";break;
        }
        return code;
    }

    public static String getOperatorCode(String myOperator) {

        String code = "";
    switch(myOperator){
            case "Aircel": code = "aircel";break;
            case "Airtel": code = "airtel";break;
            case "BSNL": code = "bsnl";break;
            case "Tata Docomo GSM": code = "ta0tadocomogsm";break;
            case "Idea": code = "idea";break;
            case "Loop Mobile": code = "loopmobile";break;
            case "MTNL Delhi": code = "mtnldelhi";break;
            case "Reliance CDMA": code = "reliancecdma";break;
            case "Tata Indicom": code = "tataindicom";break;
            case "Uninor": code = "uninor";break;
            case "Vodafone": code = "Vodafone";break;
            case "MTS": code = "mts";break;
            case "Videocon Mobile": code = "videoconmobile";break;
            case "Virgin GSM": code = "virgingsm";break;
            case "Tata Docomo CDMA": code = "tatadocomocdma";break;

            case "MTNL Mumbai": code = "mtnlmumbai";break;
        }
        return code;
    }
}
