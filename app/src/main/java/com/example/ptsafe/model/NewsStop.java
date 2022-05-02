package com.example.ptsafe.model;

public class NewsStop {

    private String newsId;
    private String newsTitle;
    private String newsAddress;
    private String imageUrl;
    private String newsContent;
    private String newsUrl;

    public NewsStop() {
    }

    public NewsStop(String newsId, String newsTitle, String newsAddress, String imageUrl, String newsContent, String newsUrl) {
        this.newsId = newsId;
        this.newsTitle = newsTitle;
        this.newsAddress = newsAddress;
        this.imageUrl = imageUrl;
        this.newsContent = newsContent;
        this.newsUrl = newsUrl;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsAddress() {
        return newsAddress;
    }

    public void setNewsAddress(String newsAddress) {
        this.newsAddress = newsAddress;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }
}
