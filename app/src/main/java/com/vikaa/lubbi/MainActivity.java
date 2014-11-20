package com.vikaa.lubbi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.ui.CreateRemindFragment;
import com.vikaa.lubbi.ui.LoginFragment;
import com.vikaa.lubbi.ui.MainFragment;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.SP;
import com.vikaa.lubbi.util.UI;

import org.apache.http.Header;
import org.json.JSONObject;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public MainFragment mainFragment;
    public CreateRemindFragment createRemindFragment;
    public LoginFragment loginFragment;
    private long exitTime;
    public static final int DATETIMEPICKER = 1;
    public static String _sign;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mainFragment == null)
            mainFragment = new MainFragment();
        if (createRemindFragment == null)
            createRemindFragment = new CreateRemindFragment();
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, mainFragment)
                .commit();

        //检测登陆
        checkLoginIn();
    }

    private void checkLoginIn() {
        //检测SP存储有没有sign
        _sign = SP.get(getApplicationContext(), "user.sign", "").toString();
        if (TextUtils.isEmpty(_sign)) {
            //手机号码登录
            if (loginFragment == null)
                loginFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.container, loginFragment)
                    .commit();
        } else {
            //_sign登录
            Http.post(AppConfig.Api.checkLogin, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    pd = UI.showProgress(MainActivity.this, null, "加载中", false);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("xialei", response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    //弹出dialog
                    AlertDialog.Builder alert = UI.alert(MainActivity.this, "加载失败", "不好意思，连接服务器失败了");
                    alert.setPositiveButton("重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkLoginIn();
                        }
                    });
                    alert.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    });
                    alert.create().show();
                }

                @Override
                public void onFinish() {
                    UI.dismissProgress(pd);
                }
            });
        }
    }

    public void onClick(View view) {
        Toast.makeText(this, view.getId() + " clicked", Toast.LENGTH_SHORT).show();
        switch (view.getId()) {
            case R.id.btn_plus:
                switchToCreateRemindFragment();
                break;
            case R.id.btn_back:
                switchToMainFragment();
                break;
            case R.id.btn_login:
                //提交表单
                submit_create_remind();
                break;
        }
    }

    /**
     * 提交创建提醒的表单
     */
    private void submit_create_remind() {
    }


    /**
     * 切换至主fragment
     */
    private void switchToMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, mainFragment)
                .commit();
    }

    /**
     * 切换至发起界面
     */
    private void switchToCreateRemindFragment() {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, createRemindFragment)
                .commit();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DATETIMEPICKER:
                String datetime = data.getExtras().getString("datetime");
                Toast.makeText(this, datetime, Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
