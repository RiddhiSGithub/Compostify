package com.example.compostify.db;

public class UserRecentActivity {
    private String typeOfUser;
    private String typeOfWaste;
    private String date;
    private String time;
    private String weight;
    private String imageUrls;

    // Constructor
    public UserRecentActivity(String typeOfUser, String typeOfWaste, String date,String time, String weight,String imageUrls) {
        this.typeOfUser = typeOfUser;
        this.typeOfWaste = typeOfWaste;
        this.date = date;
        this.time = time;
        this.weight = weight;
        this.imageUrls = imageUrls;
    }

    // Getters and setters
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
}

