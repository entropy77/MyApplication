package com.example.mkseo.myapplication.Boss.Fragments.orderListFragmentPage;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mkseo on 2017. 3. 23..
 */

public class boss_order_status_change_request extends StringRequest {

    final static private String url = "http://leafrog.iptime.org:20080/v1/order/change_status";
    private Map<String, String> parameters;

    public boss_order_status_change_request(String login_id, String password, String order_id, String status, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        super(Request.Method.PUT, url, listener, errorListener);
        parameters = new HashMap<>();

        parameters.put("login_id", login_id);
        parameters.put("password", password);
        parameters.put("order_id", order_id);
        parameters.put("status", status);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
