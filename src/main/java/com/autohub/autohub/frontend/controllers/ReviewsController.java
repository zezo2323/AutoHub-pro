package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.services.MockCarService;
import com.autohub.autohub.backend.services.MockReviewService;
import com.autohub.autohub.backend.services.MockReviewService.ReviewItem;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.util.List;

public class ReviewsController {

    @FXML private VBox rootContainer;
    @FXML private ComboBox<String> carCombo;
    @FXML private HBox starsBox;
    @FXML private TextArea reviewArea;
    @FXML private Button postBtn;
    @FXML private Label formMessage;
    @FXML private VBox reviewsListContainer;
    @FXML private VBox writeCard;

    // Services
    private final MockCarService carService = new MockCarService();
    private final MockReviewService reviewService = new MockReviewService();

    // Rating
    private int currentRating = 5; // Start with 5 stars selected
    private int hoverRating = 0;
    private Label[] starLabels = new Label[5];

    @FXML
    public void initialize() {
        setupCarComboBox();
        buildStars();
        setupPostButton();
        refreshReviews();
        styleComponents();
    }

    private void setupCarComboBox() {
        // Make it expand horizontally
        carCombo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(carCombo, Priority.ALWAYS);

        // Add cars to dropdown
        carCombo.getItems().clear();
        carCombo.getItems().add("Choose a car");
        carCombo.getItems().addAll(carService.getAllCarNames());
        carCombo.getSelectionModel().select(0);

        // Prefer CSS for visuals; apply a style-class in case
        carCombo.getStyleClass().add("custom-combo");
    }

    private void buildStars() {
        starsBox.getChildren().clear();
        starsBox.setSpacing(8);

        for (int i = 0; i < 5; i++) {
            final int starValue = i + 1;
            Label star = new Label("★"); // filled star glyph
            star.getStyleClass().add("star"); // base class
            star.setFont(Font.font(22)); // size tuned
            star.setCursor(javafx.scene.Cursor.HAND);

            // Add hover animation
            star.setOnMouseEntered(e -> {
                hoverRating = starValue;
                updateStarsVisual();
                animateStar(star, true);
            });

            star.setOnMouseExited(e -> {
                hoverRating = 0;
                updateStarsVisual();
                animateStar(star, false);
            });

            star.setOnMouseClicked(e -> {
                currentRating = starValue;
                hoverRating = 0;
                updateStarsVisual();
            });

            starLabels[i] = star;
            starsBox.getChildren().add(star);
        }
        updateStarsVisual();
    }

    private void animateStar(Label star, boolean enlarge) {
        ScaleTransition st = new ScaleTransition(Duration.millis(140), star);
        st.setFromX(star.getScaleX());
        st.setFromY(star.getScaleY());
        st.setToX(enlarge ? 1.22 : 1.0);
        st.setToY(enlarge ? 1.22 : 1.0);
        st.play();
    }

    private void updateStarsVisual() {
        for (int i = 0; i < 5; i++) {
            Label star = starLabels[i];
            int starNumber = i + 1;

            // remove filled class first
            star.getStyleClass().remove("star-filled");

            if (hoverRating > 0) {
                // Preview while hovering
                if (starNumber <= hoverRating) {
                    if (!star.getStyleClass().contains("star-filled")) star.getStyleClass().add("star-filled");
                    star.setText("★");
                } else {
                    star.setText("☆");
                }
            } else {
                // Normal mode
                if (starNumber <= currentRating) {
                    if (!star.getStyleClass().contains("star-filled")) star.getStyleClass().add("star-filled");
                    star.setText("★");
                } else {
                    star.setText("☆");
                }
            }
        }
    }

    private void setupPostButton() {
        // Make post button full width
        postBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(postBtn, Priority.ALWAYS);

        postBtn.setOnAction(e -> handlePost());

        // Use CSS classes for hover/pressed visuals — keep no heavy inline styles
        postBtn.getStyleClass().add("btn-primary");
    }

