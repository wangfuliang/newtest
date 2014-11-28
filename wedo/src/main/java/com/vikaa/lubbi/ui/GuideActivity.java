package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.adapter.GuidePagerAdapter;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.util.Logger;

import java.util.ArrayList;

public class GuideActivity extends BaseActivity {
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;

    // 定义ViewPager适配器
    private GuidePagerAdapter vpAdapter;

    // 定义一个ArrayList来存放View
    private ArrayList<View> views;

    //定义各个界面View对象
    private View view1, view2, view3;

    // 定义底部小点图片
    private ImageView pointImage0, pointImage1, pointImage2;

    //定义开始按钮对象
    private Button startBt;

    // 当前的位置索引值
    private int currIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ViewUtils.inject(this);


        initView();
        initData();
    }

    private void initData() {
        viewPager.setOnPageChangeListener(new MyPagerChangeListener());
        viewPager.setAdapter(vpAdapter);
        views.add(view1);
        views.add(view2);
        views.add(view3);
        vpAdapter.notifyDataSetChanged();
    }

    private void initView() {
        LayoutInflater mLi = LayoutInflater.from(this);
        view1 = mLi.inflate(R.layout.fragment_guide_1, null);
        view2 = mLi.inflate(R.layout.fragment_guide_2, null);
        view3 = mLi.inflate(R.layout.fragment_guide_3, null);

        //ViewPager
        views = new ArrayList<View>();
        vpAdapter = new GuidePagerAdapter(views);
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
                    pointImage0.setImageDrawable(getResources().getDrawable(R.drawable.dot_enable));
                    pointImage1.setImageDrawable(getResources().getDrawable(R.drawable.dot_disable));
                    pointImage2.setImageDrawable(getResources().getDrawable(R.drawable.dot_disable));
                    break;
                case 1:
                    pointImage0.setImageDrawable(getResources().getDrawable(R.drawable.dot_disable));
                    pointImage1.setImageDrawable(getResources().getDrawable(R.drawable.dot_enable));
                    pointImage2.setImageDrawable(getResources().getDrawable(R.drawable.dot_disable));
                    break;
                case 2:
                    pointImage0.setImageDrawable(getResources().getDrawable(R.drawable.dot_disable));
                    pointImage1.setImageDrawable(getResources().getDrawable(R.drawable.dot_disable));
                    pointImage2.setImageDrawable(getResources().getDrawable(R.drawable.dot_enable));
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
