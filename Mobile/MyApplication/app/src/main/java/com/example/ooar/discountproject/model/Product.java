package com.example.ooar.discountproject.model;

import java.util.List;

/**
 * Created by Onur Kuru on 5.3.2016.
 */
public class Product {

    private Integer id;
    private String productName;
    private double previousPrice;
    private double price;
    private int stock;
    private String productDescription;
    private String imageURL;
    private Category categoryId;
    private List<UserProduct> followList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    public void setPreviousPrice(double previousPrice) {
        this.previousPrice = previousPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Category getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Category categoryId) {
        this.categoryId = categoryId;
    }

    public List<UserProduct> getFollowList() {
        return followList;
    }

    public void setFollowList(List<UserProduct> followList) {
        this.followList = followList;
    }
}
