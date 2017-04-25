package com.example.mkseo.myapplication.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mkseo.myapplication.LoginPage.loginActivity;
import com.example.mkseo.myapplication.R;
import com.example.mkseo.myapplication.User.Fragments.userOrderFragmentPage.user_order_fragment;
import com.example.mkseo.myapplication.User.Fragments.user_search_Fragment;
import com.example.mkseo.myapplication.User.Fragments.user_main_Fragment;
import com.example.mkseo.myapplication.User.Fragments.user_consume_Fragment;
import com.example.mkseo.myapplication.User.QRcodeScanPage.qrScanActivity;

public class userMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        // init Buttons
        final Button qrscanButton = (Button)findViewById(R.id.qrScanButton);
        final Button mainButton = (Button)findViewById(R.id.mainButton);
        final Button orderButton = (Button)findViewById(R.id.orderButton);
        final Button consumeButton = (Button)findViewById(R.id.consumeButton);
        final Button searchButton = (Button)findViewById(R.id.searchButton);
        final Button logoutButton = (Button)findViewById(R.id.logoutButtonOnUserMainActivity);

        final TextView informationTextview = (TextView)findViewById(R.id.informationTextOnUserMainActivity);

        // change selected button background color
        mainButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        orderButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        consumeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        searchButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // summon fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.userFragment, new user_main_Fragment());
        fragmentTransaction.commit();

        final SharedPreferences preferences = getApplicationContext().getSharedPreferences("IDPASSWORD", getApplicationContext().MODE_PRIVATE);
        informationTextview.setText(preferences.getString("login_id", null) + "님 반갑습니다!");

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userMainActivity.this, loginActivity.class);
                startActivity(intent);

                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().commit();
                finish();
            }
        });

        // event handling for qrscanButton
        qrscanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userMainActivity.this, qrScanActivity.class);
                startActivity(intent);
            }
        });

        // event handling for mainButton
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change selected button background color
                mainButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                orderButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                consumeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                searchButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                openFragment(new user_main_Fragment());
            }
        });

        // event handling for consumeButton
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change selected button background color
                mainButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                orderButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                consumeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                searchButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                openFragment(new user_order_fragment());
            }
        });

        // event handling for searchButton
        consumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change selected button background color
                mainButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                orderButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                consumeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                searchButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                openFragment(new user_consume_Fragment());
            }
        });

        // event handling for etcButton
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change selected button background color
                mainButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                orderButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                consumeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                searchButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                openFragment(new user_search_Fragment());
            }
        });

    }

    private void openFragment(Fragment newFragment) {
        Fragment containerFragment = getSupportFragmentManager().findFragmentById(R.id.userFragment);
        if (containerFragment.getChildFragmentManager().equals(newFragment)) {
            System.out.println("this fragment has been summoned already");
            return;
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.userFragment, newFragment);
            fragmentTransaction.commit();
        }
    }
}
