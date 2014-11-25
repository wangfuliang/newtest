package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
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
import com.vikaa.lubbi.MainActivity;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.adapter.SignItemAdapter;
import com.vikaa.lubbi.core.AppConfig;
import com.vikaa.lubbi.util.Http;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.UI;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailFragment extends Fragment {
    JSONObject remindDetail;
    @ViewInject(R.id.detail_title)
    TextView detailTitle;
    @ViewInject(R.id.detail_time)
    TextView detailTime;
    @ViewInject(R.id.detail_content)
    TextView detailContent;
    @ViewInject(R.id.lj_days)
    TextView ljDays;
    @ViewInject(R.id.lx_days)
    TextView lxDays;
    @ViewInject(R.id.today_signs)
    TextView todaySigns;
    @ViewInject(R.id.joins_count)
    TextView joinsCount;
    @ViewInject(R.id.sign_listView)
    public static ListView signListView;
    @ViewInject(R.id.btn_share)
    Button share;
    SignItemAdapter sign_list;

    public void setRemindDetail(JSONObject remindDetail) {
        this.remindDetail = remindDetail;
    }

    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share", RequestType.SOCIAL);

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, null);
        ViewUtils.inject(this, view);


        try {
            String detail_title = remindDetail.getString("title");
            String detail_time = remindDetail.getString("format_time");
            String detail_content = remindDetail.getString("mark");
            if (TextUtils.isEmpty(detail_content))
                detail_content = "  这个人很懒，什么也没有写...";
            else
                detail_content = "  " + detail_content;
            detailTitle.setText(detail_title);
            detailTime.setText(detail_time);
            detailContent.setText(Html.fromHtml("<u>" + detail_content + "</u>"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //加载打卡列表
        try {
            String hash = remindDetail.getString("hash");
            RequestParams params = new RequestParams();
            params.put("_sign", MainActivity._sign);
            params.put("hash", hash);
            params.put("page", "1");
            Http.get(AppConfig.Api.listSign, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray ja = response.getJSONArray("info");
                        sign_list = new SignItemAdapter(getActivity());
                        sign_list.setList(ja);
                        signListView.setAdapter(sign_list);
                        UI.setListViewHeightBasedOnChildren(signListView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //加载打卡天数
        try {
            String hash = remindDetail.getString("hash");
            RequestParams params = new RequestParams();
            params.put("hash", hash);
            Http.post(AppConfig.Api.getSignInfo + "?_sign=" + MainActivity._sign, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONObject info = response.getJSONObject("info");
                        String joins = info.getString("joins");
                        String signed = info.getString("signed");
                        String leiji = info.getString("leiji");
                        String lianxu = info.getString("lianxu");

                        joinsCount.setText(joins);
                        todaySigns.setText(signed);
                        ljDays.setText(leiji);
                        lxDays.setText(lianxu);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


            mController.getConfig().registerListener(new SocializeListeners.SnsPostListener() {
                @Override
                public void onStart() {
                    Toast.makeText(getActivity(), "分享开始", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, SocializeEntity socializeEntity) {
                    Toast.makeText(getActivity(), "分享完成", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


        share.setOnClickListener(new ShareListener());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置标题和时间

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public class ShareListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            try {
                String title = remindDetail.getString("title");
                String desc = remindDetail.getString("mark");
                String link = AppConfig.Api.share + remindDetail.getString("hash");
                showShare(title, desc, link, "http://picturescdn.qiniudn.com/tixing.jpg");
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "分享失败,请重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showShare(final String desc, final String title, final String link, String TLImg) {
        UMImage mUMImgBitmap = new UMImage(getActivity(), TLImg);
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
        mController.getConfig().setSsoHandler(new QZoneSsoHandler(getActivity(), "100371282", "aed9b0303e3ed1e27bae87c33761161d"));
        mController.getConfig().supportQQPlatform(getActivity(), "100371282", "aed9b0303e3ed1e27bae87c33761161d", link);


        mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN, SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL);
        mController.openShare(getActivity(), false);
    }
}
