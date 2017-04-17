package com.example.mkseo.myapplication.User.QRcodeScanPage;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mkseo on 2017. 3. 15..
 */

public class qrScanRequest extends StringRequest {

    final static private String url = "http://leafrog.iptime.org:20080/v1/product/get_item";
    private Map<String, String> parameters;
    private int statusCode;

    public qrScanRequest(String product_id, String table_no, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        super(Method.POST, url, listener, errorListener);
        parameters = new HashMap<>();

        parameters.put("product_id", product_id);
        parameters.put("table_no", table_no);
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