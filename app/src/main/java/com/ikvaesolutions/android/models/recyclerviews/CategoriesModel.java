package com.ikvaesolutions.android.models.recyclerviews;

public class CategoriesModel  {
    private int categoryId;
    private String categoryCount;
    private String categoryName;

    public CategoriesModel() {

    }

    public CategoriesModel(int categoryId, String categoryCount, String categoryName){
        this.categoryId = categoryId;
        this.categoryCount = categoryCount;
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(String categoryCount) {
        this.categoryCount = categoryCount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
