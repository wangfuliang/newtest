package com.vikaa.lubbi.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

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

    public static void resizePicker(FrameLayout tp) {
        List<NumberPicker> npList = findNumberPicker(tp);
        for (NumberPicker np : npList) {
            resizeNumberPicker(np);
        }
    }

    private static List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }
        return npList;
    }

    private static void resizeNumberPicker(NumberPicker np) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, RadioGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        np.setLayoutParams(params);
    }


    /**
     * 设置listview高度
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}

