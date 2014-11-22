package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vikaa.lubbi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommentAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    JSONArray list;

    public JSONArray getList() {
        return list;
    }

    public void setList(JSONArray list) {
        this.list = list;
    }

    public CommentAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
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

    class ViewHolder {
        TextView nickname;
        TextView content;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.comment_item, null);
            holder.nickname = (TextView) view.findViewById(R.id.comment_item_nickname);
            holder.content = (TextView) view.findViewById(R.id.comment_item_content);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        try {
            JSONObject item = list.getJSONObject(i);
            String content = item.getString("message");
            JSONObject user = item.getJSONObject("user");
            String nickname = user.getString("nickname");

            holder.nickname.setText(nickname);
            holder.content.setText(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
