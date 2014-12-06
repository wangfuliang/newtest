package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tencent.weibo.sdk.android.component.sso.tools.MD5Tools;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.BaseApplication;
import com.vikaa.lubbi.core.MyApi;
import com.vikaa.lubbi.core.MyMessage;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.Regex;
import com.vikaa.lubbi.util.SP;
import com.vikaa.lubbi.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends BaseActivity {
    @ViewInject(R.id.login)
    ImageView login;
    @ViewInject(R.id.get_verify)
    Button getVerify;
    @ViewInject(R.id.phone)
    EditText editPhone;
    @ViewInject(R.id.verify)
    EditText editVerify;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        setListener();
    }

    private void setListener() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = editPhone.getText().toString().trim();
                String verify = editVerify.getText().toString().trim();
                if (checkPhone(phone) && checkVerify(verify)) {
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("phone", phone);
                    params.addBodyParameter("code", verify);
                    httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.phoneLogin, params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                            String resp = objectResponseInfo.result;
                            try {
                                JSONObject js = new JSONObject(resp);
                                if (js.getInt("status") == 0) {
                                    Toast.makeText(LoginActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                                } else {
                                    JSONObject info = js.getJSONObject("info");
                                    //写入sign
                                    String _sign = info.getString("_sign");
                                    SP.put(getApplicationContext(), "user.sign", _sign);
                                    SP.put(getApplicationContext(), "user.info", info.toString());
                                    isLogin = true;
                                    //去首页
                                    finish();
                                    handler.sendEmptyMessage(MyMessage.START_MAIN);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(LoginActivity.this, "登录失败，请重试", Toast.LENGTH_SHORT).show();
                                Logger.e(e);
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Toast.makeText(LoginActivity.this, "登录失败，请重试", Toast.LENGTH_SHORT).show();
                            Logger.e(e);
                        }
                    });
                }
            }
        });

        getVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = editPhone.getText().toString().trim();
                if (checkPhone(phone)) {
                    //发送验证码
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("phone", phone);
                    try {
                        byte[] data = (phone+"MAX无敌!!!").getBytes("UTF-8");
                        params.addBodyParameter("code", MD5Tools.toMD5(data).toLowerCase());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.verifyCode, params, new RequestCallBack<String>() {
                        @Override
                        public void onStart() {
                            Toast.makeText(LoginActivity.this, "发送验证码...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                            try {
                                JSONObject resp = new JSONObject(objectResponseInfo.result);
                                if (resp.getInt("status") == 0) {
                                    Toast.makeText(LoginActivity.this, "验证码发送失败，请重新发送", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(LoginActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                                //禁用发送
                                if (BaseApplication.debug) {
                                    String code = StringUtil.getNumber(resp.getString("info"));
                                    editVerify.setText(code);
                                    getVerify.setEnabled(false);
                                    new CountDownTimer(10000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            getVerify.setText("等待" + millisUntilFinished / 1000 + "秒");
                                        }

                                        @Override
                                        public void onFinish() {
                                            getVerify.setEnabled(true);
                                            getVerify.setText("发送验证码");
                                        }
                                    }.start();
                                } else {
                                    getVerify.setEnabled(false);
                                    new CountDownTimer(60000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            getVerify.setText("等待" + millisUntilFinished / 1000 + "秒");
                                        }

                                        @Override
                                        public void onFinish() {
                                            getVerify.setEnabled(true);
                                            getVerify.setText("发送验证码");
                                        }
                                    }.start();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(LoginActivity.this, "验证码发送失败，请重新发送", Toast.LENGTH_SHORT).show();
                                Logger.e(e);
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Toast.makeText(LoginActivity.this, "验证码发送失败，请重新发送", Toast.LENGTH_SHORT).show();
                            Logger.e(e);
                        }

                    });
                }
            }
        });
    }

    /**
     * 检测手机
     *
     * @param phone
     * @return
     */
    private boolean checkPhone(String phone) {
        if (phone.length() == 0) {
            Toast.makeText(LoginActivity.this, R.string.empty_phone, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Regex.isPhone(phone)) {
            Toast.makeText(LoginActivity.this, R.string.phone_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 检测验证码
     *
     * @param verify
     * @return
     */
    private boolean checkVerify(String verify) {
        if (verify.length() == 0) {
            Toast.makeText(LoginActivity.this, R.string.empty_verify, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                handler.sendEmptyMessage(MyMessage.SYSTEM_EXIT);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
