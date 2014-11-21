package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.vikaa.lubbi.R;

import java.util.ArrayList;
import java.util.List;

public class SignItemImageAdapter extends BaseAdapter {
    private Context context;
    LayoutInflater inflater;
    List<Bitmap> list = new ArrayList<Bitmap>();

    public SignItemImageAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<Bitmap> list) {
        this.list = list;
    }

    class ViewHolder {
        ImageView img;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.gridview_img_item, null);
            holder.img = (ImageView) view.findViewById(R.id.gridview_img);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.img.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.img.setImageBitmap(list.get(i));
        holder.img.setClickable(true);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "点击了" + i, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
