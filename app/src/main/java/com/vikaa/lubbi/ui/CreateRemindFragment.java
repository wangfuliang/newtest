package com.vikaa.lubbi.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.util.UI;
import com.vikaa.lubbi.widget.SwitchView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateRemindFragment extends Fragment {

    @ViewInject(R.id.spinner_repeat_mode)
    Spinner spinner;
    @ViewInject(R.id.hideRemind)
    SwitchView hideRemind;
    @ViewInject(R.id.input_datetime)
    TextView input_datetime;
    @ViewInject(R.id.datePicker)
    DatePicker datePicker;
    @ViewInject(R.id.timePicker)
    TimePicker timePicker;
    private final String[] repeatMode = {"一次", "每天", "每周", "每月", "每年"};

    private String datetime;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_remind, null);
        ViewUtils.inject(this, v);
        init();
        return v;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void init() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_repeat_mode, repeatMode);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        input_datetime.setText(getDatetime());
        //设置date最小值
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        UI.resizePicker(datePicker);
        UI.resizePicker(timePicker);

        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        datePicker.init(year, monthOfYear, dayOfMonth, new DatePicker.OnDateChangedListener() {

            public void onDateChanged(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                //得到时间
                String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                datetime = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + time;
                input_datetime.setText(datetime);
            }

        });

        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
                datetime = date + " " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                input_datetime.setText(datetime);
            }

        });

    }


    private String getDatetime() {
        SimpleDateFormat dg = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dg.format(new Date());
    }
}
