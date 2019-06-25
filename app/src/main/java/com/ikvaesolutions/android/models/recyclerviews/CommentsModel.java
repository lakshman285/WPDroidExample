package com.ikvaesolutions.android.models.recyclerviews;

/**
 * Created by amarilindra on 14/07/17.
 */

public class CommentsModel {

    private String commentId;
    private String parentCommentId;
    private String authorName;
    private String authorId;
    private String authorProfilePicture;
    private String commentedTime;
    private String comment;

    public CommentsModel() {
    }

    public CommentsModel(String commentId, String parentCommentId, String authorName, String authorId, String authorProfilePicture, String commentedTime, String comment) {
        this.commentId = commentId;
        this.parentCommentId = parentCommentId;
        this.authorName = authorName;
        this.authorId = authorId;
        this.authorProfilePicture = authorProfilePicture;
        this.commentedTime = commentedTime;
        this.comment = comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorProfilePicture() {
        return authorProfilePicture;
    }

    public void setAuthorProfilePicture(String authorProfilePicture) {
        this.authorProfilePicture = authorProfilePicture;
    }

    public String getCommentedTime() {
        return commentedTime;
    }

    public void setCommentedTime(String commentedTime) {
        this.commentedTime = commentedTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
