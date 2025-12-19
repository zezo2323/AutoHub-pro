package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.Comment;
import com.autohub.autohub.backend.models.CommentDAO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminReviewsController {

    @FXML
    private Label lblAverageRating;
    @FXML
    private Label lblTotalReviews;
    @FXML
    private Label lblFiveStarReviews;
    @FXML
    private HBox avgStarsBox;
    @FXML
    private VBox reviewsListContainer;

    @FXML
    public void initialize() {
        loadStatistics();
        loadReviews();
    }

    /**
     * تحميل الإحصائيات
     */
    private void loadStatistics() {
        List<Comment> allComments = CommentDAO.getAllComments();

        // Total Reviews
        int totalReviews = allComments.size();
        lblTotalReviews.setText(String.valueOf(totalReviews));

        // Average Rating
        double averageRating = 0.0;
        int fiveStarCount = 0;

        if (!allComments.isEmpty()) {
            int totalRating = 0;
            for (Comment comment : allComments) {
                totalRating += comment.getRating();
                if (comment.getRating() == 5) {
                    fiveStarCount++;
                }
            }
            averageRating = (double) totalRating / allComments.size();
        }

        lblAverageRating.setText(String.format("%.1f", averageRating));
        lblFiveStarReviews.setText(String.valueOf(fiveStarCount));

        // عرض النجوم للمتوسط
        displayAverageStars(averageRating);
    }

    /**
     * عرض نجوم المتوسط
     */
    private void displayAverageStars(double rating) {
        avgStarsBox.getChildren().clear();
        int fullStars = (int) rating;

        for (int i = 0; i < fullStars; i++) {
            Label star = new Label("★");
            star.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 20;");
            avgStarsBox.getChildren().add(star);
        }

        for (int i = fullStars; i < 5; i++) {
            Label star = new Label("☆");
            star.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 20;");
            avgStarsBox.getChildren().add(star);
        }
    }

    /**
     * تحميل التعليقات
     */
    private void loadReviews() {
        reviewsListContainer.getChildren().clear();
        reviewsListContainer.setMaxWidth(Double.MAX_VALUE); // ✅ الـ Container يأخد الـ width كامل
        List<Comment> comments = CommentDAO.getAllComments();

        if (comments.isEmpty()) {
            Label noReviews = new Label("No reviews yet.");
            noReviews.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 15; -fx-padding: 40;");
            reviewsListContainer.getChildren().add(noReviews);
            return;
        }

        for (Comment comment : comments) {
            reviewsListContainer.getChildren().add(createReviewCard(comment));
        }
    }


    /**
     * إنشاء كارد التعليق
     */
    private VBox createReviewCard(Comment comment) {
        VBox card = new VBox(16);
        card.setPadding(new javafx.geometry.Insets(24));
        card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; " +
                "-fx-border-color: #e2e8f0; -fx-border-radius: 12; " +
                "-fx-border-width: 1.5; -fx-min-height: 180;");
        card.setPrefHeight(200);
        card.setMaxWidth(Double.MAX_VALUE);

        // Header: Avatar, Name, Date
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);

        // Avatar
        StackPane avatar = new StackPane();
        avatar.setPrefSize(50, 50);
        avatar.setStyle("-fx-background-color: #2563eb; -fx-background-radius: 25;");

        String userName = comment.getUserName() != null ? comment.getUserName() : "User";
        Label avatarInitial = new Label(userName.substring(0, 1).toUpperCase());
        avatarInitial.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 20;");
        avatar.getChildren().add(avatarInitial);

        // Name and Date
        VBox nameDateBox = new VBox(3);
        Label nameLabel = new Label(userName);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #1e293b;");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String formattedDate = comment.getCommentDate().format(formatter);
        Label dateLabel = new Label(formattedDate);
        dateLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13;");

        nameDateBox.getChildren().addAll(nameLabel, dateLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Car name badge
        String carName = comment.getCarName() != null ? comment.getCarName() : "General Review";
        Label carLabel = new Label(carName);
        carLabel.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 13; -fx-font-weight: bold; " +
                "-fx-padding: 6 14 6 14; -fx-background-color: #dbeafe; -fx-background-radius: 6;");

        // Delete Button Only ✅
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                "-fx-background-radius: 6; -fx-padding: 6 14 6 14; -fx-font-size: 13; " +
                "-fx-font-weight: bold; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> handleDeleteComment(comment));

        header.getChildren().addAll(avatar, nameDateBox, spacer, carLabel, deleteBtn);

        // Rating stars
        HBox ratingBox = new HBox(6);
        ratingBox.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < comment.getRating(); i++) {
            Label star = new Label("★");
            star.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 22;");
            ratingBox.getChildren().add(star);
        }
        for (int i = comment.getRating(); i < 5; i++) {
            Label star = new Label("☆");
            star.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 22;");
            ratingBox.getChildren().add(star);
        }

        // Review content
        Label contentLabel = new Label(comment.getCommentText());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-text-fill: #475569; -fx-font-size: 15; -fx-line-spacing: 3;");
        contentLabel.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(header, ratingBox, contentLabel);

        return card;
    }


