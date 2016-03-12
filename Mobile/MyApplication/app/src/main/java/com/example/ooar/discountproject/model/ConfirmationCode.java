package com.example.ooar.discountproject.model;

/**
 * Created by Onur Kuru on 12.3.2016.
 */
public class ConfirmationCode {

    private Integer id;
    private String phoneNumber;
    private int confirmationCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(int confirmationCode) {
        this.confirmationCode = confirmationCode;
    }
}
