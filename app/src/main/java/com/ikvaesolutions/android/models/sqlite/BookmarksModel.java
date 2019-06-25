package com.ikvaesolutions.android.models.sqlite;

/**
 * Created by amarilindra on 29/06/17.
 */

public class BookmarksModel {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BookmarksModel(String id, String title, String image, String time, String authorName, String postTime) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.time = time;
        this.authorName = authorName;
        this.postTime = postTime;
    }

    public BookmarksModel() {
    }

    private String id;
    private String title;
    private String image;
    private String time;
    private String authorName;
    private String postTime;


    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

}

