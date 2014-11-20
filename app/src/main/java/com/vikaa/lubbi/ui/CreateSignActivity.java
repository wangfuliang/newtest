package com.vikaa.lubbi.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.UI;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CreateSignActivity extends Activity {
    ImageView btnUploadImage;
    ImageView btnCloseImage;
    @ViewInject(R.id.upload_image_1)
    ImageView img1;
    @ViewInject(R.id.upload_image_2)
    ImageView img2;
    @ViewInject(R.id.upload_image_3)
    ImageView img3;
    @ViewInject(R.id.create_sign_img_list)
    LinearLayout img_list;
    String hash;
    List<String> images = new ArrayList<String>();
    private final int SELECT_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_sign);
        ViewUtils.inject(this);
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
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("xialei", "req:" + requestCode);
        switch (requestCode) {
            case SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (images.size() == 3) {
                        Toast.makeText(this, "最多上传三张照片喔~", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    img_list.setVisibility(View.VISIBLE);
                    //显示在本地
                    Uri uri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    final String picturePath = cursor.getString(columnIndex);
                    Bitmap bm = BitmapFactory.decodeFile(picturePath, null);
                    if (images.size() == 0) {
                        img1.setImageBitmap(bm);
                    } else if (images.size() == 1) {
                        img2.setImageBitmap(bm);
                    } else if (images.size() == 2) {
                        img3.setImageBitmap(bm);
                    }
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
