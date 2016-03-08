package com.example.ooar.discountproject.model;

/**
 * Created by Onur Kuru on 7.3.2016.
 */
public class Token {

    private Integer id;
    private String tokenKey;
    private User user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
