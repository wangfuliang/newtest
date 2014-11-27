package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.entity.CommonEntity;

import java.util.List;

public class CommentAdapter extends BaseAdapter {
    List<CommonEntity> list;
    LayoutInflater inflater;
    Context context;

    public CommentAdapter(Context context, List<CommonEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public List<CommonEntity> getList() {
        return list;
    }

    public void setList(List<CommonEntity> list) {
        this.list = list;
    }

    public CommentAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView nickname;
        TextView message;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.comment_item, null);
            holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nickname.setText(list.get(position).getUser().getNickname());
        holder.message.setText(list.get(position).getMessage());
        return convertView;
    }
}
