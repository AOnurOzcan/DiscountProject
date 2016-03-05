package com.example.ooar.discountproject.model;

/**
 * Created by Onur Kuru on 5.3.2016.
 */
public class Auth {

    private Integer id;
    private Account accountId;
    private Company companyId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Account getAccountId() {
        return accountId;
    }

    public void setAccountId(Account accountId) {
        this.accountId = accountId;
    }

    public Company getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Company companyId) {
        this.companyId = companyId;
    }
}
