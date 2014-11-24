package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vikaa.lubbi.MainActivity;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.adapter.NotificationAdapter;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.entity.Notification;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.Logger;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarException;

public class NotificationFragment extends Fragment {
    @ViewInject(R.id.notification_list)
    ListView listView;
    @ViewInject(R.id.sync_notification)
    ImageView sync;
    static NotificationAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification, null);
        ViewUtils.inject(this, v);

        load();

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notification notification = adapter.getItem(position);
                String hash = notification.getHash();
                RequestParams params = new RequestParams();
                params.put("hash", hash);
                Http.post(AppConfig.Api.getRemind, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        int status = 0;
                        try {
                            status = response.getInt("status");
                            if (status == 0) {
                                Toast.makeText(getActivity(), "操作失败，请重试", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                JSONObject data = response.getJSONObject("info");
                                //发送消息
                                MainActivity mainActivity = (MainActivity) getActivity();
                                Message msg = new Message();
                                msg.what = AppConfig.Message.ShowRemindDetail;
                                msg.obj = data;
                                mainActivity.handler.sendMessage(msg);
                            }
                        } catch (JSONException e) {

                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getActivity(), "操作失败，请重试", Toast.LENGTH_SHORT).show();
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
            }
        });
        //测试数据
        return v;
    }


    private void load() {
        final RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(1500);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setFillAfter(true);
        sync.startAnimation(rotateAnimation);

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                sync.clearAnimation();
                List<Notification> list = new ArrayList<Notification>();
                Notification a = new Notification("http://www.baidu.com/img/logo.gif", "张三", 1400000000000L, 1, "呵呵呵", "2oCnNn");
                for (int i = 0; i < 10; i++)
                    list.add(a);
                adapter = new NotificationAdapter(getActivity(), list);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }.start();
    }
}
