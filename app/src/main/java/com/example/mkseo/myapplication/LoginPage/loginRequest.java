package com.example.mkseo.myapplication.LoginPage;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mkseo on 2017. 3. 12..
 */

public class loginRequest extends StringRequest {

    final static private String url = "http://leafrog.iptime.org:20080/v1/account/login";
    private Map<String, String> parameters;

    public loginRequest(String login_id, String password, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        super(Method.POST, url, listener, errorListener);
        parameters = new HashMap<>();

        parameters.put("login_id", login_id);
        parameters.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}