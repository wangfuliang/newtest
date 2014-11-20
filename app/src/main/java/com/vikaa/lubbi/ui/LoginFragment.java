package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.Regex;
import com.vikaa.lubbi.util.StringUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment {
    @ViewInject(R.id.btn_get_verify)
    Button btnGetVerify;
    @ViewInject(R.id.input_phone)
    EditText inputPhone;
    @ViewInject(R.id.verify_code)
    EditText verifyCode;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, null);
        ViewUtils.inject(this, view);

        set_listener();
        return view;
    }

    private void set_listener() {
        btnGetVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = inputPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getActivity(), "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Regex.isPhone(phone)) {
                    Toast.makeText(getActivity(), "手机号码格式错误", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestParams params = new RequestParams();
                params.put("phone", phone);
                Http.post(AppConfig.Api.verifyCode, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        Toast.makeText(getActivity(), "准备发送验证码", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            String info = response.getString("info");
                            if (status == 0) {
                                Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (AppConfig.debug) {
                                String code = StringUtil.getNumber(info);
                                verifyCode.setText(code);
                            }

                            Toast.makeText(getActivity(), "验证码发送成功", Toast.LENGTH_SHORT).show();
                            btnGetVerify.setEnabled(false);
                            //倒计时
                            new CountDownTimer(60000, 1000) {

                                @Override
                                public void onTick(long l) {
                                    btnGetVerify.setText(l / 1000 + "秒");
                                }

                                @Override
                                public void onFinish() {
                                    btnGetVerify.setEnabled(true);
                                    btnGetVerify.setText("获取验证码");
                                }
                            }.start();
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "发送失败,请重试", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getActivity(), "发送失败,请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
