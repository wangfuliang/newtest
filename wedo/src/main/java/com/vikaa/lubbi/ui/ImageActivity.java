package com.vikaa.lubbi.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.util.Image;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.StringUtil;
import com.vikaa.lubbi.widget.CircleProgressBar;

import java.io.File;


public class ImageActivity extends BaseActivity {
    @ViewInject(R.id.show_img_img)
    ImageView img;
    @ViewInject(R.id.save)
    TextView save;
    @ViewInject(R.id.close)
    TextView close;
    @ViewInject(R.id.progress_bar)
    CircleProgressBar progressBar;
    @ViewInject(R.id.toolbar)
    RelativeLayout toolbar;
    boolean view = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ViewUtils.inject(this);

        final String url = getIntent().getStringExtra("url") + "_w640.jpg";
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!view){
                    Animation animation = AnimationUtils.loadAnimation(ImageActivity.this,R.anim.toolbar);
                    animation.setFillAfter(true);
                    toolbar.startAnimation(animation);
                    view = true;
                }
            }
        });
        bitmapUtils.display(img, url, new BitmapLoadCallBack<ImageView>() {
            @Override
            public void onLoadStarted(ImageView container, String uri, BitmapDisplayConfig config) {
                super.onLoadStarted(container, uri, config);
                //显示转圈圈
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoading(ImageView container, String uri, BitmapDisplayConfig config, long total, long current) {
                super.onLoading(container, uri, config, total, current);
                //显示加载中的转圈圈
                long p = current * 100 / total;
                progressBar.setProgressNotInUiThread((int) p);
            }

            @Override
            public void onLoadCompleted(ImageView imageView, String s, Bitmap bitmap, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {
                progressBar.setVisibility(View.GONE);
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadFailed(ImageView imageView, String s, Drawable drawable) {

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检测下载目录是否有本图片
                final File _file = new File(downPath + "/" + StringUtil.getFileName(url));
                if (_file.exists()) {
                    Toast.makeText(ImageActivity.this, "图片已保存到 " + _file.getPath(), Toast.LENGTH_LONG).show();
                    return;
                }


                //不存在，下载
                httpUtils.download(url, _file.getPath(), true, true, new RequestCallBack<File>() {
                    @Override
                    public void onSuccess(ResponseInfo<File> fileResponseInfo) {

                        Toast.makeText(ImageActivity.this, "图片已保存到 " + fileResponseInfo.result.getPath(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Logger.e(e);
                        Toast.makeText(ImageActivity.this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
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
