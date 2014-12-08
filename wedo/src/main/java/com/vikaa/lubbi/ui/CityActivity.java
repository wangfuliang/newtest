package com.vikaa.lubbi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.adapter.CityAdapter;
import com.vikaa.lubbi.adapter.ProvinceAdapter;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.BaseApplication;
import com.vikaa.lubbi.core.MyApi;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.SP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CityActivity extends BaseActivity {
    @ViewInject(R.id.city_list)
    ListView city;
    CityAdapter adapter;
    @ViewInject(R.id.back)
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        ViewUtils.inject(this);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        final Intent intent = getIntent();
        String code = intent.getStringExtra("citycode");
        RequestParams params = new RequestParams();
        params.addBodyParameter("province_id", code);

        //查询http
        httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.city, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                try {
                    JSONObject data = new JSONObject(objectResponseInfo.result);
                    if (data.getInt("status") == 0) {
                        Toast.makeText(CityActivity.this, "城市数据加载失败", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //生成JSONARRAY
                    JSONArray array = data.getJSONArray("info");
                    adapter = new CityAdapter(CityActivity.this, array);
                    city.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Logger.e(e);
            }
        });

        city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject _item = adapter.getItem(position);
                String name = null;
                try {
                    name = _item.getString("cityname");
                    Intent intent1 = new Intent();
                    intent1.putExtra("name", name);
                    setResult(RESULT_OK, intent1);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
