package com.example.ooar.discountproject.model;

/**
 * Created by Ramos on 03.05.2016.
 */
public class ItemSlideMenu {
    private int ImgId;
    private String title;

    public ItemSlideMenu(int imgId, String title) {
        ImgId = imgId;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImgId() {
        return ImgId;
    }

    public void setImgId(int imgId) {
        ImgId = imgId;
    }
}
