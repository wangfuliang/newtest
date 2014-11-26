package com.vikaa.lubbi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.MyApi;
import com.vikaa.lubbi.core.MyMessage;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.SP;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    static UserFragment userFragment;
    static RecommendFragment recommendFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化handler
        initHandler();
        //启动闪屏
        if (!hasFlash) {
            hasFlash = true;
            startFlash();
        } else {
            handler.sendEmptyMessage(MyMessage.START_MAIN);
        }
    }

    /**
     * 检测登录
     */
    private void checkLogin() {
        sign = (String) SP.get(getApplicationContext(), "user.sign", "");
        if (sign.length() == 0) {
            //去登录
            handler.sendEmptyMessage(MyMessage.GOTO_LOGIN);
        } else {
            //检测是否登录
            if (isLogin) {
                setPush();
                handler.sendEmptyMessage(MyMessage.START_HOME);
            }
            //HTTP请求接口
            httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.checkLogin + "?_sign=" + sign, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                    try {
                        JSONObject data = new JSONObject(objectResponseInfo.result);
                        if (data.getInt("status") == 0) {
                            Toast.makeText(MainActivity.this, "自动登录失败，请重新登录", Toast.LENGTH_SHORT).show();
                            handler.sendEmptyMessage(MyMessage.GOTO_LOGIN);
                        } else {
                            JSONObject info = data.getJSONObject("info");
                            sign = info.getString("_sign");
                            //写入缓存
                            SP.put(getApplicationContext(), "user.sign", sign);
                            //写入用户基本信息
                            SP.put(getApplicationContext(), "user.info", info.toString());
                            setPush();
                            handler.sendEmptyMessage(MyMessage.START_HOME);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "自动登录失败，请重新登录", Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(MyMessage.GOTO_LOGIN);
                        Logger.e(e);
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(MainActivity.this, "自动登录失败，请重新登录", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(MyMessage.GOTO_LOGIN);
                    Logger.e(e);
                }
            });
        }
    }

    /**
     * 启动闪屏
     */
    private void startFlash() {
        Intent intent = new Intent(this, FlashActivity.class);
        startActivity(intent);
    }

    /**
     * 初始化handler
     */
    private void initHandler() {
        handler = new CoreHandler();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create:
                handler.sendEmptyMessage(MyMessage.GOTO_CREATE);
                break;
            case R.id.menu:
                break;
            case R.id.my:
                getSupportFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .show(userFragment)
                        .hide(recommendFragment)
                        .commit();
                break;
            case R.id.recommend:
                getSupportFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .show(recommendFragment)
                        .hide(userFragment)
                        .commit();
                break;
        }
    }

    /**
     * CoreHandler
     */
    public class CoreHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyMessage.START_MAIN:
                    checkLogin();
                    break;
                case MyMessage.GOTO_LOGIN:
                    startLogin();
                    break;
                case MyMessage.SYSTEM_EXIT:
                    finish();
                    System.exit(0);
                    break;
                case MyMessage.START_HOME:
                    startMain();
                    break;
                case MyMessage.GOTO_CREATE:
                    startCreate();
                    break;
            }
        }
    }

    private void startCreate() {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    private void startLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * main初始化
     */
    private void startMain() {
        //建立缓存目录

        setListener();
        initFragment();
    }

    private void initFragment() {
        if (userFragment == null)
            userFragment = new UserFragment();
        if (recommendFragment == null)
            recommendFragment = new RecommendFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!userFragment.isAdded())
            transaction.add(R.id.container, userFragment);
        if (!recommendFragment.isAdded())
            transaction.add(R.id.container, recommendFragment);

        transaction.show(userFragment).hide(recommendFragment).commit();
    }

    private void setListener() {

    }

    private void setPush() {
        //设置推送
        if (userId != null) {
            RequestParams params = new RequestParams();
            params.addBodyParameter("push_device_type", "3");
            params.addBodyParameter("push_user_id", userId);
            httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.setPush + "?_sign=" + sign, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                    Logger.d(objectResponseInfo.result);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Logger.e(e);
                }
            });
        }
    }


    public static class UserFragment extends Fragment {
        @ViewInject(R.id.avatar)
        ImageView avatar;
        @ViewInject(R.id.nickname)
        TextView nickname;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_user, null);
            ViewUtils.inject(this, view);
            String info = SP.get(getActivity().getApplicationContext(), "user.info", "").toString();
            if (info.length() > 0) {
                try {
                    JSONObject data = new JSONObject(info);
                    String _avatar = data.getString("avatar");
                    String _nickname = data.getString("nickname");
                    bitmapUtils.display(avatar, _avatar);
                    nickname.setText(_nickname);
                } catch (JSONException e) {
                    //加载默认头像
                    bitmapUtils.display(avatar, "http://qun.hk/index/avatar?url=");
                    nickname.setText("无名--");
                    Logger.e(e);
                }
            } else {
                //加载默认头像
                bitmapUtils.display(avatar, "http://qun.hk/index/avatar?url=");
                nickname.setText("无名--");
            }

            //头像动画
            ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(500);
            scaleAnimation.setFillAfter(true);
            scaleAnimation.setInterpolator(new BounceInterpolator());
            avatar.startAnimation(scaleAnimation);
            //昵称动画
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f);
            translateAnimation.setDuration(500);
            translateAnimation.setFillAfter(true);
            translateAnimation.setInterpolator(new AccelerateInterpolator());
            nickname.startAnimation(translateAnimation);
            return view;
        }
    }

    public static class RecommendFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_recommend, null);
            return view;
        }
    }
}
