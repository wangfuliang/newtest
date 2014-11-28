package com.vikaa.lubbi.entity;

import com.vikaa.lubbi.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户提醒
 */
public class UserRemindEntity {
    String hash;
    String title;
    String time;
    String formatTime;
    int repeat;
    boolean hide_remind;
    String mark;
    int hits;
    int shares;
    int created_at;
    boolean recommend;
    String openid;
    boolean isSigned;
    boolean isAdmin;

    public UserRemindEntity(JSONObject data) {
        try {
            this.hash = data.getString("hash");
            this.title = data.getString("title");
            this.time = data.getString("time");
            this.formatTime = data.getString("format_time");
            this.repeat = data.getInt("repeat");
            this.hide_remind = data.getInt("hide_remind") == 1;
            this.mark = data.getString("mark");
            this.hits = data.getInt("hits");
            this.shares = data.getInt("shares");
            this.created_at = data.getInt("created_at");
            this.recommend = data.getInt("recommend") == 1;
            this.openid = data.getString("openid");
            this.isSigned = data.getInt("isSigned") == 1;
            this.isAdmin = data.getInt("isAdmin") == 1;
        } catch (JSONException e) {
            Logger.e(e);
        }
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFormatTime() {
        return formatTime;
    }

    public void setFormatTime(String formatTime) {
        this.formatTime = formatTime;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public boolean isHide_remind() {
        return hide_remind;
    }

    public void setHide_remind(boolean hide_remind) {
        this.hide_remind = hide_remind;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public boolean isSigned() {
        return isSigned;
    }

    public void setSigned(boolean isSigned) {
        this.isSigned = isSigned;
    }
}
