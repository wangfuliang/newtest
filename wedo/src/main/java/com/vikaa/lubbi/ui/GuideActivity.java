package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
            switch (i) {
                case 0:
                    guide1();
                    break;
            }
        }

        @Override
        public void onPageSelected(int i) {
            switch (i) {
                case 0:
                    pointImage0.setImageResource(R.drawable.dot_enable);
                    pointImage1.setImageResource(R.drawable.dot_disable);
                    pointImage2.setImageResource(R.drawable.dot_disable);
                    guide1();
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


            //设置动画
        }

        @Override
        public void onPageScrollStateChanged(int i) {
            Logger.d("page scrollstatechanged:" + i);
        }


    }

    //第一页

    ImageView g1Title;
    ImageView g1Img1;
    ImageView g1Img2;
    ImageView g1Img3;
    ImageView g1Img4;

    private void guide1() {
        g1Title = (ImageView) findViewById(R.id.g1_title);
        g1Img1 = (ImageView) findViewById(R.id.g1_img1);
        g1Img2 = (ImageView) findViewById(R.id.g1_img2);
        g1Img3 = (ImageView) findViewById(R.id.g1_img3);
        g1Img4 = (ImageView) findViewById(R.id.g1_img4);


        g1Title.setVisibility(View.INVISIBLE);
        g1Img1.setVisibility(View.INVISIBLE);
        g1Img2.setVisibility(View.INVISIBLE);
        g1Img3.setVisibility(View.INVISIBLE);
        g1Img4.setVisibility(View.INVISIBLE);

        final Animation _t = AnimationUtils.loadAnimation(this, R.anim.guide);
        _t.setAnimationListener(new Animation.AnimationListener() {
            int current = 0;

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                g1Title.setVisibility(View.VISIBLE);
                //启动计时器
                new CountDownTimer(500, 80) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        switch (current) {
                            case 0:
                                Animation a = AnimationUtils.loadAnimation(GuideActivity.this, R.anim.guide);
                                a.setDuration(800);
                                a.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        g1Img1.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                g1Img1.startAnimation(a);
                                break;
                            case 1:
                                Animation a2 = AnimationUtils.loadAnimation(GuideActivity.this, R.anim.guide);
                                a2.setDuration(800);
                                a2.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        g1Img2.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                g1Img2.startAnimation(a2);
                                break;
                            case 2:
                                Animation a3 = AnimationUtils.loadAnimation(GuideActivity.this, R.anim.guide);
                                a3.setDuration(800);
                                a3.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        g1Img3.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                g1Img3.startAnimation(a3);
                                break;
                            case 3:
                                Animation a4 = AnimationUtils.loadAnimation(GuideActivity.this, R.anim.guide);
                                a4.setDuration(800);
                                a4.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        g1Img4.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                g1Img4.startAnimation(a4);
                                break;
                        }
                        current++;
                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        g1Title.startAnimation(_t);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
