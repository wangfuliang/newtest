package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.widget.SwitchView;

import java.util.HashMap;
import java.util.Map;

public class CreateActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.repeat)
    Spinner repeat;
    @ViewInject(R.id.title)
    EditText title;
    @ViewInject(R.id.time)
    TextView time;
    @ViewInject(R.id.hide)
    SwitchView hide;
    @ViewInject(R.id.mark)
    EditText mark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ViewUtils.inject(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_repeat_item, new String[]{"一次", "每天", "每周", "每月", "每年"});
        repeat.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.create:
                create();
                break;
        }
    }

    private void create() {
        String _title = title.getText().toString().trim();
        String _time = "2014-10-20 10:00:00";
        int _repeat = repeat.getSelectedItemPosition();
        int _hide = hide.getSwitchStatus() ? 1 : 0;
        String _mark = mark.getText().toString().trim();
        if (check(_title, _mark)) {
            Toast.makeText(this, new StringBuilder().append("\n")
                    .append("_title:")
                    .append(_title)
                    .append("\n_time:")
                    .append(_time)
                    .append("\nrepeat:")
                    .append(_repeat)
                    .append("\nhide:")
                    .append(_hide)
                    .append("\nmark:")
                    .append(_mark).toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean check(String title, String mark) {
        if (title.length() == 0) {
            Toast.makeText(this, "请输入提醒标题", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mark.length() == 0) {
            Toast.makeText(this, "请输入提醒描述", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

}
