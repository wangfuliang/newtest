package com.vikaa.lubbi.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;

/**
 * Created by hehe on 14/11/2.
 */
public class UI {
    /**
     * 信息框
     *
     * @param context
     * @param str
     */
    public static AlertDialog.Builder alert(final Context context, String title, String str) {
        if (context == null)
            return null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(title).setMessage(str);
        return builder;
    }

    // show the progress bar.

    /**
     * 显示进度条
     *
     * @param context 上下文
     * @param title   标题
     * @param message 信息
     * @return
     */
    public static ProgressDialog showProgress(final Context context,
                                              CharSequence title, CharSequence message) {
        try {
            if (context == null) {
                return null;
            }
            final ProgressDialog dialog = new ProgressDialog(context);
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setCancelable(true);
            dialog.show();
            return dialog;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 显示进度条
     *
     * @param context
     * @param title
     * @param message
     * @param cancelable
     * @return
     */
    public static ProgressDialog showProgress(Context context,
                                              CharSequence title, CharSequence message,
                                              boolean cancelable) {
        try {
            if (context == null) {
                return null;
            }
            final ProgressDialog dialog = new ProgressDialog(context);
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setCancelable(cancelable);
            new CountDownTimer(30000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {

                    }
                }
            }.start();
            dialog.show();
            return dialog;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 关闭进度条
     *
     * @param progressDialog
     * @return
     */
    public static ProgressDialog dismissProgress(ProgressDialog progressDialog) {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progressDialog;
    }
}
