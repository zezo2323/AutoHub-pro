package com.autohub.autohub.backend.models;

import java.time.LocalDateTime;

public class Comment {
    private int commentId;
    private int userId;
    private int carId;
    private int rating;
    private String commentText;
    private LocalDateTime commentDate;

    // للعرض في الواجهة
    private String userName;
    private String carName;

    // Constructors
    public Comment() {
        this.commentDate = LocalDateTime.now();
    }

    public Comment(int userId, int carId, int rating, String commentText) {
        this.userId = userId;
        this.carId = carId;
        this.rating = rating;
        this.commentText = commentText;
        this.commentDate = LocalDateTime.now();
    }

    // Getters and Setters
    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            this.rating = rating;
        } else {
            this.rating = 5; // default
        }
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public LocalDateTime getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(LocalDateTime commentDate) {
        this.commentDate = commentDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }
}
