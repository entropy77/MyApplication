package com.example.mkseo.myapplication.Boss.Fragments.orderListFragmentPage;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mkseo on 2017. 3. 18..
 */

public class boss_order_request extends StringRequest {

    final static private String url = "http://leafrog.iptime.org:20080/v1/order/list";
    private Map<String, String> parameters;

    public boss_order_request(String login_id, String password, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        super(Method.POST, url, listener, errorListener);
        parameters = new HashMap<>();

        parameters.put("company_id", login_id);
        parameters.put("company_pw", password);

        // since status 0 isn't completed order
        parameters.put("status", "0");

        // gotta figure out this problem
//        parameters.put("limit", "30");

        System.out.println("orderListFragmentRequest : " + parameters.toString());


    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}