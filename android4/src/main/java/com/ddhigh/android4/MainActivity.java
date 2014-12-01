package com.ddhigh.android4;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.library.utils.Logger;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.File;

public class MainActivity extends Activity {
    @ViewInject(R.id.progressBar)
    ProgressBar p;
    @ViewInject(R.id.button)
    Button button;
    @ViewInject(R.id.speed)
    TextView speed;
    static HttpUtils httpUtils;
    int time = 0;
    long bytes = 0;
    boolean downloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        httpUtils = new HttpUtils();
        p.setMax(100);
        p.setProgress(0);


        button.setOnClickListener(new BeginDownload());
    }

    private class BeginDownload implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new Thread() {
                @Override
                public void run() {
                    while (!downloaded) {
                        time++;
                        try {
                            sleep(1000);
                            Logger.d("time:"+time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
            httpUtils.download("http://big.pc6.com/dx/Android-kaifaheji-PDF.rar", "/sdcard/Download/pdf.rar", true, true, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> fileResponseInfo) {
                    Toast.makeText(MainActivity.this, "下载完成，文件路径\n" + fileResponseInfo.result.getPath(), Toast.LENGTH_SHORT).show();
                    button.setEnabled(true);
                    downloaded = true;
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    long percent = current * 100 / total;
                    p.setProgress((int) percent);
                    button.setEnabled(false);
                    bytes += current;

                    if (time > 0) {
                        long _speed = bytes / time;
                        _speed = _speed/1024;
                        speed.setText(_speed/time+"KB/s");
                        Logger.d("time:"+time+" download:"+_speed+"KB");
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Logger.e(e);
                    Toast.makeText(MainActivity.this, "下载失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    button.setEnabled(true);
                }
            });
        }
    }
}
