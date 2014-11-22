package com.vikaa.lubbi.adapter;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vikaa.lubbi.MainActivity;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.ui.CreateSignActivity;
import com.vikaa.lubbi.ui.UpdateRemindFragment;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.Image;
import com.vikaa.lubbi.util.UI;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserListRemindAdapter extends BaseAdapter {
    JSONArray list;
    private LayoutInflater inflater;
    Context context;

    public UserListRemindAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public UserListRemindAdapter(Context context, JSONArray list) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public JSONArray getList() {
        return list;
    }

    class ViewHolder {
        ImageView hd;
        TextView title;
        TextView time;
        TextView sign;
        TextView delete;
        ImageView edit;
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
            return list.get(i - 1);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
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
                view = inflater.inflate(R.layout.userremind_item, null);
                holder.title = (TextView) view.findViewById(R.id.title);
                holder.time = (TextView) view.findViewById(R.id.time);
                holder.sign = (TextView) view.findViewById(R.id.create_sign);
                holder.hd = (ImageView) view.findViewById(R.id.remind_item_hd);
                holder.delete = (TextView) view.findViewById(R.id.delete_remind);
                holder.edit = (ImageView) view.findViewById(R.id.edit_remind);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (js.getInt("isSigned") == 1) {
                //签到了
                holder.hd.setBackgroundColor(Color.rgb(102, 204, 0));
                holder.sign.setText("√");
                holder.sign.setTextColor(Color.rgb(200, 102, 102));
            } else {
                //没签到
                holder.sign.setText("+");
                holder.sign.setTextColor(Color.rgb(50, 50, 50));
                holder.hd.setBackgroundColor(Color.rgb(102, 204, 204));
            }
            holder.title.setText(title);
            holder.time.setText(time);
            holder.sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (js.getInt("isSigned") != 1) {
                            //弹出intent
                            Intent i = new Intent(inflater.getContext(), CreateSignActivity.class);
                            i.putExtra("hash", hash);
                            inflater.getContext().startActivity(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("提示");
                    builder.setMessage("确定删除吗?");
                    builder.setIcon(android.R.drawable.ic_delete);
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int t) {
                            RequestParams params = new RequestParams();
                            params.put("hash", hash);
                            Http.post(AppConfig.Api.deleteRemind + "?_sign=" + MainActivity._sign, params, new JsonHttpResponseHandler() {
                                @TargetApi(Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        int status = response.getInt("status");
                                        if (status == 0) {
                                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        JSONArray _new = new JSONArray();
                                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                        for (int _t = 0; _t < list.length(); _t++) {
                                            JSONObject o = list.getJSONObject(_t);
                                            if (!o.getString("hash").equals(hash)) {
                                                _new.put(o);
                                            }
                                        }
                                        UserListRemindAdapter.this.setList(_new);
                                        UserListRemindAdapter.this.notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                }
            });

            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UpdateRemindFragment updateRemindFragment = new UpdateRemindFragment();
                    updateRemindFragment.setData(js);
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.getSupportFragmentManager().beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.container, updateRemindFragment)
                            .commit();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
