package com.example.shiwangi.dataplan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shiwangi on 26/2/15.
 */
public class GetLog implements Serializable{
    public static String carrierName;
    public static ArrayList<OutgoingNumber> callList;
    public static double totalCallDuration;

    public GetLog(Context context, String phoneNumber , String date){
        totalCallDuration = 0;
        callList = new ArrayList<>();
        callList.add(new OutgoingNumber(phoneNumber,0,0));
        getCarrierName(context);
        getCallDetails(context,date);
    }



        public static void getCallDetails(Context context,String callDate) {

        StringBuffer sb = new StringBuffer();
        // Initializes an array to contain selection arguments
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date fromDate = null;
        try {
            fromDate = sdf.parse(callDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                new String[] { CallLog.Calls.DATE, CallLog.Calls.DURATION,CallLog.Calls.TYPE,
                        CallLog.Calls.NUMBER, CallLog.Calls._ID },
                CallLog.Calls.DATE + ">?",
                new String[] { String.valueOf(fromDate.getTime())},
                CallLog.Calls.NUMBER + " asc");

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int dateid = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        sb.append( "Call Details :");
        //&&    numCalls<10
        while ( managedCursor.moveToNext()) {
            String str = managedCursor.toString();
            String phNumber = managedCursor.getString( number );
            String callType = managedCursor.getString( type );
            String callDuration = managedCursor.getString( duration );
            Date date = new java.sql.Date(managedCursor.getLong(dateid));
            String dir = null;
            int dircode = Integer.parseInt( callType );
            switch( dircode ) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    continue;

                case CallLog.Calls.MISSED_TYPE:
                    continue;
            }

            //Filtering out LandLine Numbers.
            int len =phNumber.length();
            if(len>=10)
                phNumber = phNumber.substring(len - 10, len);
            else
                continue;

            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + "\nCall duration in sec :--- " + callDuration);
            sb.append("\nDate:"+date+"\n----------------------------------");
            totalCallDuration += Math.ceil(Double.parseDouble(callDuration)/60.0);
            OutgoingNumber call = new OutgoingNumber(phNumber,1,Double.parseDouble(callDuration));
                callList.add(call);
        }

        managedCursor.close();
        Log.d("GetLog", "Call History: \n " +
                sb);
    }


    public static void getCarrierName(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        carrierName = manager.getNetworkOperatorName();
        Log.d("GetLog", "Carrier: \n " +
                carrierName);
    }
}
