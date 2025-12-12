package com.autohub.autohub.backend.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MockReviewService {

    public static class ReviewItem {
        public int id;
        public String carName;
        public String author;
        public String content;
        public int rating;
        public String createdAt;
        public String adminReply;

        public ReviewItem(int id, String carName, String author, String content, int rating, String createdAt, String adminReply) {
            this.id = id;
            this.carName = carName;
            this.author = author;
            this.content = content;
            this.rating = rating;
            this.createdAt = createdAt;
            this.adminReply = adminReply;
        }
    }

    private final List<ReviewItem> store = new ArrayList<>();
    private int nextId = 1;

    public MockReviewService() {
        addReview("BMW 5 Series", "John Doe", "Amazing car! Very comfortable and smooth ride. Highly recommend!", 5);
        addReview("Mercedes-Benz E-Class", "Sarah Johnson", "Good experience overall.", 4);
    }

    public synchronized List<ReviewItem> getAllReviews() {
        List<ReviewItem> copy = new ArrayList<>(store);
        Collections.reverse(copy);
        return copy;
    }

    public synchronized boolean addReview(String carName, String author, String content, int rating) {
        if (content == null || content.trim().isEmpty()) return false;
        String date = LocalDate.now().toString();
        ReviewItem item = new ReviewItem(nextId++, carName, author == null ? "Anonymous" : author, content, rating, date, null);
        store.add(item);
        return true;
    }
}
