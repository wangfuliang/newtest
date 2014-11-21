package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.BinaryHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.Image;
import com.vikaa.lubbi.util.StringUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class SignItemAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    JSONArray list;

    public SignItemAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        list = new JSONArray();
    }

    public void setList(JSONArray list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return list.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHold {
        ImageView avatar;
        TextView nickname;
        TextView time;
        TextView message;
        GridView imgList;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHold holder;
        if (view == null) {
            holder = new ViewHold();
            view = inflater.inflate(R.layout.sign_item, null);
            holder.avatar = (ImageView) view.findViewById(R.id.sign_item_avatar);
            holder.nickname = (TextView) view.findViewById(R.id.sign_item_nickname);
            holder.time = (TextView) view.findViewById(R.id.sign_item_time);
            holder.message = (TextView) view.findViewById(R.id.site_item_message);
            holder.imgList = (GridView) view.findViewById(R.id.sign_img_gridview);
            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }
        try {
            JSONObject _this = list.getJSONObject(i);
            JSONObject _user = _this.getJSONObject("user");
            String nickname = _user.getString("nickname");
            String url = _user.getString("avatar");
            String time = _this.getString("sign_at");
            String message = _this.getString("message");
            holder.nickname.setText(nickname);
            holder.time.setText(time);
            holder.message.setText(message);
            //读取img
            JSONArray _imglist = new JSONArray(_this.getString("images"));
            if (_imglist.length() > 0) {
                final SignItemImageAdapter _imgAdapter = new SignItemImageAdapter(context);
                holder.imgList.setAdapter(_imgAdapter);
                for (int t = 0; t < _imglist.length(); t++) {
                   String _url = _imglist.getString(t);
                    //获取缓存文件名
                    final String fname = StringUtil.getFileName(_url);
                    //从缓存读取
                    File file = new File(AppConfig.cachePath+fname);
                    if(!file.exists()){
                        Http.get(_url, new BinaryHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                Bitmap bm = Image.getBitmapFromByte(bytes);
                                try {
                                    Image.saveFile(bm,AppConfig.cachePath+fname);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                _imgAdapter.list.add(bm);
                                _imgAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                            }
                        });
                    }else{
                        Bitmap bm = Image.getBitemapFromFile(file);
                        _imgAdapter.list.add(bm);
                        _imgAdapter.notifyDataSetChanged();
                    }
                }
                holder.imgList.setVisibility(View.VISIBLE);
            }
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new RoundedBitmapDisplayer(5))
                    .build();
            ImageLoader.getInstance().displayImage(url, holder.avatar, options);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
