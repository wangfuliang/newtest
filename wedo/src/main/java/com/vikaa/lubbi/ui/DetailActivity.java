package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.MyApi;
import com.vikaa.lubbi.core.MyMessage;
import com.vikaa.lubbi.util.Animate;
import com.vikaa.lubbi.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends BaseActivity {

    public static JSONObject _data;
    @ViewInject(R.id.count)
    RelativeLayout count;
    @ViewInject(R.id.share)
    ImageView share;
    @ViewInject(R.id.back)
    ImageView back;
    @ViewInject(R.id.title)
    TextView title;
    @ViewInject(R.id.time)
    TextView time;
    @ViewInject(R.id.mark)
    TextView mark;
    @ViewInject(R.id.lx_days)
    TextView lxDays;
    @ViewInject(R.id.lj_days)
    TextView ljDays;
    @ViewInject(R.id.joins_count)
    TextView joinsCount;
    @ViewInject(R.id.today_signs)
    TextView todaySigns;
    @ViewInject(R.id.join)
    Button join;
    String hash;
    String _title;
    String _time;
    String _mark;
    boolean isAdd;

    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share", RequestType.SOCIAL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ViewUtils.inject(this);

        try {
            hash = _data.getString("hash");
            isAdd = _data.getInt("isAdd") == 1;
        } catch (JSONException e) {
            Logger.e(hash);
            hash = "";
            isAdd = false;
        }
        //设置详情
        setDetail();
        //今天打卡数
        setCount();
        //按钮监听
        back.setOnClickListener(new BackListener());
        share.setOnClickListener(new ShareListener());
        mController.getConfig().registerListener(new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {
                Logger.d("share start");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, SocializeEntity socializeEntity) {
                Logger.d("share complete");
            }
        });
        //按钮的显示与隐藏
        if (!isAdd) {
            join.setVisibility(View.INVISIBLE);
            Animate.rotate(join);
        } else
            join.setVisibility(View.GONE);
        join.setOnClickListener(new JoinListener());
    }

    private void setCount() {
        if (hash == null || hash.length() == 0) {
            handler.sendEmptyMessage(MyMessage.START_HOME);
            finish();
        }

        RequestParams params = new RequestParams();
        params.addBodyParameter("hash", hash);
        params.addQueryStringParameter("_sign",sign);
        httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.getSignInfo, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                String resp = objectResponseInfo.result;
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    if (jsonObject.getInt("status") == 0) {
                        Toast.makeText(DetailActivity.this, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject _data = jsonObject.getJSONObject("info");
                    String joins = _data.getString("joins");
                    String signed = _data.getString("signed");
                    String leiji = _data.getString("leiji");
                    String lianxu = _data.getString("lianxu");

                    joinsCount.setText(joins);
                    todaySigns.setText(signed);
                    ljDays.setText(leiji);
                    lxDays.setText(lianxu);
                    //动画
                    Animate.bounce(count);
                } catch (JSONException e) {
                    Logger.e(e);
                    Toast.makeText(DetailActivity.this, "提醒数据加载失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Logger.e(e);
                Toast.makeText(DetailActivity.this, "提醒数据加载失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDetail() {
        try {
            _title = _data.getString("title");
            _time = _data.getString("time");
            _mark = _data.getString("mark");

            title.setText(_title);
            time.setText(_time);
            mark.setText(_mark);

        } catch (JSONException e) {
            Logger.e(e);
        }
    }

    private class BackListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class ShareListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //准备分享数据
            showShare(_mark, _title, MyApi.share + hash, MyApi.shareIcon);
        }
    }

    /**
     * 分享
     *
     * @param desc
     * @param title
     * @param link
     * @param TLImg
     */
    public void showShare(final String desc, final String title, final String link, String TLImg) {
        UMImage mUMImgBitmap = new UMImage(this, TLImg);
        mController.setShareImage(mUMImgBitmap);
        SinaShareContent sinaShareContent = new SinaShareContent();
        sinaShareContent.setShareImage(mUMImgBitmap);
        sinaShareContent.setTargetUrl(link);
        sinaShareContent.setShareContent(desc + " " + link);
        sinaShareContent.setTitle(title);
        mController.setShareMedia(sinaShareContent);

        TencentWbShareContent tencentWbShareContent = new TencentWbShareContent();
        tencentWbShareContent.setShareImage(mUMImgBitmap);
        tencentWbShareContent.setTargetUrl(link);
        tencentWbShareContent.setShareContent(desc + " " + link);
        tencentWbShareContent.setTitle(title);
        mController.setShareMedia(tencentWbShareContent);

        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareImage(mUMImgBitmap);
        qqShareContent.setTargetUrl(link);
        qqShareContent.setShareContent(desc);
        qqShareContent.setTitle(title);
        mController.setShareMedia(qqShareContent);

        QZoneShareContent qZoneShareContent = new QZoneShareContent();
        qZoneShareContent.setShareImage(mUMImgBitmap);
        qZoneShareContent.setTargetUrl(link);
        qZoneShareContent.setShareContent(desc);
        qZoneShareContent.setTitle(title);
        mController.setShareMedia(qZoneShareContent);

        mController.getConfig().openQQZoneSso();
        mController.getConfig().setSsoHandler(new QZoneSsoHandler(this, "100371282", "aed9b0303e3ed1e27bae87c33761161d"));
        mController.getConfig().supportQQPlatform(this, "100371282", "aed9b0303e3ed1e27bae87c33761161d", link);


        mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN, SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL);
        mController.openShare(this, false);
    }

    private class JoinListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            join.setEnabled(false);
            RequestParams params = new RequestParams();
            params.addBodyParameter("hash", hash);
            params.addQueryStringParameter("_sign",sign);
            httpUtils.send(HttpRequest.HttpMethod.POST, MyApi.joinRemind, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                    try {
                        JSONObject _data = new JSONObject(objectResponseInfo.result);
                        if (_data.getInt("status") == 0) {
                            join.setEnabled(true);
                            Toast.makeText(DetailActivity.this, "加入失败，请重试", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(DetailActivity.this, "加入成功", Toast.LENGTH_LONG).show();
                        //动画移除我要加入
                        Animate.alpha(join,1f,0f,500);
                    } catch (JSONException e) {
                        Logger.e(e);
                        join.setEnabled(true);
                        Toast.makeText(DetailActivity.this, "加入失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Logger.e(e);
                    join.setEnabled(true);
                    Toast.makeText(DetailActivity.this, "加入失败，请重试", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
