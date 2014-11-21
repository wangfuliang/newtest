package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.ui.CreateSignActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserListRemindAdapter extends BaseAdapter {
    JSONArray list;
    private LayoutInflater inflater;

    public UserListRemindAdapter(Context context, JSONArray list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    class ViewHolder {
        TextView title;
        TextView time;
        ImageView sign;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
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
                if (js.getInt("isSigned") == 1) {
                    view = inflater.inflate(R.layout.userremind_item_active, null);
                    holder.title = (TextView) view.findViewById(R.id.title);
                    holder.time = (TextView) view.findViewById(R.id.time);
                    holder.sign = (ImageView) view.findViewById(R.id.has_signed);
                } else {
                    view = inflater.inflate(R.layout.userremind_item_normal, null);
                    holder.title = (TextView) view.findViewById(R.id.title);
                    holder.time = (TextView) view.findViewById(R.id.time);
                    holder.sign = (ImageView) view.findViewById(R.id.create_sign);
                }
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.title.setText(title);
            holder.time.setText(time);
            holder.sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if(js.getInt("isSigned") != 1){
                            //弹出intent
                            Intent i = new Intent(inflater.getContext(), CreateSignActivity.class);
                            i.putExtra("hash",hash);
                            inflater.getContext().startActivity(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    /**
     * 设置签到状态
     * @param hash
     * @param isSign
     */
    public void setSign(String hash, int isSign) {
        for(int i = 0;i<list.length();i++){
            try {
                JSONObject jo = list.getJSONObject(i);
                if(jo.getString("hash").equals(hash)){
                    jo.put("isSigned",isSign);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.notifyDataSetChanged();
    }
}
