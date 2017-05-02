package com.example.mkseo.myapplication.LoginPage;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mkseo on 2017. 5. 2..
 */

public class registRequest extends StringRequest {

    final static private String url = "http://leafrog.iptime.org:20080/message/regist";
    private Map<String, String> parameters;

    public registRequest(String login_id, String password, String reg_id, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        super(Request.Method.POST, url, listener, errorListener);
        parameters = new HashMap<>();

        parameters.put("login_id", login_id);
        parameters.put("password", password);
        parameters.put("reg_id", reg_id);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
