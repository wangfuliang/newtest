package com.ddhigh.application.ui;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.ddhigh.application.R;
import com.ddhigh.library.utils.Logger;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.settings);

        //获取自动更新频率，设置到外面来
        int feq = articleUpdateFeq(this);
        String _feq = getFeq(feq);

        Preference preference = findPreference("article.update_feq");
        preference.setSummary(_feq);
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String s = (String) o;
                int i = Integer.parseInt(s);
                String _feq = getFeq(i);
                preference.setSummary(_feq);
                return false;
            }
        });

    }

    private String getFeq(int feq) {
        String _feq;
        switch (feq) {
            case 600:
                _feq = "10分钟";
                break;
            case 1800:
                _feq = "30分钟";
                break;
            case 3600:
                _feq = "1小时";
                break;
            case 43200:
                _feq = "12小时";
                break;
            default:
                _feq = "请设置";
                break;
        }
        return _feq;
    }

    /**
     * 获取最新文章
     *
     * @param context 上下文
     * @return 是否获取
     */
    public static boolean articleAutoUpdate(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("article.auto_update", true);
    }

    /**
     * 文章更新频率
     *
     * @param context 上下文
     * @return 更新频率
     */
    public static int articleUpdateFeq(Context context) {
        int feq;
        try {
            feq = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("article.update_feq", "0"));
        } catch (NumberFormatException e) {
            feq = 0;
        }
        return feq;
    }

    /**
     * 程序更新
     *
     * @param context 上下文
     * @return 是否自动更新
     */
    public static boolean autoUpdate(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("article.auto_update", true);
    }
}
