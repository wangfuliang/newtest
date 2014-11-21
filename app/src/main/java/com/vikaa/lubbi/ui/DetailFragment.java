package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.vikaa.lubbi.R;

import org.json.JSONObject;

public class DetailFragment extends Fragment {
    JSONObject remindDetail;

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
        Log.d("xialei",remindDetail.toString());
    }
}
