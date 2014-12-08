package com.vikaa.lubbi.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.BaseApplication;
import com.vikaa.lubbi.core.MyApi;
import com.vikaa.lubbi.util.Image;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.SP;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.avatar)
    ImageView avatar;
    @ViewInject(R.id.nickname)
    EditText nickname;
    @ViewInject(R.id.address)
    EditText address;
    @ViewInject(R.id.button)
    Button button;
    private LocationClient mLocationClient;
    private LocationClientOption.LocationMode tempMode = LocationClientOption.LocationMode.Hight_Accuracy;

    String _avatar;
    String _nickname;
    String _address;
    String _sex;
    String _openid;
    public final static int REQUEST_PROVINCE = 0x20;
    public final static int REQUEST_CITY = 0x21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ViewUtils.inject(this);
        String info = SP.get(getApplicationContext(), "user.info", "").toString();
        try {
            JSONObject jsonObject = new JSONObject(info);
            _avatar = jsonObject.getString("avatar");
            _nickname = jsonObject.getString("nickname");
            _address = jsonObject.getString("address").equals("null") ? "" : jsonObject.getString("address");
            _sex = jsonObject.getString("sex");
            _openid = jsonObject.getString("openid");
            //设置初值
            bitmapUtils.display(avatar, _avatar);
            nickname.setText(_nickname);
            address.setText(_address);
            //设置监听器
            button.setOnClickListener(new SubmitListener());
            avatar.setOnClickListener(new AvatarListener());
        } catch (JSONException e) {
            Logger.e(e);
        }

        //定位SDK
        mLocationClient = ((BaseApplication) getApplication()).mLocationClient;
        InitLocation();
        mLocationClient.requestLocation();
        BDLocation location = ((BaseApplication) getApplication()).bdLocation;
        if (location != null && address.getText().toString().trim().length() == 0) {
            address.setText(location.getCity());
            mLocationClient.stop();
        }

        //设置监听
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProvinceActivity.class);
                startActivityForResult(intent, REQUEST_PROVINCE);
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        }
    }

    private class AvatarListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //
            Image.PhotoChooseOption(ProfileActivity.this);
        }
    }

    private class SubmitListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            _nickname = nickname.getText().toString().trim();
            _address = address.getText().toString().trim();

            if (_nickname.length() == 0) {
                Toast.makeText(ProfileActivity.this, "请输入昵称", Toast.LENGTH_SHORT).show();
                return;
            } else if (_address.length() == 0) {
                Toast.makeText(ProfileActivity.this, "请输入您的地址", Toast.LENGTH_SHORT).show();
                return;
            } else {
                RequestParams params = new RequestParams();
                params.addQueryStringParameter("_sign", sign);
                params.addBodyParameter("avatar", _avatar);
                params.addBodyParameter("nickname", _nickname);
                params.addBodyParameter("address", _address);

                httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.editUser, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                        try {
                            JSONObject data = new JSONObject(objectResponseInfo.result);
                            if (data.getInt("status") == 0) {
                                Toast.makeText(ProfileActivity.this, data.getString("info"), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Toast.makeText(ProfileActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                            //更新缓存
                            JSONObject j = new JSONObject();
                            j.put("sex", _sex);
                            j.put("openid", _openid);
                            j.put("avatar", _avatar);
                            j.put("address", _address);
                            j.put("nickname", _nickname);
                            SP.put(getApplicationContext(), "user.info", j);

                            finish();
                        } catch (JSONException e) {
                            Toast.makeText(ProfileActivity.this, "不好意思，更新失败了...", Toast.LENGTH_SHORT).show();
                            Logger.e(e);
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(ProfileActivity.this, "不好意思，更新失败了...", Toast.LENGTH_SHORT).show();
                        Logger.e(e);
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch (requestCode) {
            case Image.REQUEST_CODE_GETIMAGE_BYCAMERA:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bmp = (Bitmap) bundle.get("data");
                    avatar.setImageBitmap(bmp);
                    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                    String fileName = timeStamp + ".jpg";//照片命名
                    //上传头像
                    final String path = getSDPath() + Image.DCIM + fileName;
                    try {
                        Image.saveFile(bmp, path);

                        //获取上传token

                        httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.uploadToken, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                                try {
                                    JSONObject response = new JSONObject(objectResponseInfo.result);
                                    String token = response.getString("token");
                                    final String cdn = response.getString("cdn");
                                    RequestParams params = new RequestParams();
                                    File file = new File(path);
                                    params.addBodyParameter("file", file);
                                    params.addBodyParameter("token", token);
                                    httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.uploadUrl, params, new RequestCallBack<String>() {
                                        @Override
                                        public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                                            String _resp = objectResponseInfo.result;
                                            JSONObject response = null;
                                            try {
                                                response = new JSONObject(_resp);
                                                _avatar = cdn + response.getString("key");
                                                //写入缩略图
                                                Toast.makeText(ProfileActivity.this, "头像上传成功", Toast.LENGTH_SHORT).show();
                                            } catch (JSONException e) {
                                                Logger.e(e);
                                                Toast.makeText(ProfileActivity.this, "头像上传失败，请重试", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(HttpException e, String s) {
                                            Logger.e(e);
                                            Toast.makeText(ProfileActivity.this, "头像上传失败，请重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (JSONException e) {
                                    Logger.e(e);
                                    Toast.makeText(ProfileActivity.this, "头像上传失败，请重试", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                Logger.e(e);
                                Toast.makeText(ProfileActivity.this, "头像上传失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        });

                        /////

                    } catch (IOException e) {
                        Logger.e(e);
                        Toast.makeText(ProfileActivity.this, "上传头像失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case Image.REQUEST_CODE_GETIMAGE_BYSDCARD:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    final String picturePath = cursor.getString(columnIndex);
                    bitmapUtils.display(avatar, picturePath);


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
                                    public void onStart() {
                                        super.onStart();
                                        button.setEnabled(false);
                                    }

                                    @Override
                                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                                        button.setEnabled(true);
                                        String _resp = objectResponseInfo.result;
                                        JSONObject response = null;
                                        try {
                                            response = new JSONObject(_resp);
                                            _avatar = cdn + response.getString("key");
                                            //写入缩略图
                                            Toast.makeText(ProfileActivity.this, "头像上传成功", Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            Logger.e(e);
                                            Toast.makeText(ProfileActivity.this, "头像上传失败，请重试", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(HttpException e, String s) {
                                        button.setEnabled(true);
                                        Logger.e(e);
                                        Toast.makeText(ProfileActivity.this, "头像上传失败，请重试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                Logger.e(e);
                                Toast.makeText(ProfileActivity.this, "头像上传失败，请重试", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Logger.e(e);
                            Toast.makeText(ProfileActivity.this, "头像上传失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                break;
            case REQUEST_PROVINCE:
                if (resultCode == RESULT_OK) {
                    String city = data.getStringExtra("city");
                    if (city != null)
                        address.setText(city);
                }
                break;
        }
    }

    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
        option.setCoorType("gcj02");//返回的定位结果是百度经纬度，默认值gcj02
        int span = 5000;
        option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//设置是否需要地址信息，默认为无地址
        option.setOpenGps(true);
        option.setProdName("meetxyt");
        mLocationClient.setLocOption(option);
    }
}
