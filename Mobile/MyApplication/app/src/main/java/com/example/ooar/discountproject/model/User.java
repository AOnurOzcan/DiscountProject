package com.example.ooar.discountproject.model;

import java.util.Date;

/**
 * Created by Onur Kuru on 5.3.2016.
 */
public class User {
    private Integer id;
    private String firstName;
    private String lastName;
    private String phone;
    private boolean gender;
    private boolean notificationOpen;
    private Date birthday;
    private City cityId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public boolean isNotificationOpen() {
        return notificationOpen;
    }

    public void setNotificationOpen(boolean notificationOpen) {
        this.notificationOpen = notificationOpen;
    }

    public City getCityId() {
        return cityId;
    }

    public void setCityId(City cityId) {
        this.cityId = cityId;
    }
}