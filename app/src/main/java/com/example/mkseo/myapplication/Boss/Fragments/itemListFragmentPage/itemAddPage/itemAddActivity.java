package com.example.mkseo.myapplication.Boss.Fragments.itemListFragmentPage.itemAddPage;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.mkseo.myapplication.Boss.bossMainActivity;
import com.example.mkseo.myapplication.LoginPage.loginActivity;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.User.userMainActivity;
import com.example.mkseo.myapplication.loading_dialog;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

public class itemAddActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    private Dialog dialog;
    private loading_dialog loading_dialog;

    private String login_id;
    private String password;

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
            loading_dialog.dismiss();

            Log.d(TAG, "add item done!");

            AlertDialog.Builder builder = new AlertDialog.Builder(itemAddActivity.this);
            dialog = builder.setMessage("상품 추가가 정상적으로 완료되었습니다")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
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

        // make loading_dialog gone
        loading_dialog.dismiss();

        // error string logging
        Log.d(TAG, error.toString());

        int statusCode;
        if (error.networkResponse != null) {
            // 401 error code is unreadable so be aware of that
            statusCode = error.networkResponse.statusCode;
        } else {
            // 000 - can't connect with server
            statusCode = 401;
        }

        String errorMessage;

        switch (statusCode) {
            case 400:
                errorMessage = "빈칸없이 작성해주시기 바랍니다";
                break;
            case 401:
                errorMessage = "가격이 숫자로 입력되지 않았습니다";
                break;
            case 404:
                errorMessage = "업주 아이디가 아닙니다!";
                break;
            case 410:
                errorMessage = "쿼리 에러가 발생하였습니다";
                break;
            default:
                errorMessage = "알수없는 에러가 발생하였습니다";
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(itemAddActivity.this);
        dialog = builder.setMessage(errorMessage)
                .setNegativeButton("확인", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_add);

        // init loading dialog
        loading_dialog = new loading_dialog(itemAddActivity.this);
        loading_dialog.setup();

        // bring out Static ID and password using SharedPreferences
        // which is "login_id" and "password"
        // I think it should managed in server side, not local side so it should be debated later
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("IDPASSWORD", getApplicationContext().MODE_PRIVATE);
        login_id = preferences.getString("login_id", null);
        password = preferences.getString("password", null);

        final EditText itemNameText = (EditText) findViewById(R.id.itemNameTextOnItemRegisterTag);
        final EditText itemPriceText = (EditText) findViewById(R.id.itemPriceTextOnItemRegisterTag);
        final EditText itemInformationText = (EditText) findViewById(R.id.itemInformationTextOnItemRegisterTag);
        final Button itemRegisterButton = (Button) findViewById(R.id.itemRegisterButtonOnItemRegisterTag);

        itemRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String itemName = itemNameText.getText().toString();
                final String itemPrice = itemPriceText.getText().toString();
                final String itemInformation = itemInformationText.getText().toString();

                Log.d(TAG, itemName + " " + itemPrice);
                Log.d(TAG, itemInformation);

                loading_dialog.show();
                itemAddRequest itemAddRequest = new itemAddRequest(login_id, password, itemName, itemPrice, itemInformation, getResponseListener(), getErrorListener());
                RequestQueue queue = Volley.newRequestQueue(itemAddActivity.this);
                queue.add(itemAddRequest);
            }
        });

    }
}
