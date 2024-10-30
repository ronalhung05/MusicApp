package com.pro.music.model;

import java.io.Serializable;

public class UserInfor implements Serializable {

    private long id;
    private String emailUser;

    public UserInfor() {}

    public UserInfor(long id, String emailUser) {
        this.id = id;
        this.emailUser = emailUser;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }
}
