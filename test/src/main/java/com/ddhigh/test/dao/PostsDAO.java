package com.ddhigh.test.dao;

import com.ddhigh.library.inter.MyRequestCallback;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class PostsDAO {
    static HttpUtils httpUtils;

    public PostsDAO() {
        if (httpUtils == null)
            httpUtils = new HttpUtils();
    }

    public void getList(HttpRequest.HttpMethod method, String url, RequestParams params, final MyRequestCallback<JSONObject> callback) {
        httpUtils.send(method, url, params, new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                        callback.onStart();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        callback.onLoading(total, current, isUploading);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                        JSONObject resp = null;
                        try {
                            resp = new JSONObject(objectResponseInfo.result);
                            callback.onSuccess(resp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        callback.onFailure(e, s);
                    }
                }

        );
    }
}
