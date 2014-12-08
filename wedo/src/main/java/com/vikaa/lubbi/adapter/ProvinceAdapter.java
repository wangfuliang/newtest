package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProvinceAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    JSONArray list;

    public JSONArray getList() {
        return list;
    }

    public ProvinceAdapter(Context context, JSONArray list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.length();
    }

    @Override
    public JSONObject getItem(int position) {
        try {
            return list.getJSONObject(position);
        } catch (JSONException e) {
            Logger.e(e);
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView text;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_oneline, null);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            JSONObject _item = list.getJSONObject(position);
            holder.text.setText(_item.getString("cityname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
