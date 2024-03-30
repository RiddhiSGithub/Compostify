package com.example.compostify.db;

import java.io.Serializable;
import java.util.HashMap;

public class Order implements Serializable {
    private String orderId;
    private String publishId;
    private String sellerId;
    private String purchaserId;

    public Order(String orderId, String publishId, String sellerId, String purchaserId) {
        this.orderId = orderId;
        this.publishId = publishId;
        this.sellerId = sellerId;
        this.purchaserId = purchaserId;
    }

    public Order(String publishId, String sellerId, String purchaserId) {
        this.publishId = publishId;
        this.sellerId = sellerId;
        this.purchaserId = purchaserId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPublishId() {
        return publishId;
    }

    public void setPublishId(String publishId) {
        this.publishId = publishId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getPurchaserId() {
        return purchaserId;
    }

    public void setPurchaserId(String purchaserId) {
        this.purchaserId = purchaserId;
    }

    public HashMap<String, String> toHashMap(){
        HashMap<String, String> hm = new HashMap<String, String>();
        hm.put("publishId", publishId);
        hm.put("sellerId", sellerId);
        hm.put("purchaserId", purchaserId);
        return hm;
    }
}
