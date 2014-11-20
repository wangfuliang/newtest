package com.vikaa.lubbi.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.vikaa.lubbi.R;

public class CreateSignDialog extends Dialog {
    Context context;
    ImageView btnUploadImage;
    ImageView btnCloseImage;
    String hash;
    String[] images = new String[3];
    public CreateSignDialog(Context context) {
        super(context);
        this.context = context;
    }

    public CreateSignDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_sign);
        btnUploadImage = (ImageView) findViewById(R.id.btn_upload_image);
        btnCloseImage = (ImageView) findViewById(R.id.btn_close_dialog);

        btnCloseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateSignDialog.this.dismiss();
            }
        });
    }
}
