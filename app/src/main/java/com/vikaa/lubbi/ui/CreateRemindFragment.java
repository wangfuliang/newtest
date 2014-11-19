package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.widget.SwitchView;

public class CreateRemindFragment extends Fragment {

    @ViewInject(R.id.spinner_repeat_mode)
    Spinner spinner;
    @ViewInject(R.id.hideRemind)
    SwitchView hideRemind;
    private final String[] repeatMode = {"一次", "每天", "每周", "每月", "每年"};

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_remind, null);
        ViewUtils.inject(this, v);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_repeat_mode, repeatMode);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        hideRemind.setOnSwitchChangeListener(new SwitchView.OnSwitchChangeListener() {
            @Override
            public void onSwitchChanged(boolean open) {
                Toast.makeText(getActivity(),"value:"+open,Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
}