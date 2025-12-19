package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.Rental;
import com.autohub.autohub.backend.services.MockRentalService;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MyRentalsController {

    @FXML private TableView<Rental> rentalsTable;
    @FXML private TableColumn<Rental, Void> colActions;
    @FXML private TableColumn<Rental, Integer> colDays;
    @FXML private TableColumn<Rental, String> colStatus;
    @FXML private TableColumn<Rental, Double> colAmount;
    @FXML private TableColumn<Rental, Integer> colDuration;
    @FXML private TableColumn<Rental, String> colPeriod;
    @FXML private TableColumn<Rental, String> colCar;
    @FXML private TableColumn<Rental, String> colId;

    @FXML private VBox totalCard;
    @FXML private VBox cancelledCard;
    @FXML private VBox completedCard;
    @FXML private VBox activeCard;

    private ObservableList<Rental> rentals;

    @FXML
    public void initialize() {
        rentalsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        rentals = MockRentalService.getUserRentals();
        rentalsTable.setItems(rentals);

        setupTableColumns();

        rentalsTable.refresh();
        Platform.runLater(() -> rentalsTable.refresh());

        updateSummaryCards();
    }

    private void setupTableColumns() {
        // Actions - 👁 + Cancel (مع تغيير النص والشكل بعد الإلغاء)
        colActions.setCellFactory(param -> new TableCell<Rental, Void>() {
            private final Button viewBtn = new Button("👁");
            private final Button cancelBtn = new Button("Cancel");
            private final HBox box = new HBox(15, viewBtn, cancelBtn);

            {
                viewBtn.getStyleClass().add("btn-view");
                cancelBtn.getStyleClass().add("btn-cancel");
                box.setAlignment(Pos.CENTER);

                viewBtn.setOnAction(e -> {
                    Rental rental = getTableRow().getItem();
                    if (rental != null) viewInvoice(rental);
                });

                cancelBtn.setOnAction(e -> {
                    Rental rental = getTableRow().getItem();
                    if (rental != null) cancelRental(rental);
                });
            }



            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }

                Rental rental = getTableRow().getItem();

                // زرار View دايمًا ظاهر
                viewBtn.setVisible(true);

                // زرار Cancel دايمًا ظاهر، بس يتغير حسب الـ status
                if ("Active".equalsIgnoreCase(rental.getStatus())) {
                    cancelBtn.setText("Cancel");
                    cancelBtn.setDisable(false);
                    cancelBtn.setStyle(""); // إرجاع الستايل العادي
                } else {
                    cancelBtn.setText("Cancel");
                    cancelBtn.setDisable(true);
                    // شكل أحمر فاتح + شفافية عشان يبان إنه خلاص انتهى
                    cancelBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-opacity: 0.85;");
                }

                setGraphic(box);
            }
        });

        colDays.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getDaysLeft()).asObject());
        colDays.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer days, boolean empty) {
                super.updateItem(days, empty);
                if (empty || days == null) {
                    setText(null);
                } else {
                    setText("days " + days);
                    if (days == 0) {
                        setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        colStatus.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(" " + status.toLowerCase() + " ");
                setAlignment(Pos.CENTER);
                switch (status.toLowerCase()) {
                    case "active" -> setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #065f46; -fx-background-radius: 20; -fx-padding: 6 14; -fx-font-weight: bold; ");
                    case "cancelled" -> setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-background-radius: 20; -fx-padding: 6 14; -fx-font-weight: bold; ");
                    case "completed" -> setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #374151; -fx-background-radius: 20; -fx-padding: 6 14; -fx-font-weight: bold; ");
                    default -> setStyle("");
                }
            }
        });

        colAmount.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getAmount()).asObject());
        colAmount.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty || amount == null ? null : "$" + String.format("%.0f", amount));
            }
        });

        colDuration.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getDuration()).asObject());
        colDuration.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer days, boolean empty) {
                super.updateItem(days, empty);
                setText(empty || days == null ? null : "days " + days);
            }
        });

        colPeriod.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                "From: " + data.getValue().getFromDate() + " To: " + data.getValue().getToDate()));

        colCar.setCellFactory(tc -> new TableCell<>() {
            private final Label label = new Label();

            {
                label.setStyle("-fx-font-weight: bold; -fx-text-fill: #212529; -fx-font-size: 14px;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                Rental r = getTableRow().getItem();
                label.setText(r.getCarName());
                setGraphic(label);
            }
        });

        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRentalId()));
    }

    private void viewInvoice(Rental r) {
        if (r == null) return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invoice - AutoHub");
        alert.setHeaderText("Rental Invoice #" + r.getRentalId());
        alert.setContentText(
                "Car: " + r.getCarName() + "\n" +
                        "Rental Period: " + r.getFromDate() + " → " + r.getToDate() + "\n" +
                        "Duration: " + r.getDuration() + " days\n" +
                        "Total Amount: $" + r.getAmount() + "\n" +
                        "Status: " + r.getStatus() + "\n\n" +
                        "Thank you for choosing AutoHub!"
        );
        alert.showAndWait();
    }

    private void cancelRental(Rental r) {
        if (r != null && "Active".equalsIgnoreCase(r.getStatus())) {
            r.setStatus("Cancelled");
            rentalsTable.refresh();
            Platform.runLater(() -> rentalsTable.refresh());
            updateSummaryCards();
        }
    }

    private void updateSummaryCards() {
        double total = rentals.stream().mapToDouble(Rental::getAmount).sum();
        long active = rentals.stream().filter(r -> "Active".equalsIgnoreCase(r.getStatus())).count();
        long cancelled = rentals.stream().filter(r -> "Cancelled".equalsIgnoreCase(r.getStatus())).count();
        long completed = rentals.stream().filter(r -> "Completed".equalsIgnoreCase(r.getStatus())).count();

        updateCard(totalCard, "$" + String.format("%.0f", total), "Total", "icon-total", "💰");
        updateCard(cancelledCard, String.valueOf(cancelled), "Cancelled", "icon-cancelled", "✖");
        updateCard(completedCard, String.valueOf(completed), "Completed", "icon-completed", "✓");
        updateCard(activeCard, String.valueOf(active), "Active", "icon-active", "✓");
    }

    private void updateCard(VBox card, String value, String label, String iconClass, String iconText) {
        card.getChildren().clear();

        HBox top = new HBox(12);
        top.setAlignment(Pos.CENTER);

        Label val = new Label(value);
        val.getStyleClass().add("summary-value");

        Label icon = new Label(iconText);
        icon.getStyleClass().addAll("summary-icon", iconClass);

        top.getChildren().addAll(val, icon);

        Label txt = new Label(label);
        txt.getStyleClass().add("summary-label");

        card.getChildren().addAll(top, txt);
    }
}