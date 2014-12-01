package com.vikaa.lubbi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.MyApi;
import com.vikaa.lubbi.util.DateUtils;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.UI;
import com.vikaa.lubbi.widget.SwitchView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

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
    @ViewInject(R.id.datePicker)
    DatePicker datePicker;
    @ViewInject(R.id.timePicker)
    TimePicker timePicker;
    @ViewInject(R.id.datetimpicker)
    LinearLayout dateTimePicker;
    @ViewInject(R.id.create)
    ImageView create;
    String datetime;
    @ViewInject(R.id.week)
    TextView week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ViewUtils.inject(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_repeat_item, new String[]{"一次", "每天", "每周", "每月", "每年"});
        repeat.setAdapter(adapter);

        initDateTimePicker();

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.setVisibility(View.VISIBLE);
            }
        });

        mark.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                dateTimePicker.setVisibility(View.GONE);
                return false;
            }
        });
    }


    private void initDateTimePicker() {
        timePicker.setIs24HourView(true);
        Calendar calendar = Calendar.getInstance();
        Integer year = calendar.get(Calendar.YEAR);
        Integer monthOfYear = calendar.get(Calendar.MONTH);
        Integer dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Integer dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        week.setText(DateUtils.parseWeek(dayOfWeek));
        String _now = DateUtils.getCustom("yyyy-MM-dd HH:mm");
        time.setText(_now);
        UI.resizePicker(datePicker, this);
        UI.resizePicker(timePicker, this);

        datePicker.init(year, monthOfYear, dayOfMonth, new DatePicker.OnDateChangedListener() {
            public void onDateChanged(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                String hour, min;
                monthOfYear++;
                if (timePicker.getCurrentHour() > 9) {
                    hour = timePicker.getCurrentHour().toString();
                } else {
                    hour = "0" + timePicker.getCurrentHour();
                }
                if (timePicker.getCurrentMinute() > 9) {
                    min = timePicker.getCurrentMinute().toString();
                } else {
                    min = "0" + timePicker.getCurrentMinute();
                }
                String month, day;
                Integer month2 = monthOfYear;
                Integer day2 = dayOfMonth;
                if (monthOfYear > 9) {
                    month = month2.toString();
                } else {
                    month = "0" + month2.toString();
                }
                if (dayOfMonth > 9) {
                    day = day2.toString();
                } else {
                    day = "0" + day2;
                }
                String _time = hour + ":" + min;
                datetime = year + "-" + month + "-" + day + " " + _time;
                time.setText(datetime);
                long timestamp = DateUtils.dateToLong("yyyy-MM-dd HH:mm:ss", datetime + ":00");

                Calendar c = Calendar.getInstance();
                c.setTime(new Date(timestamp));
                week.setText(DateUtils.parseWeek(c.get(Calendar.DAY_OF_WEEK) - 1));
            }

        });

        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                Integer year = datePicker.getYear();
                Integer monthOfYear = datePicker.getMonth();
                Integer dayOfMonth = datePicker.getDayOfMonth();
                String hour, min;
                monthOfYear++;

                if (hourOfDay > 9) {
                    hour = ((Integer) hourOfDay).toString();
                } else {
                    hour = "0" + ((Integer) hourOfDay).toString();
                }
                if (minute > 9) {
                    min = ((Integer) minute).toString();
                } else {
                    min = "0" + ((Integer) minute).toString();
                }
                String month, day;
                if (monthOfYear > 9) {
                    month = monthOfYear.toString();
                } else {
                    month = "0" + monthOfYear.toString();
                }
                if (dayOfMonth > 9) {
                    day = dayOfMonth.toString();
                } else {
                    day = "0" + dayOfMonth;
                }
                String datetime = year + "-" + month + "-" + day + " " + hour + ":" + min;
                time.setText(datetime);
            }
        });
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
        String _time = time.getText().toString().trim() + ":00";
        int _repeat = repeat.getSelectedItemPosition();
        int _hide = hide.getSwitchStatus() ? 1 : 0;
        String _mark = mark.getText().toString().trim();
        if (check(_title, _mark)) {
            RequestParams params = new RequestParams();
            params.addBodyParameter("title", _title);
            params.addBodyParameter("time", _time);
            params.addBodyParameter("repeat", _repeat + "");
            params.addBodyParameter("hide_remind", _hide + "");
            params.addBodyParameter("mark", _mark);
            params.addQueryStringParameter("_sign", sign);
            httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.createRemind, params, new RequestCallBack<String>() {
                @Override
                public void onStart() {
                    create.setClickable(false);
                    super.onStart();
                }

                @Override
                public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                    create.setClickable(true);
                    try {
                        JSONObject resp = new JSONObject(objectResponseInfo.result);
                        if (resp.getInt("status") == 1) {
                            Toast.makeText(CreateActivity.this, "发起成功", Toast.LENGTH_SHORT).show();
                            //调到详情页
                            DetailActivity._data = resp.getJSONObject("info");
                            DetailActivity._data.put("isAdd",1);
                            Intent i = new Intent(CreateActivity.this, DetailActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(CreateActivity.this, "发起失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Logger.e(e);
                        Toast.makeText(CreateActivity.this, "发起失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    create.setClickable(true);
                    Toast.makeText(CreateActivity.this, "发起失败", Toast.LENGTH_SHORT).show();
                    Logger.e(e);
                }
            });
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
