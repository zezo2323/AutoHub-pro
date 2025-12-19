package com.autohub.autohub.backend.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Rental {
    private int rentalId;
    private Integer userId;  // Changed to Integer to allow null
    private int carId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalAmount;
    private String paymentStatus;
    private String status; // active, completed, cancelled
    private LocalDate createdAt;

    // للعرض في الجدول
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String carName;
    private String carBrand;
    private String carImageUrl;

    // Constructors
    public Rental() {
    }

    public Rental(int userId, int carId, LocalDate startDate, LocalDate endDate,
                  double totalAmount, String paymentStatus, String status) {
        this.userId = userId;
        this.carId = carId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.status = status;
    }

    // Helper Methods
    public long getDaysLeft() {
        if (endDate == null) return 0;
        LocalDate today = LocalDate.now();
        long daysLeft = ChronoUnit.DAYS.between(today, endDate);
        return daysLeft > 0 ? daysLeft : 0;
    }

    public long getDuration() {
        if (startDate == null || endDate == null) return 0;
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public void setDuration(int days) {

    }

    public String getPeriodFormatted() {
        if (startDate == null || endDate == null) return "";
        return "From: " + startDate + "\nTo: " + endDate;
    }

    // Getters and Setters
    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public Integer getUserId() {
        return userId;
    }


    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarImageUrl() {
        return carImageUrl;
    }

    public void setCarImageUrl(String carImageUrl) {
        this.carImageUrl = carImageUrl;
    }

    public void setCustomerId(int customerId) {
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

}
