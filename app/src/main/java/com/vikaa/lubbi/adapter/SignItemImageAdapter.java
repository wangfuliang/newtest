package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.ui.ShowImgActivity;

public class SignItemImageAdapter extends BaseAdapter {
    private Context context;
    LayoutInflater inflater;
    String[] list;

    public SignItemImageAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setList(String[] list) {
        this.list = list;
    }

    class ViewHolder {
        ImageView img;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int i) {
        return list[i];
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
        final String url = list[i];
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.loading)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(url+"_64.jpg", holder.img,options);
        holder.img.setClickable(true);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ShowImgActivity.class);
                i.putExtra("url",url);
                context.startActivity(i);
            }
        });
        return view;
    }
}
