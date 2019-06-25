package com.ikvaesolutions.android.models.recyclerviews;

/**
 * Created by amarilindra on 12/07/17.
 */

public class CreditsModel {
    private String title;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public CreditsModel() {
    }

    public CreditsModel(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }

}
