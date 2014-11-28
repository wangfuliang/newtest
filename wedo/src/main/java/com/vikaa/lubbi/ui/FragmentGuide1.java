package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.util.Logger;

public class FragmentGuide1 extends Fragment {
    @ViewInject(R.id.title)
    ImageView title;
    @ViewInject(R.id.img1)
    ImageView img1;
    @ViewInject(R.id.img2)
    ImageView img2;
    @ViewInject(R.id.img3)
    ImageView img3;
    @ViewInject(R.id.img4)
    ImageView img4;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_guide_1, null);
        ViewUtils.inject(this, v);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        title.setVisibility(View.INVISIBLE);
        img1.setVisibility(View.INVISIBLE);
        img2.setVisibility(View.INVISIBLE);
        img3.setVisibility(View.INVISIBLE);
        img4.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        Animation _t = AnimationUtils.loadAnimation(getActivity(), R.anim.guide);
        _t.setAnimationListener(new Animation.AnimationListener() {
            int current = 0;

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                title.setVisibility(View.VISIBLE);
                //启动计时器
                new CountDownTimer(500, 80) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        switch (current) {
                            case 0:
                                Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.guide);
                                a.setDuration(800);
                                a.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        img1.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                img1.startAnimation(a);
                                break;
                            case 1:
                                Animation a1 = AnimationUtils.loadAnimation(getActivity(), R.anim.guide);
                                a1.setDuration(800);
                                a1.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        img2.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                img2.startAnimation(a1);
                                break;
                            case 2:
                                Animation a3 = AnimationUtils.loadAnimation(getActivity(), R.anim.guide);
                                a3.setDuration(800);
                                a3.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        img3.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                img3.startAnimation(a3);
                                break;
                            case 3:
                                Animation a4 = AnimationUtils.loadAnimation(getActivity(), R.anim.guide);
                                a4.setDuration(800);
                                a4.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        img4.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                img4.startAnimation(a4);
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

        title.startAnimation(_t);

    }
}
