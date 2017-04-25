package com.example.mkseo.myapplication.User.PayingPage;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by mkseo on 2017. 3. 17..
 */

public class payingRequest extends StringRequest {

    final static private String url = "http://leafrog.iptime.org:20080/v1/order/create";
    private String jsonBody;

    public payingRequest(JSONObject orderJSONMessage, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        super(Method.POST, url, listener, errorListener);
        System.out.println(orderJSONMessage.toString());


        jsonBody = orderJSONMessage.toString();
        Log.d(this.getClass().getSimpleName(), "orderJSONMessage - " + jsonBody);

        setRetryPolicy(new DefaultRetryPolicy(2000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return jsonBody.getBytes();
    }
}