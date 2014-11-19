package com.vikaa.lubbi;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.ui.MainFragment;

public class MainActivity extends BaseActivity{
    MainFragment mainFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(mainFragment == null)
            mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, mainFragment)
                .commit();
    }
}
