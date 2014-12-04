package com.ddhigh.test;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ddhigh.library.inter.MyRequestCallback;
import com.ddhigh.library.utils.Logger;
import com.ddhigh.test.dao.PostsDAO;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONObject;

public class MainActivity extends Activity {
    @ViewInject(R.id.button)
    Button button;
    @ViewInject(R.id.loading)
    ImageView loading;
    AnimationDrawable ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);

        ad = (AnimationDrawable) loading.getDrawable();
        stopLoading();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoading();
                new CountDownTimer(3000, 1000) {

                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        stopLoading();
                    }
                }.start();
            }
        });

    }

    private void startLoading() {
        loading.setVisibility(View.VISIBLE);
        if (ad.isRunning())
            ad.stop();
        ad.start();
    }

    private void stopLoading() {
        if (ad.isRunning())
            ad.stop();
        loading.setVisibility(View.GONE);
    }
}
