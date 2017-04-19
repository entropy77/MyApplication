package com.example.mkseo.myapplication.LoginPage.RegisterPage;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

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

public class registerActivity extends AppCompatActivity {

    private String type = "0";

    private AlertDialog dialog;
    private loading_dialog loading_dialog;

    private String TAG = this.getClass().getSimpleName();

    // for softKeyboard handling
    private InputMethodManager inputMethodManager;

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
            JSONObject jsonResponse = new JSONObject(response);

            loading_dialog.dismiss();

            String message = "회원가입에 성공하였습니다 :D";

            AlertDialog.Builder builder = new AlertDialog.Builder(registerActivity.this);
            dialog = builder.setMessage(message)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            registerActivity.this.finish();
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
            statusCode = 000;
        }

        alert(statusCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loading_dialog = new loading_dialog(registerActivity.this);
        loading_dialog.setup();

        // assign XML objects
        final EditText login_idText = (EditText) findViewById(R.id.idTextOnRegisterActivityTag);
        final EditText passwordText = (EditText) findViewById(R.id.passwordTextOnRegisterActivityTag);
        final EditText phoneText = (EditText) findViewById(R.id.phoneTextTag);

        // radioGroup XML assign + assign value on local var
        RadioGroup typeRadioGroup = (RadioGroup) findViewById(R.id.typeRadioGroupTag);
        typeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.userRadioButtonTag:
                        type = "0";
                        break;
                    case R.id.bossRadioButtonTag:
                        type = "1";
                        break;
                    default:
                        type = "0";
                        break;
                }
            }
        });

        final Button registerButton = (Button) findViewById(R.id.registerButtonOnRegisterActivityTag);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String login_id = login_idText.getText().toString();
                String password = passwordText.getText().toString();
                String phone = phoneText.getText().toString();

                // get error code locally first
                int statusCode = checkRegister(login_id, password, phone);
                Log.d(TAG, "statusCode - " + Integer.toString(statusCode));

                // after pass the local error check
                // connect with server
                if (statusCode != 200) {
                    // if it didn't pass local check
                    alert(statusCode);
                } else {
                    // if it pass local check
                    loading_dialog.show();

                    Log.d(TAG, "login_id - " + login_id);
                    Log.d(TAG, "password - " + password);
                    Log.d(TAG, "phone - " + phone);
                    Log.d(TAG, "type - " + type);
                    Log.d(TAG, "sending data to server..");

                    registerRequest registerRequest = new registerRequest(login_id, password, phone, type, getResponseListener(), getErrorListener());
                    RequestQueue queue = Volley.newRequestQueue(registerActivity.this);
                    queue.add(registerRequest);
                }
            }
        });

        // make keyboard disappear when user click background(which is LinearLayout)
        // InputMethodManager
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // backLinearLayout XML object assign
        final LinearLayout backLinearLayout = (LinearLayout) findViewById(R.id.backLinearLayout_activity_register);
        backLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.hideSoftInputFromWindow(login_idText.getWindowToken(), 0);
                inputMethodManager.hideSoftInputFromWindow(passwordText.getWindowToken(), 0);
                inputMethodManager.hideSoftInputFromWindow(phoneText.getWindowToken(), 0);
            }
        });
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private int checkRegister(String login_id, String password, String phone) {

        int statusCode = 200;

        if (login_id.equals(""))
            statusCode = 100;
            // 100 : login_id empty
        else if (!isEmailValid(login_id))
            statusCode = 101;
            // 101 : login_id format isn't email type
        else if (password.equals(""))
            statusCode = 110;
            // 110 : password empty
        else if (phone.equals(""))
            statusCode = 120;
            // 120 : phone empty
        else if (!phone.matches(".*\\d+.*"))
            statusCode = 402;
            // 402 : phone string has characters

        return statusCode;
    }

    private void alert(int statusCode) {

        String errorMessage;

        switch (statusCode) {
            case 000:
                errorMessage = "서버와 연결할 수 없습니다. 와이파이나 데이터를 켜시고 다시 시도해 주세요";
                break;
            // local error code
            // 402 is on both local and server
            case 100:
                errorMessage = "ID를 입력해 주세요";
                break;
            case 101:
                errorMessage = "ID는 Email을 입력해 주세요";
                break;
            case 110:
                errorMessage = "비밀번호를 입력해 주세요";
                break;
            case 120:
                errorMessage = "핸드폰 번호를 입력해 주세요";
                break;
            // server error code
            case 400:
                errorMessage = "빈칸없이 작성해주시기 바랍니다";
                break;
            case 401:
                errorMessage = "사용자 유형이 잘못되었습니다";
                break;
            case 402:
                errorMessage = "핸드폰번호는 숫자만 입력해 주세요";
                break;
            case 404:
                errorMessage = "아이디나 비밀번호가 일치하지 않습니다";
                break;
            case 405:
                errorMessage = "중복되는 아이디가 있습니다";
                break;
            case 410:
                errorMessage = "쿼리 에러가 발생하였습니다";
                break;
            default:
                errorMessage = "알수없는 에러가 발생하였습니다";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(registerActivity.this);
        dialog = builder.setMessage(errorMessage)
                .setNegativeButton("확인", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
