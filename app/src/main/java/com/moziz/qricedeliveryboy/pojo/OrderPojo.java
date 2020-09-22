package com.moziz.qricedeliveryboy.pojo;

public class OrderPojo {
    private int orderID;
    private String orderNumber;
    private int totalProducts;
    private double grandTotal;
    private String userName;
    private  String orderDate;
    private String status;
    private String orderStatus;
    private String deliverScheduler;
    private int deliveryBtn;

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getDeliverScheduler() {
        return deliverScheduler;
    }

    public void setDeliverScheduler(String deliverScheduler) {
        this.deliverScheduler = deliverScheduler;
    }

    public int getDeliveryBtn() {
        return deliveryBtn;
    }

    public void setDeliveryBtn(int deliveryBtn) {
        this.deliveryBtn = deliveryBtn;
    }
}
