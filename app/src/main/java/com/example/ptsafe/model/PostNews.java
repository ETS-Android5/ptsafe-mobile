package com.example.ptsafe.model;

public class PostNews {
    private String newstitle;
    private String newslabel;
    private String newscontent;
    private String imageurl;
    private String newsurl;

    public PostNews() {
    }

    public PostNews(String newstitle, String newslabel, String newscontent, String imageurl, String newsurl) {
        this.newstitle = newstitle;
        this.newslabel = newslabel;
        this.newscontent = newscontent;
        this.imageurl = imageurl;
        this.newsurl = newsurl;
    }

    public String getNewsTitle() {
        return newstitle;
    }

    public void setNewsTitle(String newstitle) {
        this.newstitle = newstitle;
    }

    public String getNewsLabel() {
        return newslabel;
    }

    public void setNewsLabel(String newslabel) {
        this.newslabel = newslabel;
    }

    public String getNewsContent() {
        return newscontent;
    }

    public void setNewsContent(String newscontent) {
        this.newscontent = newscontent;
    }

    public String getImageUrl() {
        return imageurl;
    }

    public void setImageUrl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getNewsUrl() {
        return newsurl;
    }

    public void setNewsUrl(String newsurl) {
        this.newsurl = newsurl;
    }
}
