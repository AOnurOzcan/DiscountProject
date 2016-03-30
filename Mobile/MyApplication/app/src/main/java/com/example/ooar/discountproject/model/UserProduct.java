package com.example.ooar.discountproject.model;

/**
 * Created by Onur Kuru on 30.3.2016.
 */
public class UserProduct {

    private Integer id;
    private User userId;
    private Product productId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Product getProductId() {
        return productId;
    }

    public void setProductId(Product productId) {
        this.productId = productId;
    }
}
