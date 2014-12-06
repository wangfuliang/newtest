package com.ddhigh.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ddhigh.library.inter.MyRequestCallback;
import com.ddhigh.library.utils.Logger;
import com.ddhigh.library.widget.CircleProgressBar;
import com.ddhigh.test.dao.PostsDAO;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONObject;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        final CircleProgressBar progressBar = (CircleProgressBar) findViewById(R.id.progress);
        ImageView img = (ImageView) findViewById(R.id.img);
        BitmapUtils bitmapUtils = new BitmapUtils(this);
        bitmapUtils.display(img, "http://c.hiphotos.baidu.com/image/pic/item/b90e7bec54e736d10510ec1699504fc2d56269a8.jpg", new BitmapLoadCallBack<ImageView>() {
            @Override
            public void onLoadStarted(ImageView container, String uri, BitmapDisplayConfig config) {
                super.onLoadStarted(container, uri, config);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoading(ImageView container, String uri, BitmapDisplayConfig config, long total, long current) {
                super.onLoading(container, uri, config, total, current);
                progressBar.setProgressNotInUiThread((int) (current * 100 / total));
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

    }
}
