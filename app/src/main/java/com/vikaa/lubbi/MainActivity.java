package com.vikaa.lubbi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.BaseApplication;
import com.vikaa.lubbi.receiver.Utils;
import com.vikaa.lubbi.ui.CreateRemindFragment;
import com.vikaa.lubbi.ui.DetailFragment;
import com.vikaa.lubbi.ui.FlashActivity;
import com.vikaa.lubbi.ui.LoginFragment;
import com.vikaa.lubbi.ui.MainFragment;
import com.vikaa.lubbi.ui.NotificationFragment;
import com.vikaa.lubbi.ui.RecommendFragment;
import com.vikaa.lubbi.util.HardWare;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.Regex;
import com.vikaa.lubbi.util.SP;
import com.vikaa.lubbi.util.UI;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public MainFragment mainFragment;
    public CreateRemindFragment createRemindFragment;
    public DetailFragment detailFragment;
    public LoginFragment loginFragment;
    public NotificationFragment notificationFragment;
    public RecommendFragment recommendFragment;
    public static final int DATETIMEPICKER = 1;
    public static String _sign;
    ProgressDialog pd;
    public static boolean isLogin = false;
    public CoreHandler handler;
    public static String userId = "";
    private final static int FLASH = 2;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        setContentView(R.layout.activity_main);
        handler = new CoreHandler();
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(this, "api_key"));
        if (mainFragment == null)
            mainFragment = new MainFragment();
        if (createRemindFragment == null)
            createRemindFragment = new CreateRemindFragment();
        if (!BaseApplication.hashFlashed) {
            Intent intent = new Intent(this, FlashActivity.class);
            startActivityForResult(intent, FLASH);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void checkNetWork() {
        if (!HardWare.isNetworkConnected(this)) {
            AlertDialog.Builder builder = UI.alert(this, "网络设置提示", "网络连接不可用,是否进行设置?");
            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                    System.exit(0);
                }
            });
            builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //判断手机系统的版本  即API大于10 就是3.0或以上版本
                    Intent intent = null;
                    if (android.os.Build.VERSION.SDK_INT > 10) {
                        intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    } else {
                        intent = new Intent();
                        ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                        intent.setComponent(component);
                        intent.setAction("android.intent.action.VIEW");
                    }
                    startActivity(intent);
                }
            });
            builder.create().show();
        } else {
            //检测登陆
            checkLoginIn();
        }
    }

    private void checkLoginIn() {
        //检测是否登录
        if (isLogin) {
            //绑定
            if (!TextUtils.isEmpty(userId)) {
                RequestParams params = new RequestParams();
                params.put("push_device_type", 3);
                params.put("push_user_id", userId);

                Http.post(AppConfig.Api.setPush + "?_sign=" + _sign, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                    }
                });
            }


            //去通知
            Intent intent = getIntent();
            String action = intent.getStringExtra("action");
            if (action != null && action.equals("notification")) {
                //通知
                handler.sendEmptyMessage(AppConfig.Message.ShowNotification);
            } else {
                //登录成功，去主页
                //加载用户信息
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.container, mainFragment)
                        .commit();
            }
            return;
        }
        //检测SP存储有没有sign
        _sign = SP.get(this, "user.sign", "").toString();
        if (TextUtils.isEmpty(_sign)) {
            //手机号码登录
            if (loginFragment == null)
                loginFragment = new LoginFragment();
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.container, loginFragment)
                    .commit();
        } else {
            //_sign登录
            Http.post(AppConfig.Api.checkLogin + "?_sign=" + _sign, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    pd = UI.showProgress(MainActivity.this, null, "登陆中", false);
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
                        isLogin = true;
                        JSONObject info = response.getJSONObject("info");
                        //写入sign
                        _sign = info.getString("_sign");
                        SP.put(MainActivity.this, "user.sign", _sign);
                        //写入info
                        SP.put(MainActivity.this, "user.info", info);
                        //绑定
                        if (!TextUtils.isEmpty(userId)) {
                            RequestParams params = new RequestParams();
                            params.put("push_device_type", 3);
                            params.put("push_user_id", userId);
                            Http.post(AppConfig.Api.setPush + "?_sign=" + _sign, params, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);
                                }
                            });
                        }
                        //去通知
                        Intent intent = getIntent();
                        String action = intent.getStringExtra("action");
                        if (action != null && action.equals("notification")) {
                            //通知
                            handler.sendEmptyMessage(AppConfig.Message.ShowNotification);
                        } else {
                            //登录成功，去主页
                            //加载用户信息
                            fragmentManager.beginTransaction()
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .replace(R.id.container, mainFragment)
                                    .commit();
                        }
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
        switch (view.getId()) {
            case R.id.btn_create_remind1:
            case R.id.btn_create_remind2:
            case R.id.goto_create:
                switchToCreateRemindFragment();
                break;
            case R.id.btn_my1:
            case R.id.btn_back2:
            case R.id.btn_back:
            case R.id.btn_back3:
            case R.id.btn_back4:
                switchToMainFragment();
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_menu1:
            case R.id.btn_menu:
                showNotification();
                break;
            case R.id.btn_recommend:
            case R.id.goto_remind:
                switchToRecommend();
                break;
        }
    }

    private void switchToRecommend() {
        if (recommendFragment == null)
            recommendFragment = new RecommendFragment();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, recommendFragment)

                .commit();
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
                    isLogin = true;
                    JSONObject info = response.getJSONObject("info");
                    //写入sign
                    _sign = info.getString("_sign");
                    SP.put(MainActivity.this, "user.sign", _sign);
                    //写入info
                    SP.put(MainActivity.this, "user.info", info);
                    fragmentManager.beginTransaction()
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
     * 切换至主fragment
     */
    private void switchToMainFragment() {
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, mainFragment)
                .commit();
    }

    /**
     * 切换至发起界面
     */
    private void switchToCreateRemindFragment() {
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, createRemindFragment)

                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DATETIMEPICKER:
                String datetime = data.getExtras().getString("datetime");
                break;
        }
    }

    @Override
    protected void onResume() {
        checkNetWork();
        super.onResume();
    }

    public final class CoreHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.Message.ShowRemindDetail:
                    JSONObject data = (JSONObject) msg.obj;
                    showRemindDetail(data);
                    break;
                case AppConfig.Message.ShowNotification:
                    showNotification();
                    break;
            }
        }
    }

    private void showNotification() {
        if (notificationFragment == null)
            notificationFragment = new NotificationFragment();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, notificationFragment)

                .commit();

    }

    /**
     * 显示提醒详情
     *
     * @param data
     */
    private void showRemindDetail(JSONObject data) {
        if (detailFragment == null)
            detailFragment = new DetailFragment();
        detailFragment.setRemindDetail(data);
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, detailFragment)

                .commit();
    }
}
