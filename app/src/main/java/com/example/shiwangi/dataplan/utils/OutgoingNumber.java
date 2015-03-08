package com.example.shiwangi.dataplan.utils;

/**
 * Created by shiwangi on 28/2/15.
 */
public class OutgoingNumber implements  Comparable<OutgoingNumber>{
    public String phoneNumber;
    int count;
    public double callDuration;
    public OutgoingNumber(String ph, int i,double duration) {
        phoneNumber = ph;
        count = i;
        callDuration = duration;
    }

    @Override
    public int compareTo(OutgoingNumber another) {
        if(phoneNumber.equals(another)){
            return 0;
        }
        return 1;
    }
}
