package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.adapter.MyFragmentPagerAdapter;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.SP;

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
        viewPager.setOffscreenPageLimit(0);
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

                    guide3();
                    break;
            }
            currIndex = i;


            //设置动画
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }


    }

    LinearLayout ll;
    ImageView start;

    private void guide3() {
        ll = (LinearLayout) findViewById(R.id.btn_container);
        start = (ImageView) findViewById(R.id.start);


        AnimationSet set = new AnimationSet(true);

        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1.5f,
                Animation.RELATIVE_TO_SELF, 0f
        );

        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        set.addAnimation(translateAnimation);
        set.addAnimation(alphaAnimation);
        set.setFillAfter(true);
        set.setDuration(1500);

        ll.startAnimation(set);

        start.setOnClickListener(new Start());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class Start implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
            alphaAnimation.setInterpolator(new LinearInterpolator());
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setDuration(800);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    SP.put(getApplicationContext(), "guide", "1");
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            v.startAnimation(alphaAnimation);
        }
    }
}
