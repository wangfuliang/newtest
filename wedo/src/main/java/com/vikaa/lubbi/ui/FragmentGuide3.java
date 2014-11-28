package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.util.Logger;

public class FragmentGuide3 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_guide_3, null);
        return v;
    }



    @Override
    public void onResume() {
        super.onResume();
        Logger.d("3 resume");
    }
}
