package com.vikaa.lubbi.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.vikaa.lubbi.ui.DetailFragment;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.StringUtil;
import com.vikaa.lubbi.util.UI;

import org.apache.http.Header;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHold holder;
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
            //设置点赞图片
            final JSONObject _this = list.getJSONObject(i);
            final int isPraise = _this.getInt("isPraised");
            if (isPraise == 0) {
                holder.praise.setImageResource(R.drawable.icon_praise_normal);
            } else {
                holder.praise.setImageResource(R.drawable.icon_praise_active);
            }
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
                holder.commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //获取被评论对象
                        final JSONObject _c = (JSONObject) ((CommentAdapter) holder.commentList.getAdapter()).getItem(i);


                        final EditText inputMessage = new EditText(context);
                        inputMessage.setHint("说点什么");
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setIcon(android.R.drawable.ic_menu_set_as);
                        builder.setTitle("回复评论");
                        builder.setView(inputMessage).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.setPositiveButton("评论", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int t) {
                                final String message = inputMessage.getText().toString().trim();
                                if (TextUtils.isEmpty(message)) {
                                    Toast.makeText(context, "评论内容不能为空", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                //打卡ID
                                try {
                                    JSONObject _sign = _c;
                                    JSONObject user = _sign.getJSONObject("user");
                                    String nickname = user.getString("nickname");
                                    String sign_id = _sign.getString("sign_id");
                                    RequestParams params = new RequestParams();
                                    params.put("sign_id", sign_id);
                                    params.put("message", "@" + nickname + " " + message);
                                    Http.post(AppConfig.Api.commentSign + "?_sign=" + MainActivity._sign, params, new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            int status = 0;
                                            try {
                                                status = response.getInt("status");
                                                if (status == 1) {
                                                    //追加到commentList
                                                    JSONObject c_item = response.getJSONObject("info");
                                                    CommentAdapter _a = (CommentAdapter) holder.commentList.getAdapter();
                                                    _a.list.put(c_item);
                                                    _a.notifyDataSetChanged();
                                                    SignItemAdapter.this.notifyDataSetChanged();
                                                    UI.setListViewHeightBasedOnChildren(holder.commentList);
                                                    UI.setListViewHeightBasedOnChildren(DetailFragment.signListView);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(context, "评论失败了", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, "评论失败了", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.show();


                    }
                });
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
                    _list[t] = _imglist.getString(t);
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
                    final EditText inputMessage = new EditText(context);
                    inputMessage.setHint("说点什么");
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setIcon(android.R.drawable.ic_input_add);
                    builder.setTitle("评论");
                    builder.setView(inputMessage).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setPositiveButton("评论", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int t) {
                            final String message = inputMessage.getText().toString().trim();
                            if (TextUtils.isEmpty(message)) {
                                Toast.makeText(context, "评论内容不能为空", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //打卡ID
                            try {
                                JSONObject _sign = list.getJSONObject(i);
                                String sign_id = _sign.getString("sign_id");
                                RequestParams params = new RequestParams();
                                params.put("sign_id", sign_id);
                                params.put("message", message);
                                Http.post(AppConfig.Api.commentSign + "?_sign=" + MainActivity._sign, params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        int status = 0;
                                        try {
                                            status = response.getInt("status");
                                            if (status == 1) {
                                                //追加到commentList
                                                JSONObject c_item = response.getJSONObject("info");
                                                CommentAdapter _a = (CommentAdapter) holder.commentList.getAdapter();
                                                _a.list.put(c_item);
                                                _a.notifyDataSetChanged();
                                                SignItemAdapter.this.notifyDataSetChanged();
                                                UI.setListViewHeightBasedOnChildren(holder.commentList);
                                                UI.setListViewHeightBasedOnChildren(DetailFragment.signListView);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(context, "评论失败了", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "评论失败了", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();
                }
            });

            //点赞
            holder.praise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isPraise == 0) {
                        try {
                            String _signId = _this.getString("sign_id");
                            RequestParams params = new RequestParams();
                            params.put("sign_id", _signId);
                            Http.post(AppConfig.Api.praiseSign + "?_sign=" + MainActivity._sign, params, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        int status = response.getInt("status");
                                        if (status == 0) {
                                            String info = response.getString("info");
                                            Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        Toast.makeText(context, "点赞成功", Toast.LENGTH_SHORT).show();
                                        holder.praise.setImageResource(R.drawable.icon_praise_active);
                                    } catch (JSONException e) {
                                        Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
