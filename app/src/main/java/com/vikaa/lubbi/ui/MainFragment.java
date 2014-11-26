package com.vikaa.lubbi.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.SP;
import com.vikaa.lubbi.widget.UserRemindListView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class MainFragment extends Fragment {
    static UserRemindListViewFragment userRemindListViewFragment;
    static UserNoRemindFragment userNoRemindFragment;

    @ViewInject(R.id.avatar)
    ImageView avatar;
    @ViewInject(R.id.nickname)
    TextView nickname;
    static FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (userRemindListViewFragment == null)
            userRemindListViewFragment = new UserRemindListViewFragment();
        if (userNoRemindFragment == null)
            userNoRemindFragment = new UserNoRemindFragment();
        fragmentManager = getChildFragmentManager();
        View v = inflater.inflate(R.layout.fragment_main, null);
        ViewUtils.inject(this, v);
        UserRemindListViewFragment.adapter = new UserListRemindAdapter(getActivity(), new JSONArray());

        init();


        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.user_remind_container, userRemindListViewFragment)
                .commitAllowingStateLoss();
        //加载fragment
        return v;
    }

    private void init() {
        String info = (String) SP.get(getActivity().getApplicationContext(), "user.info", "");
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
            UserRemindListViewFragment.openid = data.getString("openid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity._sign.length() > 0) {
            init();
        }
    }


    public static class UserRemindListViewFragment extends Fragment {
        UserRemindListView list;
        static UserListRemindAdapter adapter;
        static String openid;


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

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_userlist_remind, null);
            list = (UserRemindListView) v.findViewById(R.id.userlistremind);
            list.setAdapter(adapter);
            //加载默认数据
            loadDefaultData();
            list.setonRefreshListener(new UserRemindListView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    RequestParams params = new RequestParams();
                    params.put("openid", openid);
                    Http.post(AppConfig.Api.listUserRemind + "?_sign=" + MainActivity._sign, params, new JsonHttpResponseHandler() {
                        @Override
                        public void onFinish() {
                            super.onFinish();
                            list.onRefreshComplete();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                JSONArray list = response.getJSONArray("info");
                                //初始化listView
                                adapter.setList(list);
                                adapter.notifyDataSetChanged();
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
                }
            });

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    JSONObject detail = (JSONObject) adapter.getItem(i);
                    if (detail != null) {
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
                }
            });
            return v;
        }

        private void loadDefaultData() {
            RequestParams params = new RequestParams();
            params.put("openid", openid);
            Http.post(AppConfig.Api.listUserRemind + "?_sign=" + MainActivity._sign, params, new JsonHttpResponseHandler() {
                @Override
                public void onFinish() {
                    super.onFinish();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray list = response.getJSONArray("info");
                        if (list.length() == 0) {
                            //去没有的fragment
                            //加载fragment
                            Logger.d("no data");
                            fragmentManager.beginTransaction()
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .replace(R.id.user_remind_container, userNoRemindFragment)
                                    .commitAllowingStateLoss();
                            return;
                        }
                        //初始化listView
                        adapter.setList(list);
                        adapter.notifyDataSetChanged();
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
        }
    }

    public static class UserNoRemindFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View b = inflater.inflate(R.layout.fragment_user_no_remind, null);
            return b;
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
