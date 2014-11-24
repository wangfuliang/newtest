package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.entity.Notification;
import com.vikaa.lubbi.util.Image;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    List<Notification> list;

    public NotificationAdapter(Context context, List<Notification> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Notification getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        ImageView avatar;
        TextView nickname;
        TextView time;
        TextView content;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.notification_item, null);
            holder.avatar = (ImageView) convertView.findViewById(R.id.notification_item_avatar);
            holder.nickname = (TextView) convertView.findViewById(R.id.notification_item_nickname);
            holder.time = (TextView) convertView.findViewById(R.id.notification_item_time);
            holder.content = (TextView) convertView.findViewById(R.id.notification_item_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //加载头像
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.loading)
                .displayer(new RoundedBitmapDisplayer(5))
                .build();
        ImageLoader.getInstance().displayImage(this.getItem(position).getAvatar(), holder.avatar, options);
        holder.nickname.setText(this.getItem(position).getNickname());
        holder.time.setText(this.getItem(position).getTime() + "");
        holder.content.setText(this.getItem(position).getContent());

        return convertView;
    }
}
