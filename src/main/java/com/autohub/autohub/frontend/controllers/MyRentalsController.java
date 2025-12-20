package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.Rental;
import com.autohub.autohub.backend.models.RentalDAO;
import com.autohub.autohub.backend.models.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MyRentalsController implements Initializable {

    @FXML
    private HBox statsRow;
    @FXML
    private TableView<Rental> rentalsTable;
    @FXML
    private TableColumn<Rental, Void> colActions;
    @FXML
    private TableColumn<Rental, Void> colDaysLeft;
    @FXML
    private TableColumn<Rental, Void> colStatus;
    @FXML
    private TableColumn<Rental, Void> colAmount;
    @FXML
    private TableColumn<Rental, Void> colDuration;
    @FXML
    private TableColumn<Rental, Void> colPeriod;
    @FXML
    private TableColumn<Rental, Void> colCar;
    @FXML
    private TableColumn<Rental, Void> colRentalId;

    // User ID اللي مسجل (هتجيبه من الـ Session لاحقاً)
    // استخدام الـ Session بدل القيمة المؤقتة
    private int getCurrentUserId() {
        return SessionManager.getCurrentUserId();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupStatsCards();
        setupTableColumns();
        loadMyRentals();

        rentalsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupStatsCards() {
        // جلب الإحصائيات الخاصة باليوزر المسجل فقط من الـ Session
        int userId = getCurrentUserId();
        double totalSpent = RentalDAO.getTotalRevenueByUserId(userId);
        int cancelledCount = RentalDAO.getCancelledRentalsCountByUserId(userId);
        int completedCount = RentalDAO.getCompletedRentalsCountByUserId(userId);
        int activeCount = RentalDAO.getActiveRentalsCountByUserId(userId);

        // إنشاء الكروت
        VBox card1 = createStatCard(String.format("$%.0f", totalSpent), "Total Spent",
                "#10b981", "fas-dollar-sign");
        VBox card2 = createStatCard(String.valueOf(activeCount), "Active",
                "#8b5cf6", "fas-calendar");
        VBox card3 = createStatCard(String.valueOf(completedCount), "Completed",
                "#10b981", "fas-check-circle");
        VBox card4 = createStatCard(String.valueOf(cancelledCount), "Cancelled",
                "#ef4444", "fas-times-circle");

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);

        statsRow.getChildren().addAll(card1, card2, card3, card4);
    }

    /**
     * الحصول على ID اليوزر من الـ Session
     */


    private VBox createStatCard(String value, String label, String color, String icon) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-padding: 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 3);");
        card.setMaxWidth(Double.MAX_VALUE);

        FontIcon iconNode = new FontIcon(icon);
        iconNode.setIconSize(40);
        iconNode.setIconColor(Color.valueOf(color));
        iconNode.setOpacity(0.15);

        Label labelLbl = new Label(label);
        labelLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: normal;");

        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        HBox content = new HBox(16);
        content.setAlignment(Pos.CENTER_LEFT);
        VBox textBox = new VBox(4, labelLbl, valueLbl);
        content.getChildren().addAll(textBox, iconNode);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        card.getChildren().add(content);
        return card;
    }

    private void setupTableColumns() {
        // Rental ID Column
        colRentalId.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Rental rental = getTableView().getItems().get(getIndex());
                    Label label = new Label("R" + String.format("%03d", rental.getRentalId()));
                    label.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #64748b;");
                    setGraphic(label);
                }
            }
        });

        // Car Column (مع صورة عالية الجودة)
        colCar.setCellFactory(param -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            private final StackPane placeholder = new StackPane();
            private final Rectangle clip = new Rectangle(70, 70);

            {
                imageView.setFitWidth(70);
                imageView.setFitHeight(70);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                clip.setArcWidth(8);
                clip.setArcHeight(8);
                imageView.setClip(clip);

                placeholder.setPrefSize(70, 70);
                placeholder.setStyle("-fx-background-color: #e5e7eb; -fx-background-radius: 8;");
                FontIcon carIcon = new FontIcon("fas-car");
                carIcon.setIconSize(32);
                carIcon.setIconColor(Color.web("#9ca3af"));
                placeholder.getChildren().add(carIcon);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Rental rental = getTableView().getItems().get(getIndex());

                    VBox textBox = new VBox(4);
                    Label carName = new Label(rental.getCarName() != null ? rental.getCarName() : "N/A");
                    carName.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #0f172a;");
                    carName.setWrapText(false);
                    carName.setMaxWidth(140);

                    Label carBrand = new Label(rental.getCarBrand() != null ? rental.getCarBrand() : "");
                    carBrand.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

                    textBox.getChildren().addAll(carName, carBrand);

                    HBox container = new HBox(12);
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.getChildren().addAll(placeholder, textBox);

                    setPadding(new Insets(12, 8, 12, 8));
                    setGraphic(container);

                    // تحميل الصورة
                    String imageUrl = rental.getCarImageUrl();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        javafx.concurrent.Task<Image> loadTask = new javafx.concurrent.Task<>() {
                            @Override
                            protected Image call() {
                                try {
                                    return new Image(imageUrl, 140, 140, true, true, false);
                                } catch (Exception e) {
                                    return null;
                                }
                            }
                        };

                        loadTask.setOnSucceeded(event -> {
                            Image img = loadTask.getValue();
                            if (img != null && !img.isError()) {
                                imageView.setImage(img);
                                container.getChildren().set(0, imageView);
                            }
                        });

                        Thread thread = new Thread(loadTask);
                        thread.setDaemon(true);
                        thread.start();
                    }
                }
            }
        });

        // Period Column
        colPeriod.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Rental rental = getTableView().getItems().get(getIndex());

                    VBox box = new VBox(4);
                    Label from = new Label("From: " + rental.getStartDate());
                    Label to = new Label("To: " + rental.getEndDate());

                    from.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
                    to.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

                    box.getChildren().addAll(from, to);
                    setGraphic(box);
                }
            }
        });

        // Duration Column
        colDuration.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Rental rental = getTableView().getItems().get(getIndex());
                    Label label = new Label(rental.getDuration() + " days");
                    label.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
                    setGraphic(label);
                }
            }
        });

        // Amount Column
        colAmount.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Rental rental = getTableView().getItems().get(getIndex());
                    Label label = new Label(String.format("$%.0f", rental.getTotalAmount()));
                    label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #10b981;");
                    setGraphic(label);
                }
            }
        });

        // Status Column
        colStatus.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Rental rental = getTableView().getItems().get(getIndex());
                    String status = rental.getStatus();

                    Label statusLabel = new Label(status);
                    String bgColor;

                    if (status.equalsIgnoreCase("active")) {
                        bgColor = "#0284c7";
                    } else if (status.equalsIgnoreCase("completed")) {
                        bgColor = "#10b981";
                    } else {
                        bgColor = "#ef4444";
                    }

                    statusLabel.setStyle("-fx-background-color: " + bgColor + "; " +
                            "-fx-text-fill: white; -fx-padding: 6 12; " +
                            "-fx-background-radius: 20; -fx-font-size: 12px; " +
                            "-fx-font-weight: bold;");

                    setGraphic(statusLabel);
                }
            }
        });

        // Days Left Column
        colDaysLeft.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Rental rental = getTableView().getItems().get(getIndex());
                    long daysLeft = rental.getDaysLeft();

                    Label label = new Label(daysLeft + " days");
                    String color = daysLeft == 0 ? "#dc2626" : "#0f172a";
                    label.setStyle("-fx-font-size: 13px; -fx-text-fill: " + color + ";");

                    setGraphic(label);
                }
            }
        });

        // Actions Column
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button();
            private final Button cancelBtn = new Button();
            private final HBox container = new HBox(8);

            {
                // View Button
                FontIcon eyeIcon = new FontIcon("fas-eye");
                eyeIcon.setIconSize(14);
                eyeIcon.setIconColor(Color.web("#0284c7"));
                viewBtn.setGraphic(eyeIcon);
                viewBtn.setStyle("-fx-background-color: #dbeafe; -fx-border-width: 0; " +
                        "-fx-padding: 8; -fx-background-radius: 6; -fx-cursor: hand;");

                // Cancel Button
                Label cancelLabel = new Label("Cancel");
                cancelLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px;");
                cancelBtn.setGraphic(cancelLabel);
                cancelBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #dc2626; " +
                        "-fx-border-width: 1; -fx-padding: 6 12; -fx-background-radius: 6; " +
                        "-fx-border-radius: 6; -fx-cursor: hand;");

                container.setAlignment(Pos.CENTER);
                container.getChildren().addAll(viewBtn, cancelBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Rental rental = getTableView().getItems().get(getIndex());

                    viewBtn.setOnAction(e -> handleViewInvoice(rental));
                    cancelBtn.setOnAction(e -> handleCancelRental(rental));

                    // إخفاء Cancel لو ملغي أو مكتمل
                    if ("cancelled".equalsIgnoreCase(rental.getStatus()) ||
                            "completed".equalsIgnoreCase(rental.getStatus())) {
                        cancelBtn.setVisible(false);
                        cancelBtn.setManaged(false);
                    } else {
                        cancelBtn.setVisible(true);
                        cancelBtn.setManaged(true);
                    }

                    setGraphic(container);
                }
            }
        });
    }

    private void loadMyRentals() {
        // جلب إيجارات اليوزر المسجل فقط من الـ Session
        int userId = getCurrentUserId();
        List<Rental> rentals = RentalDAO.getRentalsByUserId(userId);
        rentalsTable.getItems().setAll(rentals);
        System.out.println("✅ Loaded " + rentals.size() + " rentals for user " + userId);
    }


    private void handleViewInvoice(Rental rental) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/invoice.fxml")
            );
            javafx.scene.Parent invoiceRoot = loader.load();

            InvoiceController invoiceController = loader.getController();
            invoiceController.setRental(rental);

            javafx.stage.Stage invoiceStage = new javafx.stage.Stage();
            invoiceStage.setTitle("Invoice - R" + String.format("%03d", rental.getRentalId()));
            invoiceStage.setScene(new javafx.scene.Scene(invoiceRoot));
            invoiceStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            invoiceStage.setResizable(false);

            invoiceStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load invoice");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleCancelRental(Rental rental) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Rental");
        confirm.setHeaderText("Cancel Rental #" + rental.getRentalId() + "?");
        confirm.setContentText("Are you sure you want to cancel this rental?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = RentalDAO.cancelRental(rental.getRentalId());

                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("✅ Rental Cancelled!");
                    alert.showAndWait();

                    loadMyRentals();
                    statsRow.getChildren().clear();
                    setupStatsCards();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("❌ Error cancelling rental");
                    alert.showAndWait();
                }
            }
        });
    }
}
