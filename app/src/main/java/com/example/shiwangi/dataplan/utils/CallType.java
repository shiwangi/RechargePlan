package com.example.shiwangi.dataplan.utils;

import java.io.Serializable;

/**
 * Created by shiwangi on 5/3/15.
 */
public class CallType implements Serializable {

    public Values sameOperator;
    public Values allCalls;

    public CallType(Values same , Values diff){
        sameOperator = same;
        allCalls =diff;
    }



}