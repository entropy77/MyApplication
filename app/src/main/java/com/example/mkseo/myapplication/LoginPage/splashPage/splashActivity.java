package com.example.mkseo.myapplication.LoginPage.splashPage;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.mkseo.myapplication.LoginPage.loginActivity;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.User.QRcodeScanPage.qrScanActivity;
import com.example.mkseo.myapplication.User.userMainActivity;
import com.example.mkseo.myapplication.Boss.bossMainActivity;
import com.example.mkseo.myapplication.loading_dialog;

import org.json.JSONObject;

public class splashActivity extends AppCompatActivity {

    final int MY_PERMISSION_REQUEST_CODE = 100;

    private final int SPLASH_DISPLAY_LENGTH = 2000; // 2 sec
    private loading_dialog loading_dialog;
    private Dialog dialog;

    // for killed by other activity
    static splashActivity splashActivity;

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
            String status = jsonResponse.getString("status");

            // when we are connected with server
            if (status.equals("ok")) {
                System.out.println("splashActivity : succeeded to connect with server");

                // server connection has done -> dialog message dismiss
                loading_dialog.dismiss();

                // if request had been proved
                // go to login activity
                // we do ask camera permission request
                if (requestCameraPermission())
                    goToLoginActivity();

                // if not
            } else {
                System.out.println("failed to connect with server");
            }

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
        System.out.println("error occured : " + error);

        loading_dialog.dismiss();
        String errorMessage = "Cannot connect with server. turn on the WiFi or data and try again";

        AlertDialog.Builder builder = new AlertDialog.Builder(splashActivity.this);
        dialog = builder.setMessage(errorMessage)
                .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create();
        dialog.show();

    }


    // request cameraPermission
    protected boolean requestCameraPermission() {
        int APIVersion = android.os.Build.VERSION.SDK_INT;

        boolean isCameraPermissionGranted = false;

        // if API Level is up to 23
        if (APIVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkCAMERAPermission()) {
                Log.i(this.getClass().getSimpleName(), "camera permission already granted");
                isCameraPermissionGranted = true;
            } else {
                Log.i(this.getClass().getSimpleName(), "camera permission already not granted");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_CODE);
            }
        } else {
            // we set this as android API Level is lower than 23
            // so that we don't need to ask request for camera
            Log.i(this.getClass().getSimpleName(), "this device's API Levle is under 23");
            isCameraPermissionGranted = true;
        }

        return isCameraPermissionGranted;
    }
    private boolean checkCAMERAPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    // after check CAMERA permission check - reactor
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantReults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantReults.length > 0) {
                    boolean cameraAccepted = (grantReults[0] == PackageManager.PERMISSION_GRANTED);
                    if (cameraAccepted) {
                        Log.i(this.getClass().getSimpleName(), "camera permission granted");
//                        Intent intent = new Intent(splashActivity.this, loginActivity.class);
//                        startActivity(intent);
                    } else {
                        Log.i(this.getClass().getSimpleName(), "camera permission not granted");

//                        finish();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                showMessagePermission("권한 허가를 요청합니다",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }

                }
                break;
        }
    }
    private void showMessagePermission(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("허용", okListener)
                .create()
                .show();
    }

    // login activity intent
    private void goToLoginActivity() {
        Intent intent = new Intent(splashActivity.this, loginActivity.class);
        startActivity(intent);
    }

    public static splashActivity getInstance() {
        return splashActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // for killed by other activity
        splashActivity = this;

        // make login_dialog's background transparent
        loading_dialog = new loading_dialog(splashActivity.this);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // delay splash activity for SPLASH_DSPLAY_LENGTH : 2 sec
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // loading_dialog shows first
                loading_dialog.show();

                // request GET message to server
                splashRequest splashRequest = new splashRequest(getResponseListener(), getErrorListener());
                RequestQueue queue = Volley.newRequestQueue(splashActivity.this);
                queue.add(splashRequest);

            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
