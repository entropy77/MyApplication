package com.example.mkseo.myapplication;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mkseo on 2017. 4. 11..
 */

public class FirebaseRequest extends StringRequest {

    final static private String url = "http://leafrog.iptime.org:20080/message//";
    private Map<String, String> parameters;

    public FirebaseRequest(String account_id, String message, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        parameters = new HashMap<>();

        Log.d("FirebaseRequest", account_id);

        parameters.put("account_id", account_id);
        parameters.put("message", message);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