//    private void handleEditComment(Comment comment, VBox originalCard) {
//        VBox editCard = new VBox(16);
//        editCard.setPadding(new javafx.geometry.Insets(24));
//        editCard.setStyle("-fx-background-color: #fff7ed; -fx-background-radius: 12; " +
//                "-fx-border-color: #f59e0b; -fx-border-radius: 12; " +
//                "-fx-border-width: 2; -fx-min-height: 180;");
//
//        Label editLabel = new Label("Edit Review");
//        editLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
//
//        HBox editStarsBox = new HBox(8);
//        editStarsBox.setAlignment(Pos.CENTER_LEFT);
//        Label[] editStars = new Label[5];
//        final int[] newRating = {comment.getRating()};
//
//        for (int i = 0; i < 5; i++) {
//            final int starValue = i + 1;
//            Label star = new Label(starValue <= newRating[0] ? "★" : "☆");
//            star.setStyle("-fx-text-fill: " + (starValue <= newRating[0] ? "#f59e0b" : "#cbd5e1") +
//                    "; -fx-font-size: 24; -fx-cursor: hand;");
//
//            star.setOnMouseClicked(e -> {
//                newRating[0] = starValue;
//                for (int j = 0; j < 5; j++) {
//                    editStars[j].setText((j + 1) <= newRating[0] ? "★" : "☆");
//                    editStars[j].setStyle("-fx-text-fill: " +
//                            ((j + 1) <= newRating[0] ? "#f59e0b" : "#cbd5e1") +
//                            "; -fx-font-size: 24; -fx-cursor: hand;");
//                }
//            });
//
//            editStars[i] = star;
//            editStarsBox.getChildren().add(star);
//        }
//
//        TextArea editTextArea = new TextArea(comment.getCommentText());
//        editTextArea.setWrapText(true);
//        editTextArea.setPrefRowCount(3);
//        editTextArea.setPrefHeight(100);
//        editTextArea.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; " +
//                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12; -fx-font-size: 14;");
//
//        HBox buttonsBox = new HBox(10);
//        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
//
//        Button saveBtn = new Button("Save");
//        saveBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; " +
//                "-fx-background-radius: 6; -fx-padding: 8 20 8 20; -fx-font-size: 14; " +
//                "-fx-font-weight: bold; -fx-cursor: hand;");
//
//        saveBtn.setOnAction(e -> {
//            String newText = editTextArea.getText().trim();
//            if (newText.isEmpty()) {
//                Alert alert = new Alert(Alert.AlertType.WARNING);
//                alert.setTitle("Warning");
//                alert.setHeaderText("Empty Review");
//                alert.setContentText("Review text cannot be empty");
//                alert.showAndWait();
//                return;
//            }
//
//            // تحديث البيانات في الـ Object
//            comment.setRating(newRating[0]);
//            comment.setCommentText(newText);
//
//            // حفظ في الداتابيز ✅
//            boolean success = CommentDAO.updateComment(comment);
//
//            if (success) {
//                // تحديث الإحصائيات ✅
//                loadStatistics();
//
//                // إرجاع الكارد المحدّث
//                VBox newCard = createReviewCard(comment);
//                int index = reviewsListContainer.getChildren().indexOf(editCard);
//                reviewsListContainer.getChildren().set(index, newCard);
//
//                System.out.println("✅ Review updated successfully!");
//            } else {
//                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
//                errorAlert.setTitle("Error");
//                errorAlert.setHeaderText("Failed to update review");
//                errorAlert.showAndWait();
//            }
//        });
//
//        Button cancelBtn = new Button("Cancel");
//        cancelBtn.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; " +
//                "-fx-background-radius: 6; -fx-padding: 8 20 8 20; -fx-font-size: 14; " +
//                "-fx-font-weight: bold; -fx-cursor: hand;");
//
//        cancelBtn.setOnAction(e -> {
//            int index = reviewsListContainer.getChildren().indexOf(editCard);
//            reviewsListContainer.getChildren().set(index, originalCard);
//        });
//
//        buttonsBox.getChildren().addAll(saveBtn, cancelBtn);
//        editCard.getChildren().addAll(editLabel, editStarsBox, editTextArea, buttonsBox);
//
//        int index = reviewsListContainer.getChildren().indexOf(originalCard);
//        reviewsListContainer.getChildren().set(index, editCard);
//    }


    /**
     * حذف تعليق
     */
    private void handleDeleteComment(Comment comment) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Review");
        confirmAlert.setHeaderText("Delete this review?");
        confirmAlert.setContentText("This action cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = CommentDAO.deleteComment(comment.getCommentId());

                if (success) {
                    // تحديث الإحصائيات والكاردات ✅
                    loadStatistics();
                    loadReviews();
                    System.out.println("✅ Review deleted successfully");
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Failed to delete review");
                    errorAlert.showAndWait();
                }
            }
        });
    }


}
