package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailFragment extends Fragment {
    JSONObject remindDetail;
    @ViewInject(R.id.detail_title)
    TextView detailTitle;
    @ViewInject(R.id.detail_time)
    TextView detailTime;

    public void setRemindDetail(JSONObject remindDetail) {
        this.remindDetail = remindDetail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, null);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置标题和时间
        try {
            String detail_title = remindDetail.getString("title");
            String detail_time = remindDetail.getString("format_time");
            detailTitle.setText(detail_title);
            detailTime.setText(detail_time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
