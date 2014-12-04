package com.ddhigh.library.inter;

import com.lidroid.xutils.exception.HttpException;

public interface MyRequestCallback<T> {
    public void onStart();

    public void onLoading(long total, long current, boolean isUploading);

    public void onSuccess(T responseInfo);

    public void onFailure(HttpException error, String msg);
}
