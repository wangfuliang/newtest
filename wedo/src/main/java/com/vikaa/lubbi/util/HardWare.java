package com.vikaa.lubbi.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * 硬件信息获取类
 * @author hehe
 * <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
 *
 */
public class HardWare {
	/**
	 * 获取android device_id
	 * @param activity
	 * @return
	 */
	public static String getDeviceId(Activity activity){
		TelephonyManager phone = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		return phone.getDeviceId();
	}

    /**
     * 检测网络是否连接
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
