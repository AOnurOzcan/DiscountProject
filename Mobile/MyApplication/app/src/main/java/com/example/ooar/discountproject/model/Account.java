package com.example.ooar.discountproject.model;

/**
 * Created by Onur Kuru on 5.3.2016.
 */

public class Account {

    public enum Auth {
        CREATE_USER, DELETE_USER
    }

    public enum AccountType {
        ADMIN, COMPANY
    }

    private Integer id;
    private String username;
    private String password;
    private AccountType accountType;
    private Auth accountAuth;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Auth getAccountAuth() {
        return accountAuth;
    }

    public void setAccountAuth(Auth accountAuth) {
        this.accountAuth = accountAuth;
    }
}
