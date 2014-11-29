package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.entity.RecommendEntity;

import java.util.List;

public class RecommendRemindAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    List<RecommendEntity> list;

    public RecommendRemindAdapter(Context context, List<RecommendEntity> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public RecommendRemindAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);

    }

    public List<RecommendEntity> getList() {
        return list;
    }

    public void setList(List<RecommendEntity> list) {
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
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.recommend_remind_item, null);
            holder.hd = (TextView) convertView.findViewById(R.id.hd);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            //设置边框
            if (position == 0)
                convertView.setBackgroundResource(R.drawable.line_border);
            else
                convertView.setBackgroundResource(R.drawable.line_bottom_border);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RecommendEntity entity = list.get(position);
        if (entity.isAdd()) {
            holder.hd.setBackgroundResource(R.color.light_green);
        } else {
            holder.hd.setBackgroundResource(R.color.light_blue);
        }


        holder.title.setText(list.get(position).getTitle());
        holder.time.setText(list.get(position).getFormatTime());

        return convertView;
    }
}
