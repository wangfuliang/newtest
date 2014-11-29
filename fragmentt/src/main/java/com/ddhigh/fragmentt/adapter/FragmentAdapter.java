package com.ddhigh.fragmentt.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ddhigh.fragmentt.fragments.FragmentMain;
import com.ddhigh.fragmentt.fragments.FragmentSecond;

public class FragmentAdapter extends FragmentPagerAdapter {
    static FragmentMain main;
    static FragmentSecond second;

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {
            if (main == null)
                main = new FragmentMain();
            return main;
        } else if (i == 1) {
            if (second == null)
                second = new FragmentSecond();
            return second;
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
