package com.pro.music.model;

import java.io.Serializable;

public class Staff implements Serializable {
    private long id;
    private String name;
    private String image;
    private String email;
    private String password;

    public Staff() {}

    public Staff(long id, String name, String image, String email, String password) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
