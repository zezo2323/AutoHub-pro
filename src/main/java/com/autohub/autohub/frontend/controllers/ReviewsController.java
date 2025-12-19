package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.Car;
import com.autohub.autohub.backend.models.CarDAO;
import com.autohub.autohub.backend.models.Comment;
import com.autohub.autohub.backend.models.CommentDAO;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReviewsController {

    // User ID مؤقت (هنستخدم user_id = 1 للتجربة)
    private final int CURRENT_USER_ID = 1;
    private final Label[] starLabels = new Label[5];
    @FXML
    private VBox rootContainer;
    @FXML
    private ComboBox<String> carCombo;
    @FXML
    private HBox starsBox;
    @FXML
    private TextArea reviewArea;
    @FXML
    private Button postBtn;
    @FXML
    private Label formMessage;
    @FXML
    private VBox reviewsListContainer;
    @FXML
    private VBox writeCard;
    // Rating
    private int currentRating = 5;
    private int hoverRating = 0;

    @FXML
    public void initialize() {
        setupCarComboBox();
        buildStars();
        setupPostButton();
        refreshReviews();
    }

    private void setupCarComboBox() {
        carCombo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(carCombo, Priority.ALWAYS);

        // جلب السيارات من الداتابيز
        carCombo.getItems().clear();
        carCombo.getItems().add("Choose a car");

        List<Car> cars = CarDAO.getCars();
        for (Car car : cars) {
            // عرض: Brand + Model (مثلاً: Mercedes-Benz E-Class)
            String displayName = car.getBrand() + " " + car.getModel();
            carCombo.getItems().add(displayName);
        }

        carCombo.getSelectionModel().select(0);
    }

    private void buildStars() {
        starsBox.getChildren().clear();
        starsBox.setSpacing(8);

        for (int i = 0; i < 5; i++) {
            final int starValue = i + 1;
            Label star = new Label("★");
            star.setFont(Font.font(24)); // أكبر شوية
            star.setCursor(javafx.scene.Cursor.HAND);
            star.setStyle("-fx-text-fill: #cbd5e1;"); // لون رمادي فاتح

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

            if (hoverRating > 0) {
                if (starNumber <= hoverRating) {
                    star.setStyle("-fx-text-fill: #fbbf24;"); // أصفر للـ hover
                    star.setText("★");
                } else {
                    star.setStyle("-fx-text-fill: #cbd5e1;");
                    star.setText("☆");
                }
            } else {
                if (starNumber <= currentRating) {
                    star.setStyle("-fx-text-fill: #f59e0b;"); // برتقالي للتقييم المختار
                    star.setText("★");
                } else {
                    star.setStyle("-fx-text-fill: #cbd5e1;");
                    star.setText("☆");
                }
            }
        }
    }


    private void setupPostButton() {
        postBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(postBtn, Priority.ALWAYS);
        postBtn.setOnAction(e -> handlePost());
    }

    private void handlePost() {
        String carSelection = carCombo.getValue();
        String content = reviewArea.getText().trim();

        // التحقق من البيانات
        if (carSelection == null || "Choose a car".equals(carSelection)) {
            showMessage("Please select a car", "red");
            return;
        }

        if (content.isEmpty()) {
            showMessage("Please write your review", "red");
            return;
        }

        // جلب car_id من السيارة المختارة
        List<Car> cars = CarDAO.getCars();
        int selectedCarId = -1;

        for (Car car : cars) {
            String displayName = car.getBrand() + " " + car.getModel();
            if (displayName.equals(carSelection)) {
                selectedCarId = car.getCarId();
                break;
            }
        }

        if (selectedCarId == -1) {
            showMessage("Error: Car not found", "red");
            return;
        }

        // إنشاء التعليق
        Comment comment = new Comment(CURRENT_USER_ID, selectedCarId, currentRating, content);

        // حفظ في الداتابيز
        boolean success = CommentDAO.addComment(comment);

        if (success) {
            showMessage("Review posted successfully!", "#28a745");
            refreshReviews();

            // مسح النص فقط
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
        reviewsListContainer.setSpacing(20); // زودنا المسافة

        // جلب التعليقات من الداتابيز
        List<Comment> comments = CommentDAO.getAllComments();

        if (comments.isEmpty()) {
            Label noReviews = new Label("No reviews yet. Be the first to share your experience!");
            noReviews.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 15; -fx-padding: 40;");
            reviewsListContainer.getChildren().add(noReviews);
            return;
        }

        for (Comment comment : comments) {
            reviewsListContainer.getChildren().add(createReviewCard(comment));
        }
    }


    private VBox createReviewCard(Comment comment) {
        VBox card = new VBox(16);
        card.setPadding(new javafx.geometry.Insets(24));
        card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; " +
                "-fx-border-color: #e2e8f0; -fx-border-radius: 12; " +
                "-fx-border-width: 1.5; -fx-min-height: 180;");
        card.setPrefHeight(200);

        // Header: Avatar, Name, Date, Actions
        HBox header = new HBox(14);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Avatar
        StackPane avatar = new StackPane();
        avatar.setPrefSize(50, 50);
        avatar.setStyle("-fx-background-color: #2563eb; -fx-background-radius: 25;");

        String userName = comment.getUserName() != null ? comment.getUserName() : "User";
        Label avatarInitial = new Label(userName.substring(0, 1).toUpperCase());
        avatarInitial.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 20;");
        avatar.getChildren().add(avatarInitial);

        // Name and Date
        VBox nameDateBox = new VBox(4);
        Label nameLabel = new Label(userName);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 17; -fx-text-fill: #1e293b;");

        // تنسيق التاريخ
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String formattedDate = comment.getCommentDate().format(formatter);
        Label dateLabel = new Label(formattedDate);
        dateLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14;");

        nameDateBox.getChildren().addAll(nameLabel, dateLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Car name badge
        String carName = comment.getCarName() != null ? comment.getCarName() : "General Review";
        Label carLabel = new Label(carName);
        carLabel.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 14; -fx-font-weight: bold; " +
                "-fx-padding: 8 16 8 16; -fx-background-color: #dbeafe; -fx-background-radius: 8;");

        // Action Buttons
        HBox actionsBox = new HBox(8);
        actionsBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        // Edit Button
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; " +
                "-fx-background-radius: 6; -fx-padding: 6 14 6 14; -fx-font-size: 13; " +
                "-fx-font-weight: bold; -fx-cursor: hand;");
        editBtn.setOnAction(e -> handleEditComment(comment, card));

        // Delete Button
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                "-fx-background-radius: 6; -fx-padding: 6 14 6 14; -fx-font-size: 13; " +
                "-fx-font-weight: bold; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> handleDeleteComment(comment));

        actionsBox.getChildren().addAll(editBtn, deleteBtn);

        header.getChildren().addAll(avatar, nameDateBox, spacer, carLabel, actionsBox);

        // Rating stars
        HBox ratingBox = new HBox(6);
        ratingBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
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

    /**
     * تعديل تعليق - يحول الكارد لـ Edit Mode
     */
    private void handleEditComment(Comment comment, VBox originalCard) {
        // إنشاء Edit Card
        VBox editCard = new VBox(16);
        editCard.setPadding(new javafx.geometry.Insets(24));
        editCard.setStyle("-fx-background-color: #fff7ed; -fx-background-radius: 12; " +
                "-fx-border-color: #f59e0b; -fx-border-radius: 12; " +
                "-fx-border-width: 2; -fx-min-height: 180;");

        // Header
        Label editLabel = new Label("Edit Review");
        editLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        // Rating stars للتعديل
        HBox editStarsBox = new HBox(8);
        editStarsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label[] editStars = new Label[5];
        final int[] newRating = {comment.getRating()};

        for (int i = 0; i < 5; i++) {
            final int starValue = i + 1;
            Label star = new Label(starValue <= newRating[0] ? "★" : "☆");
            star.setStyle("-fx-text-fill: " + (starValue <= newRating[0] ? "#f59e0b" : "#cbd5e1") +
                    "; -fx-font-size: 24; -fx-cursor: hand;");

            star.setOnMouseClicked(e -> {
                newRating[0] = starValue;
                for (int j = 0; j < 5; j++) {
                    editStars[j].setText((j + 1) <= newRating[0] ? "★" : "☆");
                    editStars[j].setStyle("-fx-text-fill: " +
                            ((j + 1) <= newRating[0] ? "#f59e0b" : "#cbd5e1") +
                            "; -fx-font-size: 24; -fx-cursor: hand;");
                }
            });

            editStars[i] = star;
            editStarsBox.getChildren().add(star);
        }

        // TextArea للتعديل
        TextArea editTextArea = new TextArea(comment.getCommentText());
        editTextArea.setWrapText(true);
        editTextArea.setPrefRowCount(3);
        editTextArea.setPrefHeight(100);
        editTextArea.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; " +
                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12; -fx-font-size: 14;");

        // Action Buttons
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; " +
                "-fx-background-radius: 6; -fx-padding: 8 20 8 20; -fx-font-size: 14; " +
                "-fx-font-weight: bold; -fx-cursor: hand;");

        saveBtn.setOnAction(e -> {
            String newText = editTextArea.getText().trim();
            if (newText.isEmpty()) {
                showMessage("Review text cannot be empty", "red");
                return;
            }

            // تحديث البيانات
            comment.setRating(newRating[0]);
            comment.setCommentText(newText);

            // TODO: حفظ في الداتابيز (هنعملها بعدين)
            // boolean success = CommentDAO.updateComment(comment);

            showMessage("Review updated successfully!", "#22c55e");

            // استبدال Edit Card بالكارد العادي المحدث
            VBox newCard = createReviewCard(comment);
            int index = reviewsListContainer.getChildren().indexOf(editCard);
            reviewsListContainer.getChildren().set(index, newCard);
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; " +
                "-fx-background-radius: 6; -fx-padding: 8 20 8 20; -fx-font-size: 14; " +
                "-fx-font-weight: bold; -fx-cursor: hand;");

        cancelBtn.setOnAction(e -> {
            // إرجاع الكارد الأصلي
            int index = reviewsListContainer.getChildren().indexOf(editCard);
            reviewsListContainer.getChildren().set(index, originalCard);
        });

        buttonsBox.getChildren().addAll(saveBtn, cancelBtn);

        editCard.getChildren().addAll(editLabel, editStarsBox, editTextArea, buttonsBox);

        // استبدال الكارد الأصلي بـ Edit Card
        int index = reviewsListContainer.getChildren().indexOf(originalCard);
        reviewsListContainer.getChildren().set(index, editCard);
    }

    /**
     * حذف تعليق
     */
    private void handleDeleteComment(Comment comment) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Review");
        confirmAlert.setHeaderText("Are you sure?");
        confirmAlert.setContentText("This review will be permanently deleted.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = CommentDAO.deleteComment(comment.getCommentId());

                if (success) {
                    showMessage("Review deleted successfully!", "#22c55e");
                    refreshReviews();
                } else {
                    showMessage("Error deleting review", "red");
                }
            }
        });
    }

}
