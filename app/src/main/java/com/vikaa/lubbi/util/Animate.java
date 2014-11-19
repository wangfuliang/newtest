package com.vikaa.lubbi.util;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class Animate {
    /**
     * 透明度
     * @param v 控件
     * @param begin
     * @param end
     * @param duration
     */
    public static void alpha(View v,float begin,float end,long duration){
        Animation animation = new AlphaAnimation(begin,end);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        v.startAnimation(animation);
    }
}
