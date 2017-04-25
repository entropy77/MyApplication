package com.example.mkseo.myapplication.LoginPage.splashPage;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.mkseo.myapplication.Boss.bossMainActivity;
import com.example.mkseo.myapplication.LoginPage.loginActivity;
import com.example.mkseo.myapplication.LoginPage.loginRequest;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.User.userMainActivity;
import com.example.mkseo.myapplication.loading_dialog;

import org.json.JSONObject;

public class splashActivity extends AppCompatActivity {

    final int MY_PERMISSION_REQUEST_CODE = 100;

    final String TAG = this.getClass().getSimpleName();

    private final int SPLASH_DISPLAY_LENGTH = 2000; // 2 sec
    private loading_dialog loading_dialog;
    private Dialog dialog;

    private String login_id;
    private String password;
    private String type;

    boolean prevLogin = false;

    // ping test request(splashRequest)
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

                // check if this cellphone already login for this app
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("IDPASSWORD", getApplicationContext().MODE_PRIVATE);

                login_id = (preferences.getString("login_id", null) != null) ? preferences.getString("login_id", null) : null;
                password = (preferences.getString("password", null) != null) ? preferences.getString("password", null) : null;
                type = (preferences.getString("type", null) != null) ? preferences.getString("type", null) : null;

                if (login_id != null && password != null && type != null) {
                    prevLogin = true;

                    Log.d(TAG, "SharedPreferences -> already has login experience");
                    Log.d(TAG, "login_id - " + login_id);
                    Log.d(TAG, "password - " + password);
                    Log.d(TAG, "type - " + type);
                }

                // if request had been proved
                // go to login activity
                // we do ask camera permission request
                if (checkAPIVersion()) {

                    if (prevLogin) {
                        // this request include move to main page(intent)
                        requestLogin(login_id, password);
                    } else {
                        moveActivity(splashActivity.this, loginActivity.class);
                    }


                } else {
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

    // login request(loginRequest)
    // good response
    protected Response.Listener<String> getResponseListener(final String login_id, final String password) {
        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "connection check - success");
                    loading_dialog.dismiss();
                    reactor(response, login_id, password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        return responseListener;
    }

    protected void reactor(String response, String login_id, String password) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String type = jsonResponse.getString("type");

            switch (Integer.parseInt(type)) {
                case 0:
                    System.out.println("this ID is user -> userMainActivity");
                    moveActivity(splashActivity.this, userMainActivity.class);
                    break;
                case 1:
                    System.out.println("this ID is boss -> bossMainActivity");
                    moveActivity(splashActivity.this, bossMainActivity.class);
                    break;
                case 2:
                    System.out.println("this ID is admin -> userMainActivity(temporally)");
                    moveActivity(splashActivity.this, userMainActivity.class);
                    break;
                default:
                    System.out.println("something is wrong, type isn't 0, 1 or 2 -> userMainActivity(temporally)");
                    moveActivity(splashActivity.this, userMainActivity.class);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // error response
    protected Response.ErrorListener getErrorListener(final String login_id, final String password) {
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.d(TAG, "connection check - fail");
                    loading_dialog.dismiss();
                    errorReactor(error, login_id, password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        return errorListener;
    }

    protected void errorReactor(VolleyError error, String login_id, String password) {

        // error string logging
        Log.d(TAG, error.toString());

        int statusCode;
        if (error.networkResponse != null) {
            // 401 error code is unreadable so be aware of that
            statusCode = error.networkResponse.statusCode;
        } else {
            // 000 - can't connect with server
            statusCode = 100;
        }

        String errorMessage;

        switch (statusCode) {
            case 100:
                errorMessage = "서버와 연결할 수 없습니다. 와이파이나 데이터를 켜시고 다시 시도해 주세요";
                break;
            case 400:
                errorMessage = "빈칸없이 작성해주시기 바랍니다";
                break;
            case 404:
                errorMessage = "아이디나 비밀번호가 일치하지 않습니다";
                break;
            case 410:
                errorMessage = "쿼리 에러가 발생하였습니다";
                break;
            default:
                errorMessage = "알수없는 에러가 발생하였습니다";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(splashActivity.this);
        dialog = builder.setMessage(errorMessage)
                .setNegativeButton("확인", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    // request cameraPermission
    protected boolean checkAPIVersion() {
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
                        Log.d(this.getClass().getSimpleName(), "camera permission granted");
                        if (prevLogin)
                            requestLogin(login_id, password);
                        else
                            moveActivity(splashActivity.this, loginActivity.class);
                    } else {
                        Log.d(this.getClass().getSimpleName(), "camera permission not granted");
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
                                                    if (prevLogin)
                                                        requestLogin(login_id, password);
                                                    else
                                                        moveActivity(splashActivity.this, loginActivity.class);
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
    private void moveActivity(Context oldActivity, Class<?> newActivity) {
        Intent intent = new Intent(oldActivity, newActivity);
        startActivity(intent);
        finish();
    }

    private void requestLogin(String login_id, String password) {

        loading_dialog.show();

        loginRequest loginRequest = new loginRequest(login_id, password, getResponseListener(login_id, password), getErrorListener(login_id, password));
        RequestQueue queue = Volley.newRequestQueue(splashActivity.this);
        queue.add(loginRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // make login_dialog's background transparent
        loading_dialog = new loading_dialog(splashActivity.this);
        loading_dialog.setup();

        // delay splash activity for SPLASH_DISPLAY_LENGTH : 2 sec
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
