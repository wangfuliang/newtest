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
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.ui.CreateRemindFragment;
import com.vikaa.lubbi.ui.LoginFragment;
import com.vikaa.lubbi.ui.MainFragment;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.Regex;
import com.vikaa.lubbi.util.SP;
import com.vikaa.lubbi.util.UI;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
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
        _sign = SP.get(this, "user.sign", "").toString();
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
            Http.post(AppConfig.Api.checkLogin+"?_sign="+_sign, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    pd = UI.showProgress(MainActivity.this, null, "加载中", false);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        int status = response.getInt("status");
                        if (status == 0) {
                            String info = response.getString("info");
                            Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONObject info = response.getJSONObject("info");
                        //写入sign
                        _sign = info.getString("_sign");
                        SP.put(MainActivity.this, "user.sign", _sign);
                        //写入info
                        SP.put(MainActivity.this, "user.info", info);
                        //登录成功，去主页
                        Toast.makeText(MainActivity.this, "自动登录成功", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.container, mainFragment)
                                .commit();
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "登录失败,请重试", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
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
            case R.id.btn_create_remind:
                //提交表单
                submit_create_remind();
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }

    private void login() {
        EditText phoneText = (EditText) findViewById(R.id.input_phone);
        EditText verifyCode = (EditText) findViewById(R.id.verify_code);
        String phone = phoneText.getText().toString().trim();
        String verify = verifyCode.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Regex.isPhone(phone)) {
            Toast.makeText(this, "手机号码格式错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(verify)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        //登录
        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("code", verify);
        Http.post(AppConfig.Api.phoneLogin, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pd = UI.showProgress(MainActivity.this, null, "登录中...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int status = response.getInt("status");
                    if (status == 0) {
                        String info = response.getString("info");
                        Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject info = response.getJSONObject("info");
                    //写入sign
                    _sign = info.getString("_sign");
                    SP.put(MainActivity.this, "user.sign", _sign);
                    //写入info
                    SP.put(MainActivity.this, "user.info", info);
                    //登录成功，去主页
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.container, mainFragment)
                            .commit();
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "登录失败,请重试", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(MainActivity.this, "登录失败,请重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                UI.dismissProgress(pd);
            }
        });

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
