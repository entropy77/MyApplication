package com.example.mkseo.myapplication.Boss.Fragments.orderListFragmentPage;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mkseo on 2017. 5. 2..
 */

public class boss_order_push_request extends StringRequest {

    final static private String url = "http://leafrog.iptime.org:20080/message/send";
    Map<String, String> parameters;

    String TAG = getClass().getSimpleName();

    public boss_order_push_request(String id, String msg, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        parameters = new HashMap<>();

        parameters.put("id", id);
        parameters.put("msg", msg);

        setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
