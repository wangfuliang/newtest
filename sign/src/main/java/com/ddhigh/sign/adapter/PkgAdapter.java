package com.ddhigh.sign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ddhigh.sign.R;
import com.ddhigh.sign.entity.PkgEntity;

import java.util.List;

public class PkgAdapter extends BaseAdapter {
    List<PkgEntity> list;
    Context context;
    LayoutInflater inflater;

    public PkgAdapter(List<PkgEntity> list, Context context) {
        this.list = list;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<PkgEntity> getList() {
        return list;
    }

    public void setList(List<PkgEntity> list) {
        this.list = list;
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

    class ViewHolder {
        TextView pkg;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.pkg_item, null);
            holder.pkg = (TextView) view.findViewById(R.id.pkg);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.pkg.setText(list.get(i).getName());
        return view;
    }
}
