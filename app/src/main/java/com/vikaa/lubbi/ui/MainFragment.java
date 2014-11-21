package com.vikaa.lubbi.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.vikaa.lubbi.MainActivity;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.adapter.UserListRemindAdapter;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.SP;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainFragment extends Fragment {
    @ViewInject(R.id.avatar)
    ImageView avatar;
    @ViewInject(R.id.nickname)
    TextView nickname;
    @ViewInject(R.id.userlistremind)
    ListView userListRemind;
    public UserListRemindAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, null);
        ViewUtils.inject(this, v);

        userListRemind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject detail = (JSONObject) adapter.getItem(i);
                //我的列表点进去的，肯定加入了
                try {
                    detail.put("isAdd", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = AppConfig.Message.ShowRemindDetail;
                msg.obj = detail;
                ((MainActivity) getActivity()).handler.sendMessage(msg);
            }
        });

        adapter = new UserListRemindAdapter(getActivity(),new JSONArray());
        userListRemind.setAdapter(adapter);
        return v;
    }

    private void init() {
        String info = (String) SP.get(getActivity(), "user.info", "");
        try {
            JSONObject data = new JSONObject(info);
            String _avatar = data.getString("avatar");
            String _nickname = data.getString("nickname");
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new RoundedBitmapDisplayer(5))
                    .build();
            ImageLoader.getInstance().displayImage(_avatar, avatar, options);
            nickname.setText(_nickname);

            JSONArray js = new JSONArray((String) SP.get(getActivity().getApplicationContext(), "user.remindlist", "[]"));
            if (true || js.length() == 0) {
                String openid = data.getString("openid");
                RequestParams params = new RequestParams();
                params.put("openid", openid);
                Http.post(AppConfig.Api.listUserRemind + "?_sign=" + MainActivity._sign, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONArray list = response.getJSONArray("info");
                            //初始化listView
                            adapter.setList(list);
                            adapter.notifyDataSetChanged();
                            SP.put(getActivity().getApplicationContext(),"user.remindlist",list);
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "提醒列表加载失败", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getActivity(), "提醒列表加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                adapter.setList(js);
                adapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }
}
