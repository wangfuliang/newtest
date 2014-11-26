package com.vikaa.lubbi.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vikaa.lubbi.MainActivity;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.util.SP;
import com.vikaa.lubbi.util.UI;

public class HomeActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setListener();
    }

    private void setListener() {
        ImageView back = (ImageView) findViewById(R.id.btn_back);
        //后退
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.message:
                break;
            case R.id.help:
                help();
                break;
            case R.id.logout:
                logout();
                break;
        }
    }

    private void help() {
        Intent intent = new Intent(this,HelpActivity.class);
        startActivity(intent);
    }

    private void logout() {
        AlertDialog.Builder builder = UI.alert(this,"提示","确定退出登录吗?");
        builder.setIcon(android.R.drawable.ic_menu_help);
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SP.clear(getApplicationContext());
                MainActivity.isLogin = false;
                finish();
            }
        });

        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
