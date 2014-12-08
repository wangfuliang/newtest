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
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.adapter.ProvinceAdapter;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.BaseApplication;
import com.vikaa.lubbi.core.MyApi;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.SP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProvinceActivity extends BaseActivity {
    @ViewInject(R.id.locationCity)
    TextView locationCity;
    @ViewInject(R.id.province_list)
    ListView province;
    ProvinceAdapter adapter;
    @ViewInject(R.id.back)
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province);
        ViewUtils.inject(this);
        //定位
        BDLocation location = ((BaseApplication) getApplication()).bdLocation;
        if (location != null) {
            locationCity.setText(location.getCity());
        }

        //检查缓存
        String provinceCache = SP.get(getApplicationContext(), "address.province", "").toString();
        if (provinceCache.length() == 0) {
            //查询http
            httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.province, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                    try {
                        JSONObject data = new JSONObject(objectResponseInfo.result);
                        if (data.getInt("status") == 0) {
                            Toast.makeText(ProvinceActivity.this, "省份数据加载失败", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //生成JSONARRAY
                        JSONArray array = data.getJSONArray("info");
                        //写入缓存
                        SP.put(getApplicationContext(), "address.province", array.toString());
                        adapter = new ProvinceAdapter(ProvinceActivity.this, array);
                        province.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Logger.e(e);
                }
            });
        } else {
            try {
                JSONArray array = new JSONArray(provinceCache);
                adapter = new ProvinceAdapter(this, array);
                province.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        province.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject _item = adapter.getItem(position);
                if (_item != null) {
                    try {
                        //启动城市
                        Intent intent = new Intent(ProvinceActivity.this, CityActivity.class);
                        intent.putExtra("citycode", _item.getString("citycode"));
                        startActivityForResult(intent, ProfileActivity.REQUEST_CITY);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        locationCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = locationCity.getText().toString();
                Intent data = new Intent();
                data.putExtra("city", location);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ProfileActivity.REQUEST_CITY:
                if (resultCode == RESULT_OK) {
                    String city = data.getStringExtra("name");
                    Intent intent = new Intent();
                    intent.putExtra("city", city);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }
}
