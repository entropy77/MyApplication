package com.example.mkseo.myapplication.User.PayingPage;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;


/**
 * Created by mkseo on 2017. 3. 17..
 */

public class payingRequest extends JsonObjectRequest {

    final static private String url = "http://leafrog.iptime.org:20080/v1/order/create";

    public payingRequest(JSONObject orderJSONMessage, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        super(Method.POST, url, orderJSONMessage, listener, errorListener);
        System.out.println(orderJSONMessage.toString());

        setRetryPolicy(new DefaultRetryPolicy(2000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
}