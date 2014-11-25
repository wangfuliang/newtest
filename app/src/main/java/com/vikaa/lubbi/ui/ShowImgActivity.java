package com.vikaa.lubbi.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.StringUtil;

import org.apache.http.Header;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ShowImgActivity extends Activity {
    @ViewInject(R.id.show_img_img)
    ImageView img;
    @ViewInject(R.id.btn_save_img)
    ImageView save;
    @ViewInject(R.id.close_show_img)
    ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_img);
        ViewUtils.inject(this);

        final String url = getIntent().getStringExtra("url") + "_w640.jpg";

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.loading_big)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(url, img, options);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检测下载目录是否有本图片
                final File _file = new File(AppConfig.downPath + StringUtil.getFileName(url));
                if (_file.exists()) {
                    Toast.makeText(ShowImgActivity.this, "图片已保存到 " + AppConfig.downPath + StringUtil.getFileName(url), Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    _file.createNewFile();//不存在，下载
                    Http.get(url, new BinaryHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            try {
                                DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(_file));
                                outputStream.write(bytes);
                                outputStream.flush();
                                outputStream.close();

                                Toast.makeText(ShowImgActivity.this, "图片已保存到 " + AppConfig.downPath + StringUtil.getFileName(url), Toast.LENGTH_LONG).show();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(ShowImgActivity.this, "保存失败,请重试", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(ShowImgActivity.this, "保存失败,请重试", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(ShowImgActivity.this, "保存失败,请重试", Toast.LENGTH_SHORT).show();
                            throwable.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