    private void handlePost() {
        String carName = carCombo.getValue();
        String content = reviewArea.getText().trim();

        if (carName == null || "Choose a car".equals(carName)) {
            showMessage("Please select a car", "red");
            return;
        }

        if (content.isEmpty()) {
            showMessage("Please write your review", "red");
            return;
        }

        boolean success = reviewService.addReview(
                carName.equals("Choose a car") ? null : carName,
                "John Doe", // placeholder username
                content,
                currentRating
        );

        if (success) {
            showMessage("Review posted successfully!", "#28a745");
            refreshReviews();

            // Clear text area and keep rating and car selection
            reviewArea.clear();
            reviewArea.setPromptText("...Share your experience");
        } else {
            showMessage("Error posting review", "red");
        }
    }

    private void showMessage(String text, String color) {
        formMessage.setText(text);
        formMessage.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 14;");
    }

    private void refreshReviews() {
        reviewsListContainer.getChildren().clear();
        reviewsListContainer.setSpacing(15);

        List<ReviewItem> reviews = reviewService.getAllReviews();

        if (reviews.isEmpty()) {
            Label noReviews = new Label("No reviews yet. Be the first to share your experience!");
            noReviews.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14; -fx-alignment: center; -fx-padding: 20;");
            reviewsListContainer.getChildren().add(noReviews);
            return;
        }

        for (ReviewItem review : reviews) {
            reviewsListContainer.getChildren().add(createReviewCard(review));
        }
    }

    private VBox createReviewCard(ReviewItem review) {
        VBox card = new VBox(12);
        card.setPadding(new javafx.geometry.Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-border-color: #e9ecef; -fx-border-radius: 12; " +
                "-fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        card.setMaxWidth(800);

        // Header: Avatar, Name, Date
        HBox header = new HBox(12);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Avatar
        StackPane avatar = new StackPane();
        avatar.setPrefSize(40, 40);
        avatar.setStyle("-fx-background-color: #4a6fa5; -fx-background-radius: 20;");
        Label avatarInitial = new Label(review.author.substring(0, 1).toUpperCase());
        avatarInitial.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
        avatar.getChildren().add(avatarInitial);

        // Name and Date
        VBox nameDateBox = new VBox(2);
        Label nameLabel = new Label(review.author);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #333;");

        Label dateLabel = new Label(review.createdAt);
        dateLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 13;");

        nameDateBox.getChildren().addAll(nameLabel, dateLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Car name
        Label carLabel = new Label(review.carName != null ? review.carName : "General Review");
        carLabel.setStyle("-fx-text-fill: #007bff; -fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 4 10 4 10; " +
                "-fx-background-color: #e7f1ff; -fx-background-radius: 6;");

        header.getChildren().addAll(avatar, nameDateBox, spacer, carLabel);

        // Rating stars
        HBox ratingBox = new HBox(4);
        ratingBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        for (int i = 0; i < review.rating; i++) {
            Label star = new Label("★");
            star.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 18;");
            ratingBox.getChildren().add(star);
        }
        for (int i = review.rating; i < 5; i++) {
            Label star = new Label("☆");
            star.setStyle("-fx-text-fill: #d1d5db; -fx-font-size: 18;");
            ratingBox.getChildren().add(star);
        }

        // Review content
        TextArea contentArea = new TextArea(review.content);
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setPrefRowCount(3);
        contentArea.setPrefHeight(80);
        contentArea.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; " +
                "-fx-border-radius: 8; -fx-padding: 12; -fx-font-size: 14; " +
                "-fx-control-inner-background: #f8f9fa;");

        card.getChildren().addAll(header, ratingBox, contentArea);

        return card;
    }

    private void styleComponents() {
        // TextArea styling - prefer CSS, but apply safe defaults
        reviewArea.setPrefRowCount(3);
        reviewArea.setPrefHeight(100);
        reviewArea.getStyleClass().add("review-textarea");
        reviewArea.setPromptText("...Share your experience");

        // Post button styling: rely on CSS class
        postBtn.getStyleClass().add("btn-primary");
    }
}