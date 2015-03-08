package com.example.shiwangi.dataplan.utils;

import org.json.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by shiwangi on 27/2/15.
 */
public class OperatorPlan {
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
//    public static void main(String arg[] ) throws IOException, JSONException {
//        String url = "http://api.dataweave.in/v1/telecom_data/listByCircle/?api_key=b20a79e582ee4953ceccf41ac28aa08d&operator=Vodafone&circle=Karnataka&page=1&per_page=100";
//        System.out.println(readJsonFromUrl(url));
//    }
}
