package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.adapter.NotificationAdapter;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.MyApi;
import com.vikaa.lubbi.core.MyMessage;
import com.vikaa.lubbi.entity.NotificationEntity;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.SP;
import com.vikaa.lubbi.widget.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends BaseActivity {
    @ViewInject(R.id.back)
    ImageView back;
    @ViewInject(R.id.listview)
    MyListView listView;
    @ViewInject(R.id.trash)
    ImageView delete;
    List<NotificationEntity> list;
    static NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ViewUtils.inject(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.addQueryStringParameter("_sign", sign);

                httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.clearNotification, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                        try {
                            JSONObject data = new JSONObject(objectResponseInfo.result);
                            if (data.getInt("status") == 0) {
                                Toast.makeText(NotificationActivity.this, "清空消息失败", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            adapter.getList().clear();
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Logger.e(e);
                            Toast.makeText(NotificationActivity.this, "清空消息失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
            }
        });

        list = new ArrayList<NotificationEntity>();
        adapter = new NotificationAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setonRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position--;
                NotificationEntity notificationEntity = list.get(position);
                String hash = notificationEntity.getHash();
                RequestParams params = new RequestParams();
                params.addBodyParameter("hash", hash);
                params.addQueryStringParameter("_sign", sign);

                httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.getRemind, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                        try {
                            JSONObject jsonObject = new JSONObject(objectResponseInfo.result);
                            if (jsonObject.getInt("status") == 0) {
                                Toast.makeText(NotificationActivity.this, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            JSONObject info = jsonObject.getJSONObject("info");
                            JSONObject data = new JSONObject();
                            data.put("hash", info.getString("hash"));
                            data.put("title", info.getString("title"));
                            data.put("time", info.getString("time"));
                            data.put("mark", info.getString("mark"));
                            data.put("isAdd", info.getInt("isAdd"));

                            String userInfo = SP.get(getApplicationContext(), "user.info", "").toString();
                            JSONObject j = new JSONObject(userInfo);
                            data.put("openid", j.getString("openid"));
                            Message message = new Message();
                            message.what = MyMessage.GOTO_DETAIL;
                            message.obj = data;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            Logger.e(e);
                            Toast.makeText(NotificationActivity.this, "获取详情失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Logger.e(e);
                        Toast.makeText(NotificationActivity.this, "获取详情失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                position--;
                final NotificationEntity notificationEntity = list.get(position);
                //删除该项
                Animation animation = AnimationUtils.loadAnimation(NotificationActivity.this, R.anim.push_out);
                final int finalPosition = position;
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        RequestParams params = new RequestParams();
                        params.addQueryStringParameter("_sign", sign);
                        params.addBodyParameter("notification_id", notificationEntity.getNotification_id() + "");

                        httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.deleteNotification, params, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                                Logger.d(objectResponseInfo.result);
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        adapter.getList().remove(finalPosition);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                view.startAnimation(animation);
                return true;
            }
        });
        load();
    }

    private void load() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("_sign", sign);
        httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.getUserNotification, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                listView.onRefreshComplete();
                try {
                    JSONObject jsonObject = new JSONObject(objectResponseInfo.result);
                    if (jsonObject.getInt("status") == 0) {
                        Toast.makeText(NotificationActivity.this, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONArray array = jsonObject.getJSONArray("info");
                    if (array.length() == 0)
                        return;

                    list.clear();
                    for (int t = 0; t < array.length(); t++) {
                        JSONObject j = array.getJSONObject(t);
                        int id = j.getInt("notification_id");
                        String avatar = j.getString("avatar");
                        String nickname = j.getString("nickname");
                        long time = j.getLong("time");
                        int type = j.getInt("type");
                        String message = j.getString("content");
                        String hash = j.getString("hash");
                        NotificationEntity entity = new NotificationEntity(id, avatar, nickname, time, type, message, hash);
                        list.add(entity);
                    }
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Logger.e(e);
                    Toast.makeText(NotificationActivity.this, "获取消息失败,请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Logger.e(e);
                Toast.makeText(NotificationActivity.this, "获取消息失败,请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
