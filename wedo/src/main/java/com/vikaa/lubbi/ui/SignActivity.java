package com.vikaa.lubbi.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.adapter.SignImageAdapter;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.MyApi;
import com.vikaa.lubbi.core.MyMessage;
import com.vikaa.lubbi.entity.UserRemindEntity;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.SP;
import com.vikaa.lubbi.util.UI;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class SignActivity extends BaseActivity {
    ImageView btnUploadImage;
    ImageView btnCloseImage;
    @ViewInject(R.id.create_sign_img_list)
    GridView img_list;
    @ViewInject(R.id.btn_sign)
    Button btnSign;
    @ViewInject(R.id.input_message)
    EditText inputMessage;
    String hash;
    List<String> images = new ArrayList<String>();
    private final int SELECT_IMAGE = 100;
    SignImageAdapter imgAdapter;

    public static UserRemindEntity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sign);
        ViewUtils.inject(this);

        hash = getIntent().getStringExtra("hash");
        imgAdapter = new SignImageAdapter(this);
        img_list.setAdapter(imgAdapter);
        btnUploadImage = (ImageView) findViewById(R.id.btn_upload_image);
        btnCloseImage = (ImageView) findViewById(R.id.btn_close_dialog);

        btnCloseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //选择图片
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SELECT_IMAGE);
            }
        });


        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = inputMessage.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(SignActivity.this, "请说点什么吧~", Toast.LENGTH_SHORT).show();
                    return;
                }
                //图片处理
                JSONArray js = new JSONArray(images);
                String imgs = js.toString();

                RequestParams params = new RequestParams();
                params.addBodyParameter("hash", hash);
                params.addBodyParameter("message", message);
                params.addBodyParameter("images", imgs);
                params.addQueryStringParameter("_sign", sign);
                httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.createSign, params, new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Toast.makeText(SignActivity.this, "签到中...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                        String resp = objectResponseInfo.result;
                        try {
                            JSONObject response = new JSONObject(resp);
                            if (response.getInt("status") == 0) {
                                Toast.makeText(SignActivity.this, "签到失败，请重试", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(SignActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
                            //签到成功，更新adapter
                            //获取详情
                            if (entity == null) {
                                finish();
                            } else {
                                //用户界面签到的，跳转到详情
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("hash", entity.getHash());
                                    jsonObject.put("title", entity.getTitle());
                                    jsonObject.put("time", entity.getFormatTime());
                                    jsonObject.put("mark", entity.getMark());
                                    jsonObject.put("isAdd", 1);
                                    jsonObject.put("openid", entity.getOpenid());
                                    Message msg = new Message();
                                    msg.what = MyMessage.GOTO_DETAIL;
                                    msg.obj = jsonObject;
                                    handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                finish();
                            }
                        } catch (JSONException e) {
                            Logger.e(e);
                            Toast.makeText(SignActivity.this, "签到失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(SignActivity.this, "签到失败，请重试", Toast.LENGTH_SHORT).show();
                        Logger.e(e);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_IMAGE:
                img_list.setVisibility(View.VISIBLE);
                if (resultCode == RESULT_OK) {

                    //显示在本地
                    Uri uri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    final String picturePath = cursor.getString(columnIndex);
                    Bitmap bm = BitmapFactory.decodeFile(picturePath, null);
                    imgAdapter.list.add(bm);
                    imgAdapter.notifyDataSetChanged();
                    //获取上传TOKEN
                    httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.uploadToken, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                            try {
                                JSONObject response = new JSONObject(objectResponseInfo.result);
                                String token = response.getString("token");
                                final String cdn = response.getString("cdn");
                                RequestParams params = new RequestParams();
                                File file = new File(picturePath);
                                params.addBodyParameter("file", file);
                                params.addBodyParameter("token", token);
                                httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.uploadUrl, params, new RequestCallBack<String>() {
                                    @Override
                                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                                        String _resp = objectResponseInfo.result;
                                        JSONObject response = null;
                                        try {
                                            response = new JSONObject(_resp);
                                            String url = cdn + response.getString("key");
                                            //写入缩略图
                                            images.add(url);
                                            Toast.makeText(SignActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            Logger.e(e);
                                            Toast.makeText(SignActivity.this, "上传失败，请重试", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(HttpException e, String s) {
                                        Logger.e(e);
                                        Toast.makeText(SignActivity.this, "上传失败，请重试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                Logger.e(e);
                                Toast.makeText(SignActivity.this, "上传失败，请重试", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Logger.e(e);
                            Toast.makeText(SignActivity.this, "上传失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
    }
}
