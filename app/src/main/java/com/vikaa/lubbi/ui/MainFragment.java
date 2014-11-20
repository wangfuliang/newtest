package com.vikaa.lubbi.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.util.Image;
import com.vikaa.lubbi.util.SP;

import org.json.JSONException;
import org.json.JSONObject;

public class MainFragment extends Fragment {
    @ViewInject(R.id.avatar)
    ImageView avatar;
    @ViewInject(R.id.nickname)
    TextView nickname;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, null);
        ViewUtils.inject(this, v);
        return v;
    }

    private void init() {
        String info = (String) SP.get(getActivity(), "user.info", "");
        try {
            JSONObject data = new JSONObject(info);
            String _avatar = data.getString("avatar");
            String _nickname = data.getString("nickname");
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new RoundedBitmapDisplayer(5))
                    .build();
            ImageLoader.getInstance().displayImage(_avatar, avatar, options);

            nickname.setText(_nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }
}
