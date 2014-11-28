package com.vikaa.lubbi.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.MyApi;
import com.vikaa.lubbi.core.MyMessage;
import com.vikaa.lubbi.util.AppUtils;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.SP;
import com.vikaa.lubbi.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.message:
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
                break;
            case R.id.help:
                Intent intent2 = new Intent(this, BrowserActivity.class);
                startActivity(intent2);
                break;
            case R.id.feedback:
                Intent i = new Intent(this, FeedbackActivity.class);
                startActivity(i);
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.update:
                update();
                break;
        }
    }

    private void update() {
        httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.update, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                try {
                    JSONObject data = new JSONObject(objectResponseInfo.result);
                    JSONObject info = data.getJSONObject("info");
                    final JSONObject _data = info.getJSONObject("android");

                    //检测version_code
                    int localCode = AppUtils.getVersionCode(SettingActivity.this);
                    int serverCode = _data.getInt("version_code");
                    if (localCode < serverCode) {
                        //弹出更新提示
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                        builder.setIcon(R.drawable.icon_update);
                        builder.setTitle("发现新版本");
                        builder.setMessage("版本号：" + _data.getString("version_name") + "\n" + "更新时间：" + _data.getString("update_date") + "\n更新日志：\n" + StringUtil.escape(_data.getString("update_log")));
                        builder.setNegativeButton("取消", null);
                        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    final String url = _data.getString("download_url");
                                    //检测是否下载过该版本
                                    String p = BaseActivity.downPath + "/" + StringUtil.getFileName(url);
                                    File f = new File(p);
                                    if (f.exists()) {
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setDataAndType(Uri.parse("file://" + f.toString()), "application/vnd.android.package-archive");
                                        startActivity(i);
                                    } else {
                                        final ProgressDialog pd = new ProgressDialog(SettingActivity.this);
                                        pd.setTitle("自动更新");
                                        pd.setMessage("正在更新，请稍后");
                                        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        pd.setMax(100);
                                        pd.setProgress(0);
                                        final Handler handler1 = new Handler() {
                                            @Override
                                            public void handleMessage(Message msg) {
                                                switch (msg.what) {
                                                    case 1:
                                                        //正在更新
                                                        Long[] data = (Long[]) msg.obj;
                                                        long down = data[0];
                                                        long max = data[1];
                                                        //单位转换
                                                        long progress = (int) (100 * down / max);
                                                        pd.setProgress((int) progress);
                                                        break;
                                                    case 2:
                                                        pd.dismiss();
                                                        //更新完成
                                                        String path = (String) msg.obj;
                                                        File file = new File(path);
                                                        if (!file.exists())
                                                            return;
                                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                                        i.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
                                                        startActivity(i);
                                                        break;
                                                    case 3:
                                                        //更新失败
                                                        pd.dismiss();
                                                        break;
                                                    case 4:
                                                        //开始更新
                                                        pd.show();
                                                        break;
                                                }
                                                super.handleMessage(msg);
                                            }
                                        };
                                        httpUtils.download(url, BaseActivity.downPath + "/" + StringUtil.getFileName(url), false, false, new RequestCallBack<File>() {
                                            @Override
                                            public void onSuccess(ResponseInfo<File> fileResponseInfo) {
                                                String path = BaseActivity.downPath + "/" + StringUtil.getFileName(url);
                                                Message message = new Message();
                                                message.what = 2;
                                                message.obj = path;
                                                handler1.sendMessage(message);
                                            }

                                            @Override
                                            public void onFailure(HttpException e, String s) {
                                                handler1.sendEmptyMessage(3);
                                                Logger.e(e);
                                                Toast.makeText(SettingActivity.this, "下载失败,程序将不会更新", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onStart() {
                                                handler1.sendEmptyMessage(4);
                                            }

                                            @Override
                                            public void onLoading(long total, long current, boolean isUploading) {
                                                Long[] data = new Long[]{current, total};
                                                Message msg = new Message();
                                                msg.what = 1;
                                                msg.obj = data;
                                                handler1.sendMessage(msg);
                                            }
                                        });
                                    }
                                    //
                                } catch (JSONException e) {
                                    Logger.e(e);
                                    Toast.makeText(SettingActivity.this, "检查更新失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.show();
                    } else {
                        Toast.makeText(SettingActivity.this, "您当前使用的是最新版本", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Logger.e(e);
                    Toast.makeText(SettingActivity.this, "检查更新失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Logger.e(e);
                Toast.makeText(SettingActivity.this, "检查更新失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon_help);
        builder.setTitle("提示");
        builder.setMessage("确定退出登录吗?");
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SP.clear(getApplicationContext());
                sign = null;
                handler.sendEmptyMessage(MyMessage.GOTO_LOGIN);
                finish();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
