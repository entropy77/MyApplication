package com.example.mkseo.myapplication.LoginPage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.LoginPage.RegisterPage.registerActivity;
import com.example.mkseo.myapplication.Boss.bossMainActivity;
import com.example.mkseo.myapplication.User.userMainActivity;
import com.example.mkseo.myapplication.loading_dialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class loginActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private loading_dialog loading_dialog;

    final String TAG = this.getClass().getSimpleName();

    // google iid check url
    private String Google_iid_check_url = "https://iid.googleapis.com/iid/info/";

    // google API_KEY(2017. 04. 12.)
    private String Google_API_KEY = "AAAA1kFKYlo:APA91bH0drmyubir837UfkDtoavFjzXyrSc0b3H-ZLsknM8j8JpCAZ59nvIE-MR42MHhkILcSwI9wZsxMe4E0y5jSHiQMaMRIEuaNfya3QOGGjo50VFHM8fUhP67T_6N4eyom7OE_FD4";

    // token for push message
    private String token;

    // server side data
    private String id;
    private String phone;
    private String type;

    // local side data
    private String login_id;
    private String password;

    // for softKeyboard handling
    private InputMethodManager inputMethodManager;

    public void unsubscribeTopics(String response) {
        try {
            JSONObject firstJSONObject = new JSONObject(response);

            if (firstJSONObject.has("rel")) {
                String firstString = firstJSONObject.getString("rel");
                JSONObject secondJSONObject = new JSONObject(firstString);
                String secondString = secondJSONObject.getString("topics");
                JSONObject thirdJSONObject = new JSONObject(secondString);

                for (int i = 0; i < thirdJSONObject.length(); i++) {
                    String topic = thirdJSONObject.names().getString(i);
                    System.out.println("delete topic: " + topic);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                }
            }
            // topic subscribe for push message
            System.out.println("Subscribe id as topic: " + id);
            FirebaseMessaging.getInstance().subscribeToTopic(id);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // getting topics from GOOGLE_APIS related sever-client connecting methods
    // go to askGoogleAboutTopics first
    // then we use method getRequest to get subscribed topic list
    // in get request, we ask subscribed topic list
    // than unsubscribe them

    OkHttpClient client = new OkHttpClient();

    String getRequest(String token, String API_KEY) throws IOException {

        Request request = new Request.Builder()
                .url(Google_iid_check_url + token + "?details=true")
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=" + API_KEY)
                .build();

        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();

    }

    public void askGoogleAboutTopics(final String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = getRequest(token, Google_API_KEY);
                    unsubscribeTopics(response);
                    Log.d(TAG, "response - " + response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

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

            // make dialog gone first
            loading_dialog.dismiss();

            id = jsonResponse.getString("id");
            phone = jsonResponse.getString("phone");
            type = jsonResponse.getString("type");

            Log.d(TAG, "receiving data from server..");
            Log.d(TAG, "id - " + id);
            Log.d(TAG, "phone - " + phone);
            Log.d(TAG, "type - " + type);

            // put id and password into SharedPreferences
            // this is because id and password are used all around the activities
            // we make ID and password as static vars.
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("IDPASSWORD", getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("login_id", login_id);
            editor.putString("password", password);
            editor.putString("id", id);
            editor.putString("phone", phone);
            editor.putString("type", type);
            editor.commit();


            // topic subscribe for push message
            token = FirebaseInstanceId.getInstance().getToken();
            Log.e("token", token);

            // askGoogleaboutTopics based on token parameter
            // and the Topics will unsubscribe topics
            // and subscribe topic for this account_id
            askGoogleAboutTopics(token);

            // switching Intent actions
            Intent intent;
            switch (Integer.parseInt(type)) {
                case 0:
                    System.out.println("this ID is user -> userMainActivity");
                    intent = new Intent(loginActivity.this, userMainActivity.class);
                    break;
                case 1:
                    System.out.println("this ID is boss -> bossMainActivity");
                    intent = new Intent(loginActivity.this, bossMainActivity.class);
                    break;
                case 2:
                    System.out.println("this ID is admin -> userMainActivity(temporally)");
                    intent = new Intent(loginActivity.this, userMainActivity.class);
                    break;
                default:
                    System.out.println("something is wrong, type isn't 0, 1 or 2 -> userMainActivity(temporally)");
                    intent = new Intent(loginActivity.this, userMainActivity.class);
                    break;
            }

            // carry on with view changing
            startActivity(intent);
            finish();

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
        AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity.this);
        dialog = builder.setMessage(errorMessage)
                .setNegativeButton("확인", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // loading dialog init
        loading_dialog = new loading_dialog(loginActivity.this);
        loading_dialog.setup();

        // XML object assign(Buttons)
        final Button registerButton = (Button) findViewById(R.id.registerButtonOnLoginActivityTag);
        final Button loginButton = (Button) findViewById(R.id.loginButtonOnLoginActivityTag);

        // XML object assign(EditText)
        final EditText idText = (EditText) findViewById(R.id.idTextOnLoginActivityTag);
        final EditText passwordText = (EditText) findViewById(R.id.passwordTextOnLoginActivityTag);

        // assign OnClicklistener on login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login_id = idText.getText().toString();
                password = passwordText.getText().toString();

                if (login_id.equals("") || password.equals("")) {

                    String errorMessage = "아이디와 비밀번호를 입력해 주세요";

                    AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity.this);
                    dialog = builder.setMessage(errorMessage)
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                } else {
                    loading_dialog.show();
                    Log.d(TAG, "login_id - " + login_id);
                    Log.d(TAG, "password - " + password);
                    Log.d(TAG, "sending login_id and password to server...");

                    loginRequest loginRequest = new loginRequest(login_id, password, getResponseListener(), getErrorListener());
                    RequestQueue queue = Volley.newRequestQueue(loginActivity.this);
                    queue.add(loginRequest);
                }
            }

        });

        // assign onclicklistener on register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // register button method(go to registerActivity)
                Intent intent = new Intent(loginActivity.this, registerActivity.class);
                startActivity(intent);
            }
        });

        // make keyboard disappear when user click background(which is LinearLayout)
        // InputMethodManager
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // backLinearLayout XML object assign
        final LinearLayout backLinearLayout = (LinearLayout) findViewById(R.id.backLinearLayout_activity_login);
        backLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.hideSoftInputFromWindow(idText.getWindowToken(), 0);
                inputMethodManager.hideSoftInputFromWindow(passwordText.getWindowToken(), 0);
            }
        });


    }


}