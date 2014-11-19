package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;

public class MainFragment extends Fragment implements View.OnClickListener{
    @ViewInject(R.id.btn_menu)
    Button btn_menu;
    @ViewInject(R.id.btn_plus)
    Button btn_plus;
    @ViewInject(R.id.btn_my)
    Button btn_my;
    @ViewInject(R.id.btn_recommend)
    Button btn_recommend;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_main,null);
        ViewUtils.inject(this,v);
        btn_menu.setOnClickListener(this);
        btn_plus.setOnClickListener(this);
        btn_my.setOnClickListener(this);
        btn_recommend.setOnClickListener(this);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(getActivity(),view.getId()+" clicked",Toast.LENGTH_SHORT).show();
    }
}
