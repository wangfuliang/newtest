package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.util.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.d("xialei", "!");
        ViewHold holder;
        if (view == null) {
            holder = new ViewHold();
            view = inflater.inflate(R.layout.sign_item, null);
            holder.avatar = (ImageView) view.findViewById(R.id.sign_item_avatar);
            holder.nickname = (TextView) view.findViewById(R.id.sign_item_nickname);
            holder.time = (TextView) view.findViewById(R.id.sign_item_time);
            holder.message = (TextView) view.findViewById(R.id.site_item_message);
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

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
