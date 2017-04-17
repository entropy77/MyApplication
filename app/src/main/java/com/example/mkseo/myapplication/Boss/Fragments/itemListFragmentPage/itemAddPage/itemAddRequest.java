package com.example.mkseo.myapplication.Boss.Fragments.itemListFragmentPage.itemAddPage;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mkseo on 2017. 3. 16..
 */


public class itemAddRequest extends StringRequest {

    final static private String url = "http://leafrog.iptime.org:20080/v1/product/regist";
    private Map<String, String> parameters;
    private int statusCode;

    public itemAddRequest(String login_id, String password, String name, String price, String information, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        super(Method.POST, url, listener, errorListener);
        parameters = new HashMap<>();

        parameters.put("login_id", login_id);
        parameters.put("password", password);
        parameters.put("name", name);
        parameters.put("price", price);
        parameters.put("information", information);
    }

    public int getStatusCode() {
        return statusCode;
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