package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.Rental;
import com.autohub.autohub.backend.models.RentalDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class InvoicesController implements Initializable {

    private final ObservableList<Rental> allInvoices = FXCollections.observableArrayList();
    private final ObservableList<Rental> filteredInvoices = FXCollections.observableArrayList();
    @FXML
    private HBox statsRow;
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cmbStatusFilter;
    @FXML
    private Button btnClearFilter;
    @FXML
    private TableView<Rental> invoicesTable;
    @FXML
    private TableColumn<Rental, Void> colInvoiceId;
    @FXML
    private TableColumn<Rental, Void> colCustomer;
    @FXML
    private TableColumn<Rental, Void> colDate;
    @FXML
    private TableColumn<Rental, Void> colAmount;
    @FXML
    private TableColumn<Rental, Void> colStatus;
    @FXML
    private TableColumn<Rental, Void> colActions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupStatsCards();
        setupStatusFilter();
        setupTableColumns();
        loadInvoices();
        setupSearchAndFilter();

        invoicesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupStatsCards() {
        double totalRevenue = RentalDAO.getTotalRevenue();
        int totalInvoices = RentalDAO.getAllRentals().size();
        double monthlyRevenue = RentalDAO.getMonthlyRevenue();
        int activeCount = RentalDAO.getActiveRentalsCount();

        VBox card1 = createStatCard(String.valueOf(totalInvoices), "Total Invoices",
                "#3b82f6", "fas-file-invoice");
        VBox card2 = createStatCard(String.format("$%.0f", totalRevenue), "Total Revenue",
                "#10b981", "fas-dollar-sign");
        VBox card3 = createStatCard(String.format("$%.0f", monthlyRevenue), "This Month",
                "#8b5cf6", "fas-calendar");
        VBox card4 = createStatCard(String.valueOf(activeCount), "Active",
                "#0284c7", "fas-file-alt");

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);

        statsRow.getChildren().addAll(card1, card2, card3, card4);
    }

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

    private void setupStatusFilter() {
        cmbStatusFilter.getItems().addAll("All", "Active", "Completed", "Cancelled");
        cmbStatusFilter.setValue("All");

        cmbStatusFilter.setOnAction(e -> applyFilters());
    }

    private void setupSearchAndFilter() {
        txtSearch.textProperty().addListener((obs, old, newVal) -> applyFilters());
    }

    private void applyFilters() {
        String searchText = txtSearch.getText().toLowerCase().trim();
        String statusFilter = cmbStatusFilter.getValue();

        filteredInvoices.clear();

        List<Rental> filtered = allInvoices.stream()
                .filter(rental -> {
                    // Search filter
                    boolean matchesSearch = searchText.isEmpty() ||
                            rental.getCustomerName().toLowerCase().contains(searchText) ||
                            String.format("INV-%03d", rental.getRentalId()).toLowerCase().contains(searchText) ||
                            (rental.getCustomerEmail() != null && rental.getCustomerEmail().toLowerCase().contains(searchText));

                    // Status filter
                    boolean matchesStatus = statusFilter.equals("All") ||
                            rental.getStatus().equalsIgnoreCase(statusFilter);

                    return matchesSearch && matchesStatus;
                })
                .collect(Collectors.toList());

        filteredInvoices.addAll(filtered);
        invoicesTable.setItems(filteredInvoices);
    }

    @FXML
    private void handleClearFilters() {
        txtSearch.clear();
        cmbStatusFilter.setValue("All");
        applyFilters();
    }

    private void setupTableColumns() {
        // Invoice ID Column
        colInvoiceId.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Rental rental = getTableView().getItems().get(getIndex());

                    VBox box = new VBox(4);
                    box.setAlignment(Pos.CENTER_LEFT);

                    Label invoiceNum = new Label("INV-" + String.format("%03d", rental.getRentalId()));
                    invoiceNum.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

                    Label rentalLabel = new Label("Rental #" + rental.getRentalId());
                    rentalLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

                    box.getChildren().addAll(invoiceNum, rentalLabel);
                    setPadding(new Insets(8));
                    setGraphic(box);
                }
            }
        });

        // Customer Column
        colCustomer.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Rental rental = getTableView().getItems().get(getIndex());

                    VBox box = new VBox(4);
                    Label name = new Label(rental.getCustomerName() != null ? rental.getCustomerName() : "N/A");
                    name.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #0f172a;");

                    Label email = new Label(rental.getCustomerEmail() != null ? rental.getCustomerEmail() : "");
                    email.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

                    box.getChildren().addAll(name, email);
                    setPadding(new Insets(8));
                    setGraphic(box);
                }
            }
        });

        // Date Column
        colDate.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Rental rental = getTableView().getItems().get(getIndex());

                    VBox box = new VBox(4);
                    box.setAlignment(Pos.CENTER_LEFT);

                    Label dateLabel = new Label(rental.getStartDate() != null ? rental.getStartDate().toString() : "N/A");
                    dateLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #0f172a;");

                    Label periodLabel = new Label(rental.getDuration() + " days");
                    periodLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");

                    box.getChildren().addAll(dateLabel, periodLabel);
                    setPadding(new Insets(8));
                    setGraphic(box);
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
                    label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #10b981;");
                    setAlignment(Pos.CENTER);
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

                    Label statusLabel = new Label(status.toUpperCase());
                    String bgColor;

                    if (status.equalsIgnoreCase("active")) {
                        bgColor = "#0284c7";
                    } else if (status.equalsIgnoreCase("completed")) {
                        bgColor = "#10b981";
                    } else {
                        bgColor = "#ef4444";
                    }

                    statusLabel.setStyle("-fx-background-color: " + bgColor + "; " +
                            "-fx-text-fill: white; -fx-padding: 8 16; " +
                            "-fx-background-radius: 20; -fx-font-size: 12px; " +
                            "-fx-font-weight: bold;");

                    setAlignment(Pos.CENTER);
                    setGraphic(statusLabel);
                }
            }
        });

        // Actions Column
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View");

            {
                FontIcon eyeIcon = new FontIcon("fas-eye");
                eyeIcon.setIconSize(14);
                eyeIcon.setIconColor(Color.WHITE);
                viewBtn.setGraphic(eyeIcon);
                viewBtn.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; " +
                        "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: 600;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Rental rental = getTableView().getItems().get(getIndex());
                    viewBtn.setOnAction(e -> handleViewInvoice(rental));
                    setAlignment(Pos.CENTER);
                    setGraphic(viewBtn);
                }
            }
        });
    }

    private void loadInvoices() {
        allInvoices.clear();
        allInvoices.addAll(RentalDAO.getAllRentals());
        filteredInvoices.clear();
        filteredInvoices.addAll(allInvoices);
        invoicesTable.setItems(filteredInvoices);
        System.out.println("✅ Loaded " + allInvoices.size() + " invoices");
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
            invoiceStage.setTitle("Invoice - INV-" + String.format("%03d", rental.getRentalId()));
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
}
