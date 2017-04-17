package com.example.mkseo.myapplication.User.RegisterPage;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.mkseo.myapplication.R;

public class registerActivity extends AppCompatActivity {

    private String login_id;
    private String password;
    private String phone;
    private String type = "0";
    private AlertDialog dialog;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        final Button registerButton = (Button)findViewById(R.id.registerButtonOnRegisterActivityTag);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login_id = login_idText.getText().toString();
                String password = passwordText.getText().toString();
                String phone = phoneText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(registerActivity.this);
                            dialog = builder.setMessage("Success!")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            registerActivity.this.finish();
                                        }
                                    })
                                    .create();
                            dialog.show();

                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int statusCode = error.networkResponse.statusCode;
                        String errorMessage;

                        switch(statusCode) {
                            case 400:
                                errorMessage = "fill out every blank";
                                break;
                            case 401:
                                errorMessage = "type is wrong";
                                break;
                            case 402:
                                errorMessage = "use number only on phone number";
                                break;
                            case 405:
                                errorMessage = "ID is already exist";
                                break;
                            case 410:
                                errorMessage = "Query got wrong, sorry!";
                                break;
                            default:
                                errorMessage = "error";
                                break;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(registerActivity.this);
                        dialog = builder.setMessage(errorMessage)
                                .setNegativeButton("ok", null)
                                .create();
                        dialog.show();


                        System.out.println("error occured : " + error.getMessage());
                        System.out.println("error status code : " + statusCode);
                    }
                };

                registerRequest registerRequest = new registerRequest(login_id, password, phone, type, responseListener, errorListener);
                RequestQueue queue = Volley.newRequestQueue(registerActivity.this);
                queue.add(registerRequest);
            }
        });
    }
}
