package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecommendAdapter extends BaseAdapter {
    JSONArray list;
    private LayoutInflater inflater;
    Context context;

    public RecommendAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public RecommendAdapter(Context context, JSONArray list) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public JSONArray getList() {
        return list;
    }

    class ViewHolder {
        ImageView hd;
        TextView title;
        TextView time;
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
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        String title;
        String time;
        final String hash;
        try {
            final JSONObject js = list.getJSONObject(i);
            title = js.getString("title");
            time = js.getString("format_time");
            hash = js.getString("hash");
            //判断是否打卡了
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.recommend_item, null);
                if (i == 0)
                    view.setBackgroundResource(R.drawable.line_border);
                else
                    view.setBackgroundResource(R.drawable.line_bottom_border);
                holder.title = (TextView) view.findViewById(R.id.title);
                holder.time = (TextView) view.findViewById(R.id.time);
                holder.hd = (ImageView) view.findViewById(R.id.remind_item_hd);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.title.setText(title);
            holder.time.setText(time);
            JSONObject o = (JSONObject) this.getItem(i);
            if(!o.isNull("isAdd")){
                int isAdd = o.getInt("isAdd");
                if (isAdd == 1) {
                    holder.hd.setBackgroundColor(Color.rgb(102, 204, 0));
                } else {
                    holder.hd.setBackgroundColor(Color.rgb(102, 204, 204));
                }
            }else{
                Logger.d("is null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
