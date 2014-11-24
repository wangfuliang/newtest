package com.vikaa.lubbi.entity;

public class Notification {
    String avatar;
    String nickname;
    long time;
    int type;
    String content;

    public Notification(String avatar, String nickname, long time, int type, String content) {
        this.avatar = avatar;
        this.nickname = nickname;
        this.time = time;
        this.type = type;
        this.content = content;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public long getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
