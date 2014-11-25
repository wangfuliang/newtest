package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vikaa.lubbi.MainActivity;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.adapter.RecommendAdapter;
import com.vikaa.lubbi.adapter.UserListRemindAdapter;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.widget.UserRemindListView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecommendFragment extends Fragment {
    @ViewInject(R.id.recommendlist)
    UserRemindListView recommendListView;
    public RecommendAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recommend, null);
        ViewUtils.inject(this, v);


        recommendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject detail = (JSONObject) adapter.getItem(i);
                if (detail != null) {
                    Message msg = new Message();
                    msg.what = AppConfig.Message.ShowRemindDetail;
                    msg.obj = detail;
                    ((MainActivity) getActivity()).handler.sendMessage(msg);
                }
            }
        });

        adapter = new RecommendAdapter(getActivity(), new JSONArray());
        //默认加载一次
        RequestParams params = new RequestParams();
        params.put("page", 1);
        params.put("size", 30);
        Http.post(AppConfig.Api.recommendList + "?_sign=" + MainActivity._sign, params, new JsonHttpResponseHandler() {
            @Override
            public void onFinish() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray list = response.getJSONArray("info");
                    //初始化listView
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "推荐提醒加载失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity(), "推荐提醒加载失败", Toast.LENGTH_SHORT).show();
            }
        });

        recommendListView.setonRefreshListener(new UserRemindListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RequestParams params = new RequestParams();
                params.put("page", 1);
                params.put("size", 30);
                Http.post(AppConfig.Api.recommendList + "?_sign=" + MainActivity._sign, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onFinish() {
                        recommendListView.onRefreshComplete();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONArray list = response.getJSONArray("info");
                            //初始化listView
                            adapter.setList(list);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "推荐提醒加载失败", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getActivity(), "推荐提醒加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        recommendListView.setAdapter(adapter);
        return v;
    }


}
