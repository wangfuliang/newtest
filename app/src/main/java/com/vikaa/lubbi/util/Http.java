package com.vikaa.lubbi.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * HTTP请求工具类
 * @author hehe
 * <uses-permission android:name="android.permission.INTERNET"/>
 */
public class Http {
	private static AsyncHttpClient client = new AsyncHttpClient();
	/**
	 * 执行GET请求
	 * 
	 * @param url
	 * @param handler
	 */
	public static void get(String url, AsyncHttpResponseHandler handler) {
		client.get(url, handler);
	}

	/**
	 * 执行带参数的GET的请求
	 * 
	 * @param url
	 * @param params
	 * @param handler
	 */
	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler handler) {
		client.get(url, params, handler);
	}

	/**
	 * 执行GET请求返回JSON
	 * @param url
	 * @param handler
	 */
	public static void get(String url, JsonHttpResponseHandler handler) {
		client.get(url, handler);
	}
	
	/**
	 * 执行带参数的GET请求返回JSON
	 * @param url
	 * @param params
	 * @param handler
	 */
	public static void get(String url,RequestParams params,JsonHttpResponseHandler handler){
		client.get(url,params,handler);
	}
	
	/**
	 * POST请求
	 * @param url
	 * @param params
	 * @param handler
	 */
	public static void post(String url,RequestParams params,AsyncHttpResponseHandler handler){
		client.post(url, params,handler);
	}
	
	/**
	 * POST请求返回JSON
	 * @param url
	 * @param params
	 * @param handler
	 */
	public static void post(String url,RequestParams params,JsonHttpResponseHandler handler){
		client.post(url,params,handler);
	}
	/**
	 * 下载
	 * @param url
	 * @param handler
	 */
	public static void get(String url,BinaryHttpResponseHandler handler){
		client.get(url, handler);
	}
	
	
	/**
	 * 获取单例句柄
	 * @return
	 */
	public static AsyncHttpClient getInstance(){
		return client;
	}

}
