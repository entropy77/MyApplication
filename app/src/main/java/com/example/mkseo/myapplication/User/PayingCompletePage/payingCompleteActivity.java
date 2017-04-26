package com.example.mkseo.myapplication.User.PayingCompletePage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.mkseo.myapplication.User.PayingPage.payingActivity;
import com.example.mkseo.myapplication.User.QRcodeScanPage.qrScanActivity;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.User.itemInfoForUser;
import com.example.mkseo.myapplication.User.userMainActivity;

import java.util.ArrayList;

public class payingCompleteActivity extends AppCompatActivity {

    private String id;

    public static payingCompleteActivity payingCompleteActivity;

    // good response
    protected Response.Listener<String> getResponseListener() {
        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    reactor(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        return responseListener;
    }
    protected void reactor(String response) {
        try {
//            JSONObject jsonResponse = new JSONObject(response);
            Log.d("payingCompleteActivity", response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // error response
    protected Response.ErrorListener getErrorListener() {
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    errorReactor(error);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        return errorListener;
    }
    protected void errorReactor(VolleyError error) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        payingCompleteActivity = this;

        ArrayList<itemInfoForUser> infos;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paying_complete);

        // kill privious processes(qrScanActivity, PayingActivity)
        payingActivity.getInstance().finish();
        qrScanActivity.getInstance().finish();

        // catch passed data from qrScanActivity which are items and tableNumber
        Intent intent = getIntent();
        infos = (ArrayList<itemInfoForUser>) intent.getSerializableExtra("selectedItemArrayFrompayingActivity");
        id = String.valueOf(infos.get(infos.size() - 1).getCompany_id());

        Button quitButton = (Button)findViewById(R.id.quitButtonTag);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userMainActivity.getInstance().orderListButtonClickEvent();
                finish();
            }
        });

    }
}
