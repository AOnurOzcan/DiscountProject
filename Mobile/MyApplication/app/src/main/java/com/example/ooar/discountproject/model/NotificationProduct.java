package com.example.ooar.discountproject.model;

/**
 * Created by Onur Kuru on 5.3.2016.
 */
public class NotificationProduct {

    private Integer id;
    private Notification notificationId;
    private Product productId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Notification getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Notification notificationId) {
        this.notificationId = notificationId;
    }

    public Product getProductId() {
        return productId;
    }

    public void setProductId(Product productId) {
        this.productId = productId;
    }
}
