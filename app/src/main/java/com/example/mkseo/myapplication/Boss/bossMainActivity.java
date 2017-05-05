package com.example.mkseo.myapplication.Boss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.mkseo.myapplication.Boss.Fragments.orderListFragmentPage.boss_order_fragment;
import com.example.mkseo.myapplication.Boss.Fragments.itemListFragmentPage.itemListFragment;
import com.example.mkseo.myapplication.Boss.Fragments.etcBossFragment;
import com.example.mkseo.myapplication.LoginPage.loginActivity;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.User.Fragments.userOrderFragmentPage.user_order_fragment;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class bossMainActivity extends AppCompatActivity {

    static bossMainActivity bossMainActivity;
    private Button orderListButton, productListButton, etcBossButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss_main);

        bossMainActivity = this;

        // init XML objects
        orderListButton = (Button) findViewById(R.id.orderListButtonTag);
        productListButton = (Button) findViewById(R.id.productListButtonTag);
        etcBossButton = (Button) findViewById(R.id.etcButtonInBossTag);
        logoutButton = (Button) findViewById(R.id.logoutButtonOnBossMainActivity);

        final TextView informationTextview = (TextView) findViewById(R.id.informationTextOnBossMainActivity);

        // change selected button background color
        orderListButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        productListButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        etcBossButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        final SharedPreferences preferences = getApplicationContext().getSharedPreferences("IDPASSWORD", getApplicationContext().MODE_PRIVATE);
        informationTextview.setText(preferences.getString("login_id", null) + "님 반갑습니다!");

        // init fragment as orderListFragment
        switchFragment(new boss_order_fragment());

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(bossMainActivity.this, loginActivity.class);
                startActivity(intent);

                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().commit();
                askGoogleAboutTopics(FirebaseInstanceId.getInstance().getToken());
                finish();
            }
        });

        orderListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // change selected button background color
                orderListButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                productListButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                etcBossButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                switchFragment(new boss_order_fragment());

            }
        });

        productListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // change selected button background color
                orderListButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                productListButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                etcBossButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                switchFragment(new itemListFragment());
            }
        });

        etcBossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // change selected button background color
                orderListButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                productListButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                etcBossButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                switchFragment(new etcBossFragment());
            }
        });


    }

    public void orderListButtonClickEvent() {
        orderListButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        productListButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        etcBossButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        switchFragment(new user_order_fragment());
    }

    public void switchFragment(Fragment fragment) {

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.bossFragment, fragment);
            fragmentTransaction.commit();
        }
    }

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
                    Log.d(TAG, "delete topic: " + topic);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                }
            }

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
    // google iid check url
    private String Google_iid_check_url = "https://iid.googleapis.com/iid/info/";

    // google API_KEY(2017. 04. 12.)
    private String Google_API_KEY = "AAAA1kFKYlo:APA91bH0drmyubir837UfkDtoavFjzXyrSc0b3H-ZLsknM8j8JpCAZ59nvIE-MR42MHhkILcSwI9wZsxMe4E0y5jSHiQMaMRIEuaNfya3QOGGjo50VFHM8fUhP67T_6N4eyom7OE_FD4";

    String getRequest(String token, String API_KEY) throws IOException {

        Request request = new Request.Builder()
                .url(Google_iid_check_url + token + "?details=true")
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=" + API_KEY)
                .build();

        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();

    }

    private String TAG = this.getClass().getSimpleName();

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

    public static bossMainActivity getInstance() {
        return bossMainActivity;
    }
}