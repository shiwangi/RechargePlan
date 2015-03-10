package com.example.shiwangi.dataplan.utils;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shiwangi on 26/2/15.
 */
public class GetLog implements Serializable{
    public static String carrierName,log;
    public static ArrayList<OutgoingNumber> callList;
    public double localCalls;
    public double sameOperatorCalls;
    public double stdCalls;
    public double diffOperatorCalls;
    public static double totalCallDuration;

    public GetLog(Context context, String phoneNumber){
        totalCallDuration = 0;
        callList = new ArrayList<>();
        callList.add(new OutgoingNumber(phoneNumber,0,0));

        getCarrierName(context);
        getCallDetails(context);



    }

    public static void getCallDetails(Context context) {

        StringBuffer sb = new StringBuffer();
        String[] strFields = {
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.TYPE,
                android.provider.CallLog.Calls.CACHED_NAME,
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.DURATION,

        };

        // Defines a string to contain the selection clause
        String mSelectionClause = android.provider.CallLog.Calls.DATE+ " >= ?";

        // Initializes an array to contain selection arguments
        String[] mSelectionArgs = { createDate(2014,3,1).toString() };

        Cursor managedCursor = context.getContentResolver().query(
                android.provider.CallLog.Calls.CONTENT_URI,
                strFields,
                mSelectionClause,
                mSelectionArgs,
                null
        );

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int dateid = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        sb.append( "Call Details :");
int numCalls =0 ;
        //&&    numCalls<10
        while ( managedCursor.moveToNext() &&    numCalls<100) {
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
                    dir = "INCOMING";
                    continue;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
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
            if(callList.indexOf(call)!=-1){
                int c = callList.get(callList.indexOf(call)).count;
                callList.remove(call);
                callList.add(new OutgoingNumber(phNumber,c+1,Double.parseDouble(callDuration)));
            }
            else
                callList.add(call);
                numCalls+=1;
        }

        managedCursor.close();
        log = sb.toString();
        Log.d("GetLog", "Call History: \n " +
                sb);
    }
    public static Long createDate(int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance();

        calendar.set(year, month, day);

        return calendar.getTimeInMillis();

    }
    private static boolean isSameNetwork(String phNumber) {

        return false;
    }

    private static boolean isSTD(String ph_no){
       // fetchLocationOfReceiver();
        return true;
    }
    public static void getCarrierName(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        carrierName = manager.getNetworkOperatorName();
        Log.d("GetLog", "Carrier: \n " +
                carrierName);
    }

    public static void getLocation(Context context){
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Got the location!
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(context, locationResult);
        Log.d("GetLog","Current Location:" );

    }
}
