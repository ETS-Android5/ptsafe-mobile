package com.example.ptsafe.model;


public class News {

   private String newsId;
   private String newsTitle;
   private String newsLabel;
   private String newsContent;
   private String imageUrl;
   private String newsUrl;
   private String newsLocation;
   private Integer newsPostCode;

   public News(){

   }

   public News(String newsId, String newsTitle, String newsLabel, String newsContent, String imageUrl, String newsUrl, String newsLocation, Integer newsPostCode) {
      this.newsId = newsId;
      this.newsTitle = newsTitle;
      this.newsLabel = newsLabel;
      this.newsContent = newsContent;
      this.imageUrl = imageUrl;
      this.newsUrl = newsUrl;
      this.newsLocation = newsLocation;
      this.newsPostCode=  newsPostCode;
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

   public String getNewsLabel() {
      return newsLabel;
   }

   public void setNewsLabel(String newsLabel) {
      this.newsLabel = newsLabel;
   }

   public String getNewsContent() {
      return newsContent;
   }

   public void setNewsContent(String newsContent) {
      this.newsContent = newsContent;
   }

   public String getImageUrl() {
      return imageUrl;
   }

   public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
   }

   public String getNewsUrl() {
      return newsUrl;
   }

   public void setNewsUrl(String newsUrl) {
      this.newsUrl = newsUrl;
   }

   public String getNewsLocation() {
      return newsLocation;
   }

   public void setNewsLocation(String newsLocation) {
      this.newsLocation = newsLocation;
   }

   public Integer getNewsPostCode() {
      return newsPostCode;
   }

   public void setNewsPostCode(int newsPostCode) {
      this.newsPostCode = newsPostCode;
   }
}