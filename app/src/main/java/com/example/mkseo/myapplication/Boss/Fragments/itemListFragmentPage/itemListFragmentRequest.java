package com.example.mkseo.myapplication.Boss.Fragments.itemListFragmentPage;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mkseo on 2017. 3. 16..
 */

public class itemListFragmentRequest extends StringRequest {

//    final static private String url = "http://leafrog.iptime.org:20080/v1/product/list";
    public String id;
    private Map<String, String> parameters;

    public itemListFragmentRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, listener, errorListener);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
