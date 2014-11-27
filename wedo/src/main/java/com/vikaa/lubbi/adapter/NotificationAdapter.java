package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.entity.NotificationEntity;
import com.vikaa.lubbi.util.DateUtils;

import java.util.Date;
import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    List<NotificationEntity> list;

    public NotificationAdapter(Context context, List<NotificationEntity> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    public List<NotificationEntity> getList() {
        return list;
    }

    public void setList(List<NotificationEntity> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public NotificationEntity getItem(int position) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.notification_item, null);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BaseActivity.bitmapUtils.display(holder.avatar, this.getItem(position).getAvatar());
        holder.nickname.setText(this.getItem(position).getNickname());
        holder.time.setText(DateUtils.getCustom("MM-dd HH:mm", new Date(this.getItem(position).getTime() * 1000)));
        holder.content.setText(this.getItem(position).getContent());

        return convertView;
    }
}
