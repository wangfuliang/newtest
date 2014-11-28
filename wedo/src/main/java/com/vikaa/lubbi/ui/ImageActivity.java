package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.StringUtil;

import java.io.File;


public class ImageActivity extends BaseActivity {
    @ViewInject(R.id.show_img_img)
    ImageView img;
    @ViewInject(R.id.btn_save_img)
    ImageView save;
    @ViewInject(R.id.close_show_img)
    ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ViewUtils.inject(this);

        final String url = getIntent().getStringExtra("url") + "_w640.jpg";

        bitmapUtils.display(img, url);
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