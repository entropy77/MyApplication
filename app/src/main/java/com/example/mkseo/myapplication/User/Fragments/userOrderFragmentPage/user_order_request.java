package com.example.mkseo.myapplication.User.Fragments.userOrderFragmentPage;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mkseo on 2017. 3. 22..
 */

public class user_order_request extends StringRequest {

    final static private String url = "http://leafrog.iptime.org:20080/v1/order/list";
    private Map<String, String> parameters;

    public user_order_request(String login_id, String password, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        super(Request.Method.POST, url, listener, errorListener);
        parameters = new HashMap<>();

        parameters.put("member_id", login_id);
        parameters.put("member_pw", password);

        // since status 0 isn't completed order
        parameters.put("status", "0");

        System.out.println("orderListFragmentRequest : " + parameters.toString());


    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
