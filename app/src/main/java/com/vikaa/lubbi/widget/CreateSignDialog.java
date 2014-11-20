package com.vikaa.lubbi.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.vikaa.lubbi.R;

public class CreateSignDialog extends Dialog {
    Context context;

    public CreateSignDialog(Context context) {
        super(context);
        this.context = context;
    }

    public CreateSignDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_sign);
    }
}
