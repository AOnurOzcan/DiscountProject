package com.example.ooar.discountproject.model;

/**
 * Created by Onur Kuru on 9.3.2016.
 */
public class CompanyCategory {

    private Integer id;
    private Company companyId;
    private Category categoryId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Company getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Company companyId) {
        this.companyId = companyId;
    }

    public Category getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Category categoryId) {
        this.categoryId = categoryId;
    }
}
