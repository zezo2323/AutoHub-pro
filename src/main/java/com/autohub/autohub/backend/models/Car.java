package com.autohub.autohub.backend.models;

public class Car {
    private int carId;
    private String carName;
    private String brand;           // ← جديد
    private String model;
    private String year;
    private double pricePerDay;
    private int seats;              // ← جديد
    private String transmission;    // ← جديد
    private String fuelType;        // ← جديد
    private String imageUrl;
    private String status;
    private String description;
    private String features;        // ← جديد

    // Constructor فاضي
    public Car() {
    }

    // Constructor كامل (حدثه)
    public Car(int carId, String carName, String brand, String model, String year,
               double pricePerDay, int seats, String transmission, String fuelType,
               String imageUrl, String status, String description, String features) {
        this.carId = carId;
        this.carName = carName;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.pricePerDay = pricePerDay;
        this.seats = seats;
        this.transmission = transmission;
        this.fuelType = fuelType;
        this.imageUrl = imageUrl;
        this.status = status;
        this.description = description;
        this.features = features;
    }

    // Getters & Setters الموجودة + الجديدة

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }
}
