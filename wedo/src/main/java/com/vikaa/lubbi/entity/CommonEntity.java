package com.vikaa.lubbi.entity;

public class CommonEntity {
    int comment_id;
    String message;
    int comment_at;
    String openid;
    int sign_id;
    UserEntity user;

    public CommonEntity(int comment_id, String message, int comment_at, String openid, int sign_id, UserEntity user) {
        this.comment_id = comment_id;
        this.message = message;
        this.comment_at = comment_at;
        this.openid = openid;
        this.sign_id = sign_id;
        this.user = user;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getComment_at() {
        return comment_at;
    }

    public void setComment_at(int comment_at) {
        this.comment_at = comment_at;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public int getSign_id() {
        return sign_id;
    }

    public void setSign_id(int sign_id) {
        this.sign_id = sign_id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
