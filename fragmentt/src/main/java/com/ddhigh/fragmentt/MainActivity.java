package com.ddhigh.fragmentt;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.fragmentt.adapter.FragmentAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MainActivity extends FragmentActivity {
    @ViewInject(R.id.viewpager)
    ViewPager viewPager;
    @ViewInject(R.id.home)
    TextView home;
    @ViewInject(R.id.second)
    TextView second;
    @ViewInject(R.id.cursor)
    ImageView cursor;
    private int currentIndex;//当前页码
    private int bmpW;//横线图片宽度
    private int offset;//图片偏移量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        home.setOnClickListener(new TextListener(0));
        second.setOnClickListener(new TextListener(1));

        initImage();
        initViewPager();
    }

    private void initViewPager() {
        viewPager.setOnPageChangeListener(new MyOnPagerListener());
    }

    private void initImage() {
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 2 - bmpW) / 2;

        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);
    }

    private class TextListener implements View.OnClickListener {
        private int index;

        public TextListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }

    private class MyOnPagerListener implements ViewPager.OnPageChangeListener {
        private int one = offset * 2 + bmpW;

        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
            Animation animation = new TranslateAnimation(currentIndex * one, i * one, 0, 0);
            currentIndex = i;
            animation.setFillAfter(true);
            animation.setDuration(200);
            cursor.startAnimation(animation);
            int j = currentIndex + 1;

            Toast.makeText(MainActivity.this, "您选择了 " + j + "个标签页",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }
}
