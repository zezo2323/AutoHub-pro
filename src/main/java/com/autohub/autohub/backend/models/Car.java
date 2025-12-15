package com.autohub.autohub.backend.models;

public class Car {
    private String name;
    private String type;
    private String year;
    private String fuel;
    private String seats;
    private String status;
    private String pricePerDay;

    public Car() {
    }

    public Car(String name, String type, String year,
               String fuel, String seats, String status,
               String pricePerDay) {
        this.name = name;
        this.type = type;
        this.year = year;
        this.fuel = fuel;
        this.seats = seats;
        this.status = status;
        this.pricePerDay = pricePerDay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(String pricePerDay) {
        this.pricePerDay = pricePerDay;
    }
}
