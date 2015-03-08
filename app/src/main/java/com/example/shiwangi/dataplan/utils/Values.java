package com.example.shiwangi.dataplan.utils;

import java.io.Serializable;

/**
 * Created by shiwangi on 5/3/15.
 */
public class Values implements Serializable{
    public int minutes;
    public int seconds;

    public Values(int min , int sec){
        minutes = min;
        seconds = sec;
    }

}
