package com.example.mkseo.myapplication.Boss.Fragments.itemListFragmentPage.itemAddPage;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.loading_dialog;

public class itemAddActivity extends AppCompatActivity {

    private Dialog dialog;
    private loading_dialog loading_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_add);

        // init loading dialog
        loading_dialog = new loading_dialog(itemAddActivity.this);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            loading_dialog.dismiss();

                            System.out.println("Add item done!");

                            AlertDialog.Builder builder = new AlertDialog.Builder(itemAddActivity.this);
                            dialog = builder.setMessage("Add item done!")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .create();
                            dialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int statusCode = 401;

                        loading_dialog.dismiss();

                        // statusCode starts with 401 since Volley can't detect 401 and networkResponse is null
                        // this must be cautioned!!!!!!!!!!!!!!!!!!!!!!
                        // 2017. 03. 16.
                        if (error.networkResponse != null) {
                            statusCode = error.networkResponse.statusCode;
                        }

                        System.out.println("error : " + error.toString());
                        String errorMessage;

                        switch (statusCode) {
                            case 400:
                                errorMessage = "Fill out the form";
                                break;
                            case 401:
                                errorMessage = "Price is not a Number";
                                break;
                            case 404:
                                errorMessage = "this is not a boss account";
                                break;
                            case 410:
                                errorMessage = "Query error! sorry!";
                                break;
                            default:
                                errorMessage = "Unknown error";
                                break;
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(itemAddActivity.this);
                        dialog = builder.setMessage(errorMessage)
                                .setNegativeButton("ok", null)
                                .create();
                        dialog.show();
                    }
                };

                // bring out Static ID and password using SharedPreferences
                // which is "login_id" and "password"
                // I think it should managed in server side, not local side so it should be debated later
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("IDPASSWORD", getApplicationContext().MODE_PRIVATE);
                String login_id = preferences.getString("login_id", null);
                String password = preferences.getString("password", null);

                System.out.println(itemName);
                System.out.println(itemPrice);
                System.out.println(itemInformation);

                loading_dialog.show();
                itemAddRequest itemAddRequest = new itemAddRequest(login_id, password, itemName, itemPrice, itemInformation, responseListener, errorListener);
                RequestQueue queue = Volley.newRequestQueue(itemAddActivity.this);
                queue.add(itemAddRequest);
            }
        });

    }
}
