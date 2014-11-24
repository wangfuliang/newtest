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
import android.widget.ImageView;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.adapter.NotificationAdapter;
import com.vikaa.lubbi.entity.Notification;

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
                Notification a = new Notification("http://www.baidu.com/img/logo.gif", "张三", 1400000000000L, 1, "呵呵呵");
                list.add(a);
                adapter = new NotificationAdapter(getActivity(), list);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }.start();
    }
}
