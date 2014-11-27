package com.vikaa.lubbi.util;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class Animate {
    /**
     * 透明度
     *
     * @param v        控件
     * @param begin
     * @param end
     * @param duration
     */
    public static void alpha(View v, float begin, float end, long duration) {
        Animation animation = new AlphaAnimation(begin, end);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        v.startAnimation(animation);
    }


    /**
     *
     * @param v
     * @param begin
     * @param end
     * @param duration
     * @param listener
     */
    public static void alpha(View v, float begin, float end, long duration, Animation.AnimationListener listener) {
        Animation animation = new AlphaAnimation(begin, end);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        v.startAnimation(animation);
        animation.setAnimationListener(listener);
    }

    /**
     * 弹掉缩放效果
     *
     * @param v
     */
    public static void bounce(View v) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setInterpolator(new BounceInterpolator());
        v.startAnimation(scaleAnimation);
    }

    /**
     * 平移动画
     *
     * @param v
     */
    public static void translate(View v) {
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f);
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        v.startAnimation(translateAnimation);
    }

    /**
     * 旋转
     *
     * @param v
     */
    public static void rotate(View v) {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setFillAfter(true);
        v.startAnimation(rotateAnimation);
    }
}
