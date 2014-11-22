package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.util.StringUtil;
import com.vikaa.lubbi.util.UI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignItemAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    JSONArray list;

    public SignItemAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        list = new JSONArray();
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
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHold {
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHold holder;
        if (view == null) {
            holder = new ViewHold();
            view = inflater.inflate(R.layout.sign_item, null);
            holder.avatar = (ImageView) view.findViewById(R.id.sign_item_avatar);
            holder.nickname = (TextView) view.findViewById(R.id.sign_item_nickname);
            holder.time = (TextView) view.findViewById(R.id.sign_item_time);
            holder.message = (TextView) view.findViewById(R.id.site_item_message);
            holder.imgList = (GridView) view.findViewById(R.id.sign_img_gridview);
            holder.comment = (ImageView) view.findViewById(R.id.sign_item_comment);
            holder.praise = (ImageView) view.findViewById(R.id.sign_item_praise);
            holder.commentList = (ListView) view.findViewById(R.id.comment_listview);
            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }
        try {
            JSONObject _this = list.getJSONObject(i);
            JSONObject _user = _this.getJSONObject("user");
            String nickname = _user.getString("nickname");
            String url = _user.getString("avatar");
            long time = _this.getLong("sign_at");
            String message = _this.getString("message");
            holder.nickname.setText(nickname);
            holder.time.setText(StringUtil.parseDate(time, true));
            holder.message.setText(message);


            try {
                //加载评论列表
                JSONArray comments = _this.getJSONArray("comments");
                CommentAdapter _adapter = new CommentAdapter(context);
                _adapter.setList(comments);
                holder.commentList.setAdapter(_adapter);
                UI.setListViewHeightBasedOnChildren(holder.commentList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //读取img
            JSONArray _imglist = new JSONArray(_this.getString("images"));
            if (_imglist.length() > 0) {
                holder.imgList.setVisibility(View.VISIBLE);
                //写入url
                String[] _list = new String[_imglist.length()];
                for (int t = 0; t < _imglist.length(); t++) {
                    _list[t] = _imglist.getString(t) + "_64.jpg";
                }
                SignItemImageAdapter _adapter = new SignItemImageAdapter(context);
                _adapter.setList(_list);
                holder.imgList.setAdapter(_adapter);
            }
            DisplayImageOptions options = new DisplayImageOptions.Builder()

                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .showImageOnLoading(R.drawable.loading)
                    .displayer(new RoundedBitmapDisplayer(5))
                    .build();
            ImageLoader.getInstance().displayImage(url, holder.avatar, options);
            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "click comment", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
