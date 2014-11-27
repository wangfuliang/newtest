package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.vikaa.lubbi.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.message)
    EditText message;
    @ViewInject(R.id.submit)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ViewUtils.inject(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _message = message.getText().toString().trim();
                if (_message.length() == 0) {
                    Toast.makeText(FeedbackActivity.this, "请输入反馈内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestParams params = new RequestParams();
                params.addBodyParameter("message", _message);
                params.addQueryStringParameter("_sign", sign);

                httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.feedBack, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                        try {
                            JSONObject data = new JSONObject(objectResponseInfo.result);
                            if (data.getInt("status") == 0) {
                                Toast.makeText(FeedbackActivity.this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(FeedbackActivity.this, "提交成功，谢谢您的反馈!", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (JSONException e) {
                            Logger.e(e);
                            Toast.makeText(FeedbackActivity.this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Logger.e(e);
                        Toast.makeText(FeedbackActivity.this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
