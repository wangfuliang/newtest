package com.vikaa.lubbi.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vikaa.lubbi.MainActivity;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.UI;
import com.vikaa.lubbi.widget.SwitchView;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateRemindFragment extends Fragment {

    MainActivity mainActivity;
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
    @ViewInject(R.id.btn_update_remind)
    ImageView btnUpdate;
    ProgressDialog pd;
    private final String[] repeatMode = {"一次", "每天", "每周", "每月", "每年"};

    //form
    @ViewInject(R.id.input_title)
    EditText inputTitle;
    @ViewInject(R.id.input_datetime)
    TextView inputDatetime;
    private String datetime;
    String repeatModeString;
    @ViewInject(R.id.mark)
    EditText inputMark;

    JSONObject data;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_remind, null);
        ViewUtils.inject(this, v);
        init();
        return v;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    private void init() {
        //设置参数
        try {
            String title = data.getString("title");
            String time = data.getString("time");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date;
            try {
                date = df.parse(time);
            } catch (ParseException e) {
                date = new Date();
            }
            long timestamp = date.getTime();
            int repeat = data.getInt("repeat");

            boolean hide = data.getInt("hide_remind") != 0;
            String _mark = data.getString("mark");

            //设置值
            inputTitle.setText(title);
            inputMark.setText(_mark);
            hideRemind.setSwitchStatus(hide);
            input_datetime.setText(time);


            mainActivity = (MainActivity) getActivity();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_repeat_mode, repeatMode);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(repeat);
            Date d = new Date(timestamp);
            //设置date最小值
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            Integer year = calendar.get(Calendar.YEAR);
            Integer monthOfYear = calendar.get(Calendar.MONTH);
            Integer dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            UI.resizePicker(datePicker);
            UI.resizePicker(timePicker);

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
                    String time = hour + ":" + min;
                    datetime = year + "-" + month + "-" + day + " " + time;
                    input_datetime.setText(datetime);
                }

            });

            timePicker.setIs24HourView(true);
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
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
                    input_datetime.setText(datetime);
                }

            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submit();
                }
            });
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    repeatModeString = "" + i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    repeatModeString = "0";
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void submit() {
        try {
            String hash = data.getString("hash");
            String title = inputTitle.getText().toString().trim();
            String datetime = input_datetime.getText().toString();
            String hide_remind = hideRemind.getSwitchStatus() ? "1" : "0";
            String mark = inputMark.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(getActivity(), "请输入提醒名称", Toast.LENGTH_SHORT).show();
                inputTitle.setFocusable(true);
                return;
            }
            //发送HTTP
            RequestParams params = new RequestParams();
            params.put("hash", hash);
            params.put("title", title);
            params.put("time", datetime);
            params.put("repeat", repeatModeString);
            params.put("hide_remind", hide_remind);
            params.put("mark", mark);
            Http.post(AppConfig.Api.updateRemind+"?_sign="+MainActivity._sign, params, new JsonHttpResponseHandler() {
                @Override
                public void onFinish() {
                    UI.dismissProgress(pd);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        int status = response.getInt("status");
                        if (status == 0) {
                            String info = response.getString("info");
                            Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONObject data = response.getJSONObject("info");
                        data.put("isAdd", 1);
                        Message msg = new Message();
                        msg.what = AppConfig.Message.ShowRemindDetail;
                        msg.obj = data;
                        mainActivity.handler.sendMessage(msg);
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "编辑失败,请重试", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getActivity(), "编辑失败,请重试", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStart() {
                    pd = UI.showProgress(getActivity(), null, "正在编辑...");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private String getDatetime() {
        SimpleDateFormat dg = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dg.format(new Date());
    }


    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}
