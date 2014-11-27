package com.vikaa.lubbi.entity;

public class UserEntity {
    String avatar;
    String nickname;

    public UserEntity(String avatar, String nickname) {
        this.avatar = avatar;
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
