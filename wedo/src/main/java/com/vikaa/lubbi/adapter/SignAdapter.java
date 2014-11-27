package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.http.RequestParams;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.MyMessage;
import com.vikaa.lubbi.entity.SignEntity;
import com.vikaa.lubbi.ui.DetailActivity;
import com.vikaa.lubbi.util.DateUtils;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.UI;

import java.util.Date;
import java.util.List;

public class SignAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<SignEntity> list;
    static Handler handler;


    public SignAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        handler = ((DetailActivity) context).handler;
    }


    public SignAdapter(Context context, List<SignEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        handler = ((DetailActivity) context).handler;
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
        ImageView avatar;
        TextView nickname;
        TextView time;
        TextView message;
        GridView imgList;
        ImageView comment;
        ImageView praise;
        ListView commentList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.detail_sign_item, null);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.imgList = (GridView) convertView.findViewById(R.id.images);
            holder.comment = (ImageView) convertView.findViewById(R.id.comment);
            holder.praise = (ImageView) convertView.findViewById(R.id.praise);
            holder.commentList = (ListView) convertView.findViewById(R.id.comments);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //设置值
        final SignEntity entity = list.get(position);
        //头像
        BaseActivity.bitmapUtils.display(holder.avatar, entity.getUser().getAvatar());
        //昵称
        holder.nickname.setText(entity.getUser().getNickname());
        //time
        Date date = new Date(entity.getSign_at() * 1000);
        holder.time.setText(DateUtils.getCustom("MM-dd HH:mm", date));
        //message
        holder.message.setText(entity.getMessage());
        //images
        SignItemImageAdapter _images = new SignItemImageAdapter(context, entity.getImages());
        holder.imgList.setAdapter(_images);
        holder.imgList.setOnItemClickListener(new ImageListener());
        if (_images.getCount() > 0) {
            holder.imgList.setVisibility(View.VISIBLE);
        }
        //comments
        final CommentAdapter _comments = new CommentAdapter(context, entity.getComments());
        holder.commentList.setAdapter(_comments);
        holder.commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
                Message message = new Message();
                message.what = MyMessage.REPLY_COMMENT;
                message.obj = entity.getComments().get(position1);
                handler.sendMessage(message);


                Message message1 = new Message();
                message1.what = MyMessage.SHOW_COMMENT;
                message1.obj = position;
                handler.sendMessage(message1);
            }
        });
        UI.setListViewHeightBasedOnChildren(holder.commentList);
        //comment
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                message.what = MyMessage.SHOW_COMMENT;
                message.obj = position;
                handler.sendMessage(message);
            }
        });
        if (entity.isPraised()) {
            holder.praise.setImageResource(R.drawable.icon_praise_active);
        } else {
            holder.praise.setImageResource(R.drawable.icon_praise_normal);
        }
        //praise
        holder.praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                message.what = !entity.isPraised() ? MyMessage.PRAISE_SIGN : MyMessage.CANCEL_PRAISE;
                message.obj = position;
                handler.sendMessage(message);

            }
        });
        return convertView;
    }

    private class ImageListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Logger.d("img:" + position);
        }
    }

    /**
     * 点赞
     *
     * @param position
     */
    public void praise(int position) {
        SignEntity entity = list.get(position);
        entity.setPraised(true);
        list.set(position, entity);
        notifyDataSetChanged();
    }

    /**
     * 取消赞
     *
     * @param position
     */
    public void cancelPraise(int position) {
        SignEntity entity = list.get(position);
        entity.setPraised(false);
        list.set(position, entity);
        notifyDataSetChanged();
    }

}
