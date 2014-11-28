package com.vikaa.lubbi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.MyMessage;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.SP;

public class FlashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        //渐显
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //渐隐
                AlphaAnimation alphaAnimation1 = new AlphaAnimation(1f, 0.8f);
                alphaAnimation1.setDuration(500);
                alphaAnimation1.setFillAfter(true);
                alphaAnimation1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //检测是否需要引导
                        String isNew = SP.get(getApplicationContext(), "guide", "").toString();
                        if (isNew.length() == 0) {
                            Intent intent = new Intent(FlashActivity.this, GuideActivity.class);
                            startActivityForResult(intent, 0x100);
                        } else {
                            finish();
                            //发送handler消息
                            handler.sendEmptyMessage(MyMessage.START_MAIN);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                findViewById(R.id.screen_flash).startAnimation(alphaAnimation1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        findViewById(R.id.screen_flash).startAnimation(alphaAnimation);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x100 && resultCode == RESULT_OK) {
            SP.put(getApplicationContext(), "guide", "1");
            finish();
            handler.sendEmptyMessage(MyMessage.START_MAIN);
        }
    }
}
