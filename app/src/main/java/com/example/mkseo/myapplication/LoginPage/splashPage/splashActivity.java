package com.example.mkseo.myapplication.LoginPage.splashPage;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.example.mkseo.myapplication.loading_dialog;

import org.json.JSONObject;

public class splashActivity extends AppCompatActivity {

    final int MY_PERMISSION_REQUEST_CODE = 100;

    final String TAG = this.getClass().getSimpleName();

    private final int SPLASH_DISPLAY_LENGTH = 2000; // 2 sec
    private loading_dialog loading_dialog;
    private Dialog dialog;

    // good response
    protected Response.Listener<String> getResponseListener() {
        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "connection check - success");
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
                if (checkCameraPermission())
                    goToLoginActivity();
                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(splashActivity.this);
                    dialog = builder.setMessage("QR코드로 주문하기 위해 카메라 권한을 요청합니다! :D")
                            .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_CODE);
                                    }
                                }
                            })
                            .create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                }

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
                    Log.d(TAG, "connection check - fail");
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
        String errorMessage = "서버와 연결할 수 없습니다. 와이파이나 데이터를 킨 뒤에 다시 실행해 주세요";

        AlertDialog.Builder builder = new AlertDialog.Builder(splashActivity.this);
        dialog = builder.setMessage(errorMessage)
                .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .create();
        dialog.show();

    }

    // request cameraPermission
    protected boolean checkCameraPermission() {
        int APIVersion = android.os.Build.VERSION.SDK_INT;

        boolean isCameraPermissionGranted = false;

        // if API Level is up to 23
        if (APIVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkCameraPermissionHasGranted()) {
                Log.i(this.getClass().getSimpleName(), "camera permission already granted");
                isCameraPermissionGranted = true;
            } else {
                Log.i(this.getClass().getSimpleName(), "camera permission has not granted");
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_CODE);
            }
        } else {
            // we set this as android API Level is lower than 23
            // so that we don't need to ask request for camera
            Log.i(this.getClass().getSimpleName(), "this device's API Levle is under 23");
            isCameraPermissionGranted = true;
        }

        return isCameraPermissionGranted;
    }
    private boolean checkCameraPermissionHasGranted() {
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
                    } else {
                        Log.i(this.getClass().getSimpleName(), "camera permission not granted");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                // enter the code if user deny for camera using permission
                                // go to LoginActivity for now
                                // need to ask permission when user uses QRscanning again
                                AlertDialog.Builder builder = new AlertDialog.Builder(splashActivity.this);
                                dialog = builder.setMessage("QR코드로 주문하기 위해 나중에라도 꼭 허용해 주세요! ;D")
                                        .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    goToLoginActivity();
                                                }
                                            }
                                        })
                                        .create();
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();


                                return;
                            }
                        }
                    }

                }
                break;
        }
    }

    // login activity intent
    private void goToLoginActivity() {
        Intent intent = new Intent(splashActivity.this, loginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // make login_dialog's background transparent
        loading_dialog = new loading_dialog(splashActivity.this);
        loading_dialog.setup();

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
