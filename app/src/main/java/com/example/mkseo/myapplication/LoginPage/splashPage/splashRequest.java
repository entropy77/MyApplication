package com.example.mkseo.myapplication.LoginPage.splashPage;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mkseo on 2017. 3. 13..
 */

public class splashRequest extends StringRequest {

    final static private String url = "http://leafrog.iptime.org:20080/test/ping";

    public splashRequest(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, listener, errorListener);

        // RetryPolicy manual
//        setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        setRetryPolicy(new DefaultRetryPolicy(300, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    // making new header for GET
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        params.put("paytalab", "splashActivity");
        return params;
    }
}
