package com.vikaa.lubbi.entity;

import android.os.Handler;
import android.os.Message;

import com.vikaa.lubbi.util.UI;

import java.util.List;

public class SignEntity {
    int sign_id;
    String message;
    String[] images;
    int sign_at;
    String openid;
    String hash;
    boolean isPraised;
    int praise;
    List<CommonEntity> comments;
    UserEntity user;

    public SignEntity(int sign_id, String message, String[] images, int sign_at, String openid, String hash, boolean isPraised, int praise, List<CommonEntity> comments, UserEntity user) {
        this.sign_id = sign_id;
        this.message = message;
        this.images = images;
        this.sign_at = sign_at;
        this.openid = openid;
        this.hash = hash;
        this.isPraised = isPraised;
        this.praise = praise;
        this.user = user;
        this.comments = comments;
    }

    public int getSign_id() {
        return sign_id;
    }

    public void setSign_id(int sign_id) {
        this.sign_id = sign_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public int getSign_at() {
        return sign_at;
    }

    public void setSign_at(int sign_at) {
        this.sign_at = sign_at;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public boolean isPraised() {
        return isPraised;
    }

    public void setPraised(boolean isPraised) {
        this.isPraised = isPraised;
    }

    public int getPraise() {
        return praise;
    }

    public void setPraise(int praise) {
        this.praise = praise;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<CommonEntity> getComments() {
        return comments;
    }

    public void setComments(List<CommonEntity> comments) {
        this.comments = comments;
    }
}
