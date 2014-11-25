package com.vikaa.lubbi.entity;

public class Notification {
    int notification_id;
    String avatar;
    String nickname;
    long time;
    int type;
    String content;
    String hash;

    public Notification(int id,String avatar, String nickname, long time, int type, String content, String hash) {
        this.notification_id = id;
        this.avatar = avatar;
        this.nickname = nickname;
        this.time = time;
        this.type = type;
        this.content = content;
        this.hash = hash;
    }

    public String getHash() {
        return hash;
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

    public int getNotification_id() {
        return notification_id;
    }
}
