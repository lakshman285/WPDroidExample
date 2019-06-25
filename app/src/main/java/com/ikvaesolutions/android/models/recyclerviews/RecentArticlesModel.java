package com.ikvaesolutions.android.models.recyclerviews;

/**
 * Created by AMAR on 10/22/2016.
 */
public class RecentArticlesModel {
    private String title;
    private String thumbnail;
    private Integer id;
    private String authorName;
    private String timestamp;
    private String layoutStyle;

    public RecentArticlesModel() {
    }

    public RecentArticlesModel(String title, String thumbnail, Integer id, String authorName, String timestamp, String layoutStyle) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.id = id;
        this.authorName = authorName;
        this.timestamp = timestamp;
        this.layoutStyle = layoutStyle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getLayoutStyle() {
        return layoutStyle;
    }

    public void setLayoutStyle(String layoutStyle) {
        this.layoutStyle = layoutStyle;
    }
}
