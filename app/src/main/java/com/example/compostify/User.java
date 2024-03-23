package com.example.compostify;

import com.google.firebase.Timestamp;

import java.util.List;

public class User {
    private List<String> imageUrls;
    private String mixWasteWeight;
    private String naturalWasteWeight;
    private String otherDetails;
    private Timestamp postDateTime; // Assuming this is a long representing Unix timestamp
    private String totalWeight;
    private String typeOfUser;
    private String typeOfWaste;

    public User() {
        // Required default constructor
    }

    public User(List<String> imageUrls, String mixWasteWeight, String naturalWasteWeight, String otherDetails,
                Timestamp postDateTime, String totalWeight, String typeOfUser, String typeOfWaste) {
        this.imageUrls = imageUrls;
        this.mixWasteWeight = mixWasteWeight;
        this.naturalWasteWeight = naturalWasteWeight;
        this.otherDetails = otherDetails;
        this.postDateTime = postDateTime;
        this.totalWeight = totalWeight;
        this.typeOfUser = typeOfUser;
        this.typeOfWaste = typeOfWaste;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getMixWasteWeight() {
        return mixWasteWeight;
    }

    public void setMixWasteWeight(String mixWasteWeight) {
        this.mixWasteWeight = mixWasteWeight;
    }

    public String getNaturalWasteWeight() {
        return naturalWasteWeight;
    }

    public void setNaturalWasteWeight(String naturalWasteWeight) {
        this.naturalWasteWeight = naturalWasteWeight;
    }

    public String getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(String otherDetails) {
        this.otherDetails = otherDetails;
    }

    public Timestamp getPostDateTime() {
        return postDateTime;
    }

    public void setPostDateTime(Timestamp postDateTime) {
        this.postDateTime = postDateTime;
    }

    public String getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(String totalWeight) {
        this.totalWeight = totalWeight;
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
}

