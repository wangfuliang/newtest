package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.entity.UserRemindEntity;
import com.vikaa.lubbi.ui.SignActivity;
import com.vikaa.lubbi.util.Logger;

import java.util.List;

public class UserRemindAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    List<UserRemindEntity> list;

    public UserRemindAdapter(Context context, List<UserRemindEntity> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public UserRemindAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);

    }

    public List<UserRemindEntity> getList() {
        return list;
    }

    public void setList(List<UserRemindEntity> list) {
        this.list = list;
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
        TextView hd;
        TextView title;
        TextView time;
        TextView sign;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.user_remind_item, null);
            holder.hd = (TextView) convertView.findViewById(R.id.hd);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.sign = (TextView) convertView.findViewById(R.id.sign);
            //设置边框
            if (position == 0)
                convertView.setBackgroundResource(R.drawable.line_border);
            else
                convertView.setBackgroundResource(R.drawable.line_bottom_border);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UserRemindEntity entity = list.get(position);

        if (entity.isSigned()) {
            holder.hd.setBackgroundResource(R.color.light_green);
            holder.sign.setText("√");
            holder.sign.setTextColor(context.getResources().getColor(R.color.my_red));
        } else {
            holder.hd.setBackgroundResource(R.color.light_blue);
            holder.sign.setText("+");
            holder.sign.setTextColor(context.getResources().getColor(R.color.deep_gray));
            holder.sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String hash = list.get(position).getHash();
                    Intent intent = new Intent(context, SignActivity.class);
                    intent.putExtra("hash", hash);
                    context.startActivity(intent);
                }
            });
        }


        return convertView;
    }
}
