package com.vikaa.lubbi.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.MyMessage;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.SP;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.message:
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
                break;
            case R.id.help:
                Intent intent2 = new Intent(this, BrowserActivity.class);
                startActivity(intent2);
                break;
            case R.id.feedback:
                Intent i = new Intent(this,FeedbackActivity.class);
                startActivity(i);
                break;
            case R.id.logout:
                logout();
                break;
        }
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon_help);
        builder.setTitle("提示");
        builder.setMessage("确定退出登录吗?");
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SP.clear(getApplicationContext());
                sign = null;
                handler.sendEmptyMessage(MyMessage.GOTO_LOGIN);
                finish();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
