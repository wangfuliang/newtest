package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.adapter.MyFragmentPagerAdapter;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.util.Logger;

public class GuideActivity extends BaseActivity {
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;

    // 定义ViewPager适配器
    private MyFragmentPagerAdapter vpAdapter;

    // 定义底部小点图片
    private ImageView pointImage0, pointImage1, pointImage2;


    // 当前的位置索引值
    private int currIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ViewUtils.inject(this);

        vpAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(vpAdapter);
        viewPager.setOnPageChangeListener(new MyPagerChangeListener());

        initView();
    }

    private void initView() {
        pointImage0 = (ImageView) findViewById(R.id.dot1);
        pointImage1 = (ImageView) findViewById(R.id.dot2);
        pointImage2 = (ImageView) findViewById(R.id.dot3);
    }


    private class MyPagerChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
            switch (i) {
                case 0:
                    pointImage0.setImageResource(R.drawable.dot_enable);
                    pointImage1.setImageResource(R.drawable.dot_disable);
                    pointImage2.setImageResource(R.drawable.dot_disable);
                    break;
                case 1:
                    pointImage0.setImageResource(R.drawable.dot_disable);
                    pointImage1.setImageResource(R.drawable.dot_enable);
                    pointImage2.setImageResource(R.drawable.dot_disable);
                    break;
                case 2:
                    pointImage0.setImageResource(R.drawable.dot_disable);
                    pointImage1.setImageResource(R.drawable.dot_disable);
                    pointImage2.setImageResource(R.drawable.dot_enable);
                    break;
            }
            currIndex = i;
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
