package com.pro.music.model;

import java.io.Serializable;

public class Premium implements Serializable {
    private String email;
    private boolean isPremium;

    public Premium(String email, boolean isPremium) {
        this.email = email;
        this.isPremium = isPremium;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }
}
