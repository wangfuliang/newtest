package com.vikaa.lubbi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vikaa.lubbi.MainActivity;
import com.vikaa.lubbi.R;

public class HomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setListener();
    }

    private void setListener() {
        ImageView back = (ImageView) findViewById(R.id.btn_back);
        LinearLayout logout = (LinearLayout) findViewById(R.id.logout);
        //后退
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //退出登录
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
