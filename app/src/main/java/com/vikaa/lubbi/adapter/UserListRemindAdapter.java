package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.widget.CreateSignDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserListRemindAdapter extends BaseAdapter {
    JSONArray list;
    private LayoutInflater inflater;

    public UserListRemindAdapter(Context context, JSONArray list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
        Log.d("xialei", list.toString());
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
        boolean isSign = false;
        try {
            JSONObject js = list.getJSONObject(i);
            title = js.getString("title");
            time = js.getString("format_time");
            hash = js.getString("hash");
            //判断是否打卡了
            if (view == null) {
                holder = new ViewHolder();
                if (js.getInt("isSigned") == 1) {
                    isSign = true;
                    view = inflater.inflate(R.layout.userremind_item_active, null);
                    holder.title = (TextView) view.findViewById(R.id.title);
                    holder.time = (TextView) view.findViewById(R.id.time);
                    holder.sign = (ImageView) view.findViewById(R.id.create_sign);
                } else {
                    isSign = false;
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
            if (!isSign) {
                holder.sign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //弹出dialog
                        CreateSignDialog dialog = new CreateSignDialog(inflater.getContext(), R.style.MyDialog);
                        dialog.show();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
