package com.vikaa.lubbi.inter;

import com.lidroid.xutils.exception.HttpException;

public interface MyRequestCallback<T> {
    public void onStart();

    public void onLoading(long total, long current, boolean isUploading);

    public void onSuccess(T result);

    public void onFailure(HttpException error, String msg);
}
