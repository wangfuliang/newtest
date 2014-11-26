package com.vikaa.lubbi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.vikaa.lubbi.MainActivity;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.entity.Notification;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.Image;
import com.vikaa.lubbi.util.Logger;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    List<Notification> list;
    private float start;
    private float end;
    private Animation animation;

    public NotificationAdapter(Context context, List<Notification> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        animation = AnimationUtils.loadAnimation(context, R.anim.push_out);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Notification getItem(int position) {
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
        TextView content;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.notification_item, null);
            holder.avatar = (ImageView) convertView.findViewById(R.id.notification_item_avatar);
            holder.nickname = (TextView) convertView.findViewById(R.id.notification_item_nickname);
            holder.time = (TextView) convertView.findViewById(R.id.notification_item_time);
            holder.content = (TextView) convertView.findViewById(R.id.notification_item_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        start = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        end = event.getX();
                        break;
                }
                if (Math.abs(start - end) > 50) {
                    Logger.d("!");
                 //   deleteItem(v, position);   //删除数据，加动画
                    return true;
                }
                return false;
            }
        });


        //加载头像
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.loading)
                .displayer(new RoundedBitmapDisplayer(5))
                .build();
        ImageLoader.getInstance().displayImage(this.getItem(position).getAvatar(), holder.avatar, options);
        holder.nickname.setText(this.getItem(position).getNickname());
        holder.time.setText(this.getItem(position).getTime() + "");
        holder.content.setText(this.getItem(position).getContent());

        return convertView;
    }

    private void deleteItem(final View view, final int position) {
        view.startAnimation(animation);  //给view设置动画
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) { //动画执行完毕
                RequestParams params = new RequestParams();
                params.put("notification_id", list.get(position).getNotification_id());
                Http.post(AppConfig.Api.deleteNotification + "?_sign=" + MainActivity._sign, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        list.remove(position);
                        notifyDataSetChanged();
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
            }
        });
    }
}
