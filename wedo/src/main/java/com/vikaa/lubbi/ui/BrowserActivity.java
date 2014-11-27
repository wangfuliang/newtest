package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.MyApi;

public class BrowserActivity extends BaseActivity {
    @ViewInject(R.id.back)
    ImageView back;
    @ViewInject(R.id.browser)
    WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broswer);
        ViewUtils.inject(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                browser.loadUrl(url);
                return true;
            }
        });
        String url = getIntent().getStringExtra("url");
        url = url == null ? MyApi.help : url;
        browser.loadUrl(url);
    }
}
