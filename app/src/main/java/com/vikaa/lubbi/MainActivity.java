package com.vikaa.lubbi;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.ui.CreateRemindFragment;
import com.vikaa.lubbi.ui.MainFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public MainFragment mainFragment;
    public CreateRemindFragment createRemindFragment;
    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mainFragment == null)
            mainFragment = new MainFragment();
        if (createRemindFragment == null)
            createRemindFragment = new CreateRemindFragment();
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, mainFragment)
                .commit();
    }

    public void onClick(View view) {
        Toast.makeText(this, view.getId() + " clicked", Toast.LENGTH_SHORT).show();
        switch (view.getId()) {
            case R.id.btn_plus:
                switchToCreateRemindFragment();
                break;
            case R.id.btn_back:
                switchToMainFragment();
                break;
        }
    }

    /**
     * 切换至主fragment
     */
    private void switchToMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, mainFragment)
                .commit();
    }

    /**
     * 切换至发起界面
     */
    private void switchToCreateRemindFragment() {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, createRemindFragment)
                .commit();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
