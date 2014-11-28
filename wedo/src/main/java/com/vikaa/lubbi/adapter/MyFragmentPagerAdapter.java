package com.vikaa.lubbi.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vikaa.lubbi.ui.FragmentGuide1;
import com.vikaa.lubbi.ui.FragmentGuide2;
import com.vikaa.lubbi.ui.FragmentGuide3;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new FragmentGuide1();
            case 1:
                return new FragmentGuide2();
            case 2:
                return new FragmentGuide3();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
