package com.vikaa.lubbi.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.adapter.RecommendRemindAdapter;
import com.vikaa.lubbi.adapter.UserRemindAdapter;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.MyApi;
import com.vikaa.lubbi.core.MyMessage;
import com.vikaa.lubbi.entity.RecommendEntity;
import com.vikaa.lubbi.entity.UserRemindEntity;
import com.vikaa.lubbi.util.Animate;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.SP;
import com.vikaa.lubbi.widget.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    static UserFragment userFragment;
    static RecommendFragment recommendFragment;
    @ViewInject(R.id.my)
    Button _my;
    @ViewInject(R.id.recommend)
    Button _remind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        //初始化handler
        initHandler();
        //启动闪屏
        if (!hasFlash) {
            hasFlash = true;
            startFlash();
        } else {
            handler.sendEmptyMessage(MyMessage.START_MAIN);
        }
    }

    /**
     * 检测登录
     */
    private void checkLogin() {
        sign = (String) SP.get(getApplicationContext(), "user.sign", "");
        if (sign.length() == 0) {
            //去登录
            handler.sendEmptyMessage(MyMessage.GOTO_LOGIN);
        } else {
            //检测是否登录
            if (isLogin) {
                setPush();
                handler.sendEmptyMessage(MyMessage.START_HOME);
            }
            //HTTP请求接口
            RequestParams params = new RequestParams();
            params.addQueryStringParameter("_sign", sign);
            httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.checkLogin, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                    try {
                        JSONObject data = new JSONObject(objectResponseInfo.result);
                        if (data.getInt("status") == 0) {
                            Toast.makeText(MainActivity.this, "自动登录失败，请重新登录", Toast.LENGTH_SHORT).show();
                            handler.sendEmptyMessage(MyMessage.GOTO_LOGIN);
                        } else {
                            JSONObject info = data.getJSONObject("info");
                            sign = info.getString("_sign");
                            //写入缓存
                            SP.put(getApplicationContext(), "user.sign", sign);
                            //写入用户基本信息
                            SP.put(getApplicationContext(), "user.info", info.toString());
                            setPush();
                            handler.sendEmptyMessage(MyMessage.START_HOME);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "自动登录失败，请重新登录", Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(MyMessage.GOTO_LOGIN);
                        Logger.e(e);
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(MainActivity.this, "自动登录失败，请重新登录", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(MyMessage.GOTO_LOGIN);
                    Logger.e(e);
                }
            });
        }
    }

    /**
     * 启动闪屏
     */
    private void startFlash() {
        Intent intent = new Intent(this, FlashActivity.class);
        startActivity(intent);
    }

    /**
     * 初始化handler
     */
    private void initHandler() {
        handler = new CoreHandler();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create:
                handler.sendEmptyMessage(MyMessage.GOTO_CREATE);
                break;
            case R.id.menu:
                startMenu();
                break;
            case R.id.my:
                startMy();
                break;
            case R.id.recommend:
                startRecommend();
                break;
        }
    }

    private void startMenu() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    private void startMy() {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .show(userFragment)
                .hide(recommendFragment)
                .commit();
        _my.setBackgroundResource(R.drawable.corner_left);
        _remind.setBackgroundResource(R.drawable.corner_right);
        _my.setTextColor(getResources().getColor(R.color.white));
        _remind.setTextColor(getResources().getColor(R.color.light_blue));
    }

    /**
     * CoreHandler
     */
    public class CoreHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyMessage.START_MAIN:
                    checkLogin();
                    break;
                case MyMessage.GOTO_LOGIN:
                    startLogin();
                    break;
                case MyMessage.SYSTEM_EXIT:
                    finish();
                    System.exit(0);
                    break;
                case MyMessage.START_HOME:
                    startMain();
                    break;
                case MyMessage.GOTO_CREATE:
                    startCreate();
                    break;
                case MyMessage.GOTO_RECOMMEND:
                    startRecommend();
                    break;
                case MyMessage.GOTO_DETAIL:
                    JSONObject detail = (JSONObject) msg.obj;
                    startDetail(detail);
                    break;
            }
        }
    }

    private void startDetail(JSONObject detail) {
        DetailActivity._data = detail;
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }

    private void startRecommend() {
        if (userFragment == null)
            userFragment = new UserFragment();
        if (recommendFragment == null)
            recommendFragment = new RecommendFragment();
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .show(recommendFragment)
                .hide(userFragment)
                .commit();
        _my.setBackgroundResource(R.drawable.corner_left_normal);
        _remind.setBackgroundResource(R.drawable.corner_right_active);
        _my.setTextColor(getResources().getColor(R.color.light_blue));
        _remind.setTextColor(getResources().getColor(R.color.white));
    }

    private void startCreate() {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    private void startLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * main初始化
     */
    private void startMain() {
        //检测是否有通知
        Intent intent = getIntent();
        if (intent.getStringExtra("action") != null && intent.getStringExtra("action").equals("notification")) {
            Intent intent1 = new Intent(this, NotificationActivity.class);
            startActivity(intent1);
        }
        setListener();
        initFragment();
    }

    private void initFragment() {
        if (userFragment == null)
            userFragment = new UserFragment();
        if (recommendFragment == null)
            recommendFragment = new RecommendFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!userFragment.isAdded())
            transaction.add(R.id.container, userFragment);
        if (!recommendFragment.isAdded())
            transaction.add(R.id.container, recommendFragment);

        transaction.show(userFragment).hide(recommendFragment).commit();
    }

    private void setListener() {

    }

    private void setPush() {
        //设置推送
        if (userId != null) {
            RequestParams params = new RequestParams();
            params.addBodyParameter("push_device_type", "3");
            params.addBodyParameter("push_user_id", userId);
            params.addQueryStringParameter("_sign", sign);
            httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.setPush, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                    Logger.d(objectResponseInfo.result);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Logger.e(e);
                }
            });
        }
    }


    public static class UserFragment extends Fragment {
        @ViewInject(R.id.avatar)
        ImageView avatar;
        @ViewInject(R.id.nickname)
        TextView nickname;

        static HasFragment hasFragment;
        static NoFragment noFragment;
        static FragmentManager fragmentManager;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_user, null);
            ViewUtils.inject(this, view);
            String info = SP.get(getActivity().getApplicationContext(), "user.info", "").toString();
            if (info.length() > 0) {
                try {
                    JSONObject data = new JSONObject(info);
                    String _avatar = data.getString("avatar");
                    String _nickname = data.getString("nickname");
                    bitmapUtils.display(avatar, _avatar);
                    nickname.setText(_nickname);
                } catch (JSONException e) {
                    //加载默认头像
                    bitmapUtils.display(avatar, "http://qun.hk/index/avatar?url=");
                    nickname.setText("无名--");
                    Logger.e(e);
                }
            } else {
                //加载默认头像
                bitmapUtils.display(avatar, "http://qun.hk/index/avatar?url=");
                nickname.setText("无名--");
            }

            if (hasFragment == null)
                hasFragment = new HasFragment();
            if (noFragment == null)
                noFragment = new NoFragment();
            fragmentManager = getChildFragmentManager();
            addFragment();
            return view;
        }

        private void addFragment() {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!hasFragment.isAdded())
                transaction.add(R.id.container, hasFragment);
            if (!noFragment.isAdded())
                transaction.add(R.id.container, noFragment);
            transaction.hide(hasFragment)
                    .hide(noFragment)
                    .commit();
        }

        private void switchToHas() {
            fragmentManager.beginTransaction()
                    .show(hasFragment)
                    .hide(noFragment)
                    .commit();
        }

        private void switchToNo() {
            fragmentManager.beginTransaction()
                    .show(noFragment)
                    .hide(hasFragment)
                    .commit();
        }

        @Override
        public void onResume() {
            super.onResume();

            //头像动画
            Animate.bounce(avatar);
            //昵称动画

            Animate.translate(nickname);

            //检测有没有提醒
            RequestParams params = new RequestParams();
            params.addQueryStringParameter("_sign", sign);
            httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.listUserRemind, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                    try {
                        JSONObject data = new JSONObject(objectResponseInfo.result);
                        if (data.getInt("status") == 0) {
                            switchToNo();
                            return;
                        }

                        JSONArray _list = data.getJSONArray("info");
                        if (_list.length() > 0) {
                            switchToHas();
                        } else {
                            switchToNo();
                        }
                    } catch (JSONException e) {
                        switchToNo();
                        Logger.e(e);
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    //加载默认
                    Logger.e(e);
                    switchToNo();
                }
            });
        }

        @Override
        public void onPause() {
            super.onPause();
            avatar.setVisibility(View.INVISIBLE);
            nickname.setVisibility(View.INVISIBLE);
        }

        //设置内部fragment
        public static class HasFragment extends Fragment {
            @ViewInject(R.id.listview)
            MyListView listView;
            List<UserRemindEntity> list;
            UserRemindAdapter adapter;

            @Override
            public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_user_has, null);
                ViewUtils.inject(this, view);
                //将jsonArray转化成entity
                list = new ArrayList<UserRemindEntity>();
                //设置adapter
                adapter = new UserRemindAdapter(getActivity(), list);
                listView.setonRefreshListener(new MyListView.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        load();
                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        UserRemindEntity entity = list.get(position - 1);
                        //启动detail
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("hash", entity.getHash());
                            jsonObject.put("title", entity.getTitle());
                            jsonObject.put("time", entity.getFormatTime());
                            jsonObject.put("mark", entity.getMark());
                            jsonObject.put("isAdd", 1);
                            Message msg = new Message();
                            msg.what = MyMessage.GOTO_DETAIL;
                            msg.obj = jsonObject;
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                listView.setAdapter(adapter);
                this.registerForContextMenu(listView);
                return view;
            }

            @Override
            public void onResume() {
                super.onResume();
                //请求数据
                load();
            }

            private void load() {
                RequestParams params = new RequestParams();
                params.addQueryStringParameter("_sign", sign);
                httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.listUserRemind, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                        listView.onRefreshComplete();
                        try {
                            JSONObject data = new JSONObject(objectResponseInfo.result);
                            if (data.getInt("status") == 0) {
                                return;
                            }
                            JSONArray _list = data.getJSONArray("info");
                            if (_list.length() > 0) {
                                list.clear();
                                for (int i = 0; i < _list.length(); i++) {
                                    try {
                                        JSONObject _t = _list.getJSONObject(i);
                                        UserRemindEntity entity = new UserRemindEntity(_t);
                                        list.add(entity);
                                        adapter.setList(list);
                                        adapter.notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        Logger.e(e);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            Logger.e(e);
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        //加载默认
                        Logger.e(e);
                    }
                });
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo menuInfo2;
                menuInfo2 = (AdapterView.AdapterContextMenuInfo) menuInfo;
                int p = --menuInfo2.position;
                //查看是否管理员
                UserRemindEntity entity = list.get(p);
                if (entity.isAdmin()) {
                    menu.add(0, 1, 0, "编辑");
                    menu.add(0, 2, 0, "删除");
                } else {
                    menu.add(0, 3, 0, "退出");
                }
                menu.setHeaderTitle("[" + entity.getTitle() + "]");

            }

            @Override
            public boolean onContextItemSelected(MenuItem item) {
                final AdapterView.AdapterContextMenuInfo menuInfo;
                menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                //获取操作项
                //获取菜单
                int c = item.getItemId();
                final UserRemindEntity entity = list.get(menuInfo.position);
                if (c == 3) {
                    //退出
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("确定退出吗?");
                    builder.setTitle("提示");
                    builder.setIcon(android.R.drawable.ic_menu_info_details);
                    builder.setNegativeButton("取消", null);
                    builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //请求HTTP接口实现退出
                            RequestParams params = new RequestParams();
                            params.addQueryStringParameter("_sign", sign);
                            params.addQueryStringParameter("hash", entity.getHash());
                            httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.deleteJoin, params, new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                                    try {
                                        JSONObject data = new JSONObject(objectResponseInfo.result);
                                        if (data.getInt("status") == 1) {
                                            adapter.getList().remove(menuInfo.position);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(getActivity(), "退出失败", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        Logger.e(e);
                                        Toast.makeText(getActivity(), "退出失败", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    Logger.e(e);
                                    Toast.makeText(getActivity(), "退出失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    builder.show();
                }
                if (c == 2) {
                    //删除
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("确定删除?");
                    builder.setTitle("提示");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setNegativeButton("取消", null);
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //请求HTTP接口实现退出
                            RequestParams params = new RequestParams();
                            params.addQueryStringParameter("_sign", sign);
                            params.addBodyParameter("hash", entity.getHash());
                            httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.deleteRemind, params, new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                                    try {
                                        JSONObject data = new JSONObject(objectResponseInfo.result);
                                        if (data.getInt("status") == 1) {
                                            adapter.getList().remove(menuInfo.position);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(getActivity(), data.getString("info"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        Logger.e(e);
                                        Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    Logger.e(e);
                                    Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    builder.show();
                }
                if (c == 1) {
                    //编辑
                    EditActivity.entity = entity;
                    Intent intent = new Intent(getActivity(), EditActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        }

        public static class NoFragment extends Fragment {
            @ViewInject(R.id.create)
            Button create;
            @ViewInject(R.id.recommend)
            Button recommend;
            MainActivity mainActivity;
            @ViewInject(R.id.user_no)
            LinearLayout user_no;

            @Override
            public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_user_no, null);
                ViewUtils.inject(this, view);

                mainActivity = (MainActivity) getActivity();
                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.sendEmptyMessage(MyMessage.GOTO_CREATE);
                    }
                });

                recommend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.sendEmptyMessage(MyMessage.GOTO_RECOMMEND);
                    }
                });

                //动画
                Animate.rotate(user_no);
                return view;
            }
        }
    }

    public static class RecommendFragment extends Fragment {
        @ViewInject(R.id.listview)
        MyListView listView;
        List<RecommendEntity> list;
        RecommendRemindAdapter adapter;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_recommend, null);
            ViewUtils.inject(this, view);

            list = new ArrayList<RecommendEntity>();
            adapter = new RecommendRemindAdapter(getActivity(), list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new RecommendDetailListener());
            listView.setonRefreshListener(new MyListView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    load();
                }
            });
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            load();
        }

        private void load() {
            RequestParams params = new RequestParams();
            params.addQueryStringParameter("page", "1");
            params.addQueryStringParameter("size", "30");
            params.addQueryStringParameter("_sign", sign);
            httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.recommendList, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                    listView.onRefreshComplete();

                    try {
                        JSONObject data = new JSONObject(objectResponseInfo.result);
                        if (data.getInt("status") == 0) {
                            return;
                        }
                        JSONArray _list = data.getJSONArray("info");
                        if (_list.length() > 0) {
                            list.clear();
                            for (int i = 0; i < _list.length(); i++) {
                                try {
                                    JSONObject _t = _list.getJSONObject(i);
                                    RecommendEntity entity = new RecommendEntity(_t);
                                    list.add(entity);
                                    adapter.setList(list);
                                    adapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    Logger.e(e);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Logger.e(e);
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Logger.e(e);
                    Toast.makeText(getActivity(), "推荐提醒加载失败", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private class RecommendDetailListener implements AdapterView.OnItemClickListener {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position--;
                RecommendEntity entity = list.get(position);
                //启动detail
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("hash", entity.getHash());
                    jsonObject.put("title", entity.getTitle());
                    jsonObject.put("time", entity.getFormatTime());
                    jsonObject.put("mark", entity.getMark());
                    jsonObject.put("isAdd", entity.isAdd() ? 1 : 0);
                    Message msg = new Message();
                    msg.what = MyMessage.GOTO_DETAIL;
                    msg.obj = jsonObject;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
