package com.example.ptsafe.model;

public class PostComment {
    private String newsid;
    private String commenttitle;
    private String commentcontent;

    public PostComment() {

    }

    public PostComment(String newsId, String commentTitle, String commentContent) {
        this.newsid = newsId;
        this.commenttitle = commentTitle;
        this.commentcontent = commentContent;
    }

    public String getNewsId() {
        return newsid;
    }

    public void setNewsId(String newsId) {
        this.newsid = newsId;
    }

    public String getCommentTitle() {
        return commenttitle;
    }

    public void setCommentTitle(String commentTitle) {
        this.commenttitle = commentTitle;
    }

    public String getCommentContent() {
        return commentcontent;
    }

    public void setCommentContent(String commentContent) {
        this.commentcontent = commentContent;
    }
}
