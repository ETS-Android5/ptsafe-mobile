package com.example.ptsafe.model;

public class Comment {
    private String commentId;
    private String newsId;
    private String commentTitle;
    private String commentContent;

    public Comment() {

    }

    public Comment(String commentId, String newsId, String commentTitle, String commentContent) {
        this.commentId = commentId;
        this.newsId = newsId;
        this.commentTitle = commentTitle;
        this.commentContent = commentContent;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getCommentTitle() {
        return commentTitle;
    }

    public void setCommentTitle(String commentTitle) {
        this.commentTitle = commentTitle;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
