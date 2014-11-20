package com.vikaa.lubbi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.vikaa.lubbi.R;

public class CreateSignActivity extends Activity{
    ImageView btnUploadImage;
    ImageView btnCloseImage;
    String hash;
    String[] images = new String[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_sign);
        btnUploadImage = (ImageView) findViewById(R.id.btn_upload_image);
        btnCloseImage = (ImageView) findViewById(R.id.btn_close_dialog);

        btnCloseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //选择图片
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }
        });
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
