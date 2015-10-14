package com.gnirt69.loveandlife.model;

public class Category {

    private int categoryId;
    private String categoryName;
    private int imgResourceCategoryId;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getImgResourceCategoryId() {
        return imgResourceCategoryId;
    }

    public void setImgResourceCategoryId(int imgResourceCategoryId) {
        this.imgResourceCategoryId = imgResourceCategoryId;
    }

    public Category(int categoryId, String categoryName,
                    int imgResourceCategoryId) {
        super();
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.imgResourceCategoryId = imgResourceCategoryId;
    }

}
