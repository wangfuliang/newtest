package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vikaa.lubbi.MainActivity;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.util.Http;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailFragment extends Fragment {
    JSONObject remindDetail;
    @ViewInject(R.id.detail_title)
    TextView detailTitle;
    @ViewInject(R.id.detail_time)
    TextView detailTime;
    @ViewInject(R.id.detail_content)
    TextView detailContent;
    @ViewInject(R.id.lj_days)
    TextView ljDays;
    @ViewInject(R.id.lx_days)
    TextView lxDays;
    @ViewInject(R.id.today_signs)
    TextView todaySigns;
    @ViewInject(R.id.joins_count)
    TextView joinsCount;

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
            String detail_content = remindDetail.getString("mark");
            if (TextUtils.isEmpty(detail_content))
                detail_content = "  这个人很懒，什么也没有写...";
            else
                detail_content = "  " + detail_content;
            detailTitle.setText(detail_title);
            detailTime.setText(detail_time);
            detailContent.setText(Html.fromHtml("<u>" + detail_content + "</u>"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //加载打卡天数
        try {
            String hash = remindDetail.getString("hash");
            RequestParams params = new RequestParams();
            params.put("hash", hash);
            Http.post(AppConfig.Api.getSignInfo + "?_sign=" + MainActivity._sign, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONObject info = response.getJSONObject("info");
                        String joins = info.getString("joins");
                        String signed = info.getString("signed");
                        String leiji = info.getString("leiji");
                        String lianxu = info.getString("lianxu");

                        joinsCount.setText(joins);
                        todaySigns.setText(signed);
                        ljDays.setText(leiji);
                        lxDays.setText(lianxu);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
