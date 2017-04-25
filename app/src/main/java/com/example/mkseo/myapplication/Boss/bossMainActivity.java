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
import android.widget.Button;
import android.widget.TextView;

import com.example.mkseo.myapplication.Boss.Fragments.orderListFragmentPage.boss_order_fragment;
import com.example.mkseo.myapplication.Boss.Fragments.itemListFragmentPage.itemListFragment;
import com.example.mkseo.myapplication.Boss.Fragments.etcBossFragment;
import com.example.mkseo.myapplication.LoginPage.loginActivity;
import com.example.mkseo.myapplication.R;

public class bossMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss_main);
        
        // init XML objects
        final Button orderListButton = (Button)findViewById(R.id.orderListButtonTag);
        final Button productListButton = (Button)findViewById(R.id.productListButtonTag);
        final Button etcBossButton = (Button)findViewById(R.id.etcButtonInBossTag);
        final Button logoutButton = (Button)findViewById(R.id.logoutButtonOnBossMainActivity);

        final TextView informationTextview = (TextView)findViewById(R.id.informationTextOnBossMainActivity);

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

    public void switchFragment(Fragment fragment) {

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.bossFragment, fragment);
            fragmentTransaction.commit();
        }
    }
}