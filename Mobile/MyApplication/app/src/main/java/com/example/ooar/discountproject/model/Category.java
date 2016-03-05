package com.example.ooar.discountproject.model;

/**
 * Created by Onur Kuru on 5.3.2016.
 */
public class Category {

    private Integer id;
    private String categoryName;
    private Category parentCategory;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }
}
