package com.example.mkseo.myapplication.User.RegisterPage;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mkseo on 2017. 3. 12..
 */

public class validateRequestDEFRECATED extends StringRequest {

    final static private String url = "http://mk7seo.woobi.co.kr/validate.php";
    private Map<String, String> parameters;

    public validateRequestDEFRECATED(String login_id, Response.Listener<String> listener) {

        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();

        parameters.put("login_id", login_id);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}