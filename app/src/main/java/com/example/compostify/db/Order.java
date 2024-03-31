package com.example.compostify.db;

import java.io.Serializable;

public class Order implements Serializable {
    private String sellerBusinessName;
    private String sellerName;
    private String sellerTypeOfUser;
    private String sellerAddress;
    private String orderDate;
    private String typeOfWaste;
    private String weight;
    private String naturalWeight;
    private String mixWeight;
    private String totalAmount;
    private String purchaserBusinessName;
    private String purchaserName;
    private String purchaserTypeOfUser;
    private String purchaserCardNumber;
    private String sellerBusinessLogoUrl;

    public Order(String sellerBusinessName, String sellerName, String sellerTypeOfUser, String sellerAddress, String orderDate, String typeOfWaste, String weight, String naturalWeight, String mixWeight, String totalAmount, String purchaserBusinessName, String purchaserName, String purchaserTypeOfUser, String purchaserCardNumber, String sellerBusinessLogoUrl) {
        this.sellerBusinessName = sellerBusinessName;
        this.sellerName = sellerName;
        this.sellerTypeOfUser = sellerTypeOfUser;
        this.sellerAddress = sellerAddress;
        this.orderDate = orderDate;
        this.typeOfWaste = typeOfWaste;
        this.weight = weight;
        this.naturalWeight = naturalWeight;
        this.mixWeight = mixWeight;
        this.totalAmount = totalAmount;
        this.purchaserBusinessName = purchaserBusinessName;
        this.purchaserName = purchaserName;
        this.purchaserTypeOfUser = purchaserTypeOfUser;
        this.purchaserCardNumber = purchaserCardNumber;
        this.sellerBusinessLogoUrl = sellerBusinessLogoUrl;
    }

    public String getSellerBusinessName() {
        return sellerBusinessName;
    }

    public void setSellerBusinessName(String sellerBusinessName) {
        this.sellerBusinessName = sellerBusinessName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerTypeOfUser() {
        return sellerTypeOfUser;
    }

    public void setSellerTypeOfUser(String sellerTypeOfUser) {
        this.sellerTypeOfUser = sellerTypeOfUser;
    }

    public String getSellerAddress() {
        return sellerAddress;
    }

    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getTypeOfWaste() {
        return typeOfWaste;
    }

    public void setTypeOfWaste(String typeOfWaste) {
        this.typeOfWaste = typeOfWaste;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getNaturalWeight() {
        return naturalWeight;
    }

    public void setNaturalWeight(String naturalWeight) {
        this.naturalWeight = naturalWeight;
    }

    public String getMixWeight() {
        return mixWeight;
    }

    public void setMixWeight(String mixWeight) {
        this.mixWeight = mixWeight;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPurchaserBusinessName() {
        return purchaserBusinessName;
    }

    public void setPurchaserBusinessName(String purchaserBusinessName) {
        this.purchaserBusinessName = purchaserBusinessName;
    }

    public String getPurchaserName() {
        return purchaserName;
    }

    public void setPurchaserName(String purchaserName) {
        this.purchaserName = purchaserName;
    }

    public String getPurchaserTypeOfUser() {
        return purchaserTypeOfUser;
    }

    public void setPurchaserTypeOfUser(String purchaserTypeOfUser) {
        this.purchaserTypeOfUser = purchaserTypeOfUser;
    }

    public String getPurchaserCardNumber() {
        return purchaserCardNumber;
    }

    public void setPurchaserCardNumber(String purchaserCardNumber) {
        this.purchaserCardNumber = purchaserCardNumber;
    }

    public String getSellerBusinessLogoUrl() {
        return sellerBusinessLogoUrl;
    }

    public void setSellerBusinessLogoUrl(String sellerBusinessLogoUrl) {
        this.sellerBusinessLogoUrl = sellerBusinessLogoUrl;
    }
}
