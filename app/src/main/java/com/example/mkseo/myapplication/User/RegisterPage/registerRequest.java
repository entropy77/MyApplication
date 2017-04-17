package com.example.mkseo.myapplication.User.RegisterPage;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by mkseo on 2017. 3. 12..
 */

public class registerRequest extends StringRequest {

//    final static private String url = "http://mk7seo.woobi.co.kr/register.php";
    final static private String url = "http://leafrog.iptime.org:20080/v1/account/regist";
    private Map<String, String> parameters;
    private int statusCode;


    public registerRequest(String login_id, String password, String phone, String type, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        super(Method.POST, url, listener, errorListener);
        parameters = new HashMap<>();

        parameters.put("login_id", login_id);
        parameters.put("password", password);
        parameters.put("phone", phone);
        parameters.put("type", type);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        statusCode = response.statusCode;
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}