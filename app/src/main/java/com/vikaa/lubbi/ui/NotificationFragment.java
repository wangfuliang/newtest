package com.vikaa.lubbi.ui;

import android.os.Bundle;
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

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

        Http.post(AppConfig.Api.getUserNotification + "?_sign=" + MainActivity._sign, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                sync.startAnimation(rotateAnimation);
                super.onStart();
            }

            @Override
            public void onFinish() {
                sync.clearAnimation();
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray info = response.getJSONArray("info");
                    if (info.length() == 0){
                        Toast.makeText(getActivity(), "暂时没有通知", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int notification_id;
                    String avatar;
                    String nickname;
                    long time;
                    int type;
                    String content;
                    String hash;
                    List<Notification> list = new ArrayList<Notification>();
                    for (int i = 0; i < info.length(); i++) {
                        JSONObject j = info.getJSONObject(i);
                        notification_id = j.getInt("notification_id");
                        avatar = j.getString("avatar");
                        nickname = j.getString("nickname");
                        time = j.getLong("time");
                        type = j.getInt("type");
                        content = j.getString("content");
                        hash = j.getString("hash");
                        Notification a = new Notification(notification_id, avatar, nickname, time, type, content, hash);
                        list.add(a);
                    }
                    adapter = new NotificationAdapter(getActivity(), list);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity(), "获取通知失败,请刷新重试", Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}
