package com.example.compostify.db;

public class UserRecentActivity {
    private String userId;
    private String publishId;
    private String typeOfUser;
    private String typeOfWaste;
    private String date;
    private String time;
    private String NaturalWasteWeight;
    private String MixWasteWeight;
    private String weight;
    private String imageUrls;
    private String postStatus;

    // Constructor
    public UserRecentActivity(String userId, String publishId,String typeOfUser, String typeOfWaste, String date,String time,String NaturalWasteWeight, String MixWasteWeight, String weight,String imageUrls, String postStatus) {
        this.userId = userId;
        this.publishId = publishId;
        this.typeOfUser = typeOfUser;
        this.typeOfWaste = typeOfWaste;
        this.date = date;
        this.time = time;
        this.NaturalWasteWeight = NaturalWasteWeight;
        this.MixWasteWeight = MixWasteWeight;
        this.weight = weight;
        this.imageUrls = imageUrls;
        this.postStatus = postStatus;
    }

    // Getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPublishId(){
        return publishId;
    }
    public void setPublishId(String publishId){
        this.publishId = publishId;
    }
    public String getTypeOfUser() {
        return typeOfUser;
    }

    public void setTypeOfUser(String typeOfUser) {
        this.typeOfUser = typeOfUser;
    }

    public String getTypeOfWaste() {
        return typeOfWaste;
    }

    public void setTypeOfWaste(String typeOfWaste) {
        this.typeOfWaste = typeOfWaste;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime(){
        return time;
    }
    public void setTime(String time){
        this.time = time;
    }

    public String getNaturalWasteWeight() {
        return NaturalWasteWeight;
    }

    public void setNaturalWasteWeight(String naturalWasteWeight) {
        NaturalWasteWeight = naturalWasteWeight;
    }

    public String getMixWasteWeight() {
        return MixWasteWeight;
    }

    public void setMixWasteWeight(String mixWasteWeight) {
        MixWasteWeight = mixWasteWeight;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getImageUrl() {
        return imageUrls;
    }

    public void setImageUrl(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }
}

