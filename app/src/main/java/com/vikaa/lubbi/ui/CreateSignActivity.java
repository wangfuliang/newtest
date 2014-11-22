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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vikaa.lubbi.MainActivity;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.adapter.CreateSignImageAdapter;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.Image;
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

public class CreateSignActivity extends Activity {
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
    CreateSignImageAdapter imgAdapter;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_sign);
        ViewUtils.inject(this);

        hash = getIntent().getStringExtra("hash");
        imgAdapter = new CreateSignImageAdapter(this);
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
                    Toast.makeText(CreateSignActivity.this, "请说点什么吧~", Toast.LENGTH_SHORT).show();
                    return;
                }
                //图片处理
                JSONArray js = new JSONArray(images);
                String imgs = js.toString();

                RequestParams params = new RequestParams();
                params.put("hash", hash);
                params.put("message", message);
                params.put("images", imgs);

                Http.post(AppConfig.Api.createSign + "?_sign=" + MainActivity._sign, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        pd = UI.showProgress(CreateSignActivity.this, null, "签到中...");
                    }

                    @Override
                    public void onFinish() {
                        UI.dismissProgress(pd);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            if (status == 1) {
                                Toast.makeText(CreateSignActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
                                SP.remove(getApplicationContext(),"user.remindlist");
                                finish();
                                //改变list的值
                            } else {
                                String info = response.getString("info");
                                Toast.makeText(CreateSignActivity.this, info, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(CreateSignActivity.this, "签到失败，请重试", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(CreateSignActivity.this, "签到失败，请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Toast.makeText(CreateSignActivity.this, "签到失败，请重试", Toast.LENGTH_SHORT).show();
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
                    Http.post(AppConfig.Api.uploadToken, new JsonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            Toast.makeText(CreateSignActivity.this, "上传图片中...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                String token = response.getString("token");
                                final String cdn = response.getString("cdn");
                                RequestParams params = new RequestParams();
                                File file = new File(picturePath);
                                params.put("file", file);
                                params.put("token", token);
                                Http.post(AppConfig.Api.uploadUrl, params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        try {
                                            Toast.makeText(CreateSignActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                                            String url = cdn + response.getString("key");
                                            //写入缩略图
                                            images.add(url);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(CreateSignActivity.this, "上传图片失败，请重试", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        Toast.makeText(CreateSignActivity.this, "上传图片失败，请重试", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        Toast.makeText(CreateSignActivity.this, "上传图片失败，请重试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //准备上传
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(CreateSignActivity.this, "图片上传失败,请重试", Toast.LENGTH_SHORT).show();
                            } catch (FileNotFoundException e) {
                                Toast.makeText(CreateSignActivity.this, "图片上传失败,请重试", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(CreateSignActivity.this, "图片上传失败,请重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
    }
}
