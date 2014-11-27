package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.ui.DetailActivity;

import java.util.List;

public class SignItemImageAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    String[] list;

    public SignItemImageAdapter(Context context, String[] list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public SignItemImageAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        ImageView img;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.sign_item_image, null);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DetailActivity detailActivity = (DetailActivity) context;
        detailActivity.bitmapUtils.display(holder.img, list[position]+"_w100.jpg");
        return convertView;
    }
}
