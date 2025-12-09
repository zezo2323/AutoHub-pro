package com.autohub.autohub.frontend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    private String selectedImagePath = "";
    @FXML
    private VBox contentBox;
    @FXML
    private Button btnDashboard, btnCars, btnRentals, btnInvoices, btnComments, btnUsers;
    @FXML
    private Button btnProfile;
    // في أول الكلاس بعد الـ variables الموجودة:
    @FXML
    private Button btnLogout;
    @FXML
    private StackPane modalOverlay;
    @FXML
    private TextField txtBrand, txtCarName, txtYear, txtModel, txtPricePerDay, txtSeats, txtImageUrl, txtFeatures;
    @FXML
    private ComboBox<String> cbCategory, cbTransmission, cbFuelType;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupNavigationIcons();
        loadDashboardContent();
        setupComboBoxes();
    }

    private void setupComboBoxes() {
        // لو الـ modal لسه متحمّلش، الـ controls هتبقى null
        if (cbCategory == null) {
            return;
        }

        cbCategory.getItems().setAll("Sedan", "SUV", "Hatchback", "Coupe", "Convertible", "Truck");
        cbTransmission.getItems().setAll("Automatic", "Manual");
        cbFuelType.getItems().setAll("Petrol", "Diesel", "Electric", "Hybrid");

    }


    private void setupNavigationIcons() {
        FontIcon iconDashboard = new FontIcon("fas-chart-line");
        iconDashboard.setIconSize(14);
        iconDashboard.setIconColor(Color.WHITE);
        btnDashboard.setGraphic(iconDashboard);
        btnDashboard.setGraphicTextGap(8);

        FontIcon iconCars = new FontIcon("fas-car");
        iconCars.setIconSize(14);
        iconCars.setIconColor(Color.web("#64748b"));
        btnCars.setGraphic(iconCars);
        btnCars.setGraphicTextGap(8);

        FontIcon iconRentals = new FontIcon("fas-key");
        iconRentals.setIconSize(14);
        iconRentals.setIconColor(Color.web("#64748b"));
        btnRentals.setGraphic(iconRentals);
        btnRentals.setGraphicTextGap(8);

        FontIcon iconInvoices = new FontIcon("fas-file-invoice");
        iconInvoices.setIconSize(14);
        iconInvoices.setIconColor(Color.web("#64748b"));
        btnInvoices.setGraphic(iconInvoices);
        btnInvoices.setGraphicTextGap(8);

        FontIcon iconComments = new FontIcon("fas-star-half-alt");
        iconComments.setIconSize(14);
        iconComments.setIconColor(Color.web("#64748b"));
        btnComments.setGraphic(iconComments);
        btnComments.setGraphicTextGap(8);

        FontIcon iconUsers = new FontIcon("fas-users-cog");
        iconUsers.setIconSize(14);
        iconUsers.setIconColor(Color.web("#64748b"));
        btnUsers.setGraphic(iconUsers);
        btnUsers.setGraphicTextGap(8);
    }

    private void updateIconColors() {
        if (btnDashboard.getGraphic() != null)
            ((FontIcon) btnDashboard.getGraphic()).setIconColor(Color.web("#64748b"));
        if (btnCars.getGraphic() != null)
            ((FontIcon) btnCars.getGraphic()).setIconColor(Color.web("#64748b"));
        if (btnRentals.getGraphic() != null)
            ((FontIcon) btnRentals.getGraphic()).setIconColor(Color.web("#64748b"));
        if (btnInvoices.getGraphic() != null)
            ((FontIcon) btnInvoices.getGraphic()).setIconColor(Color.web("#64748b"));
        if (btnComments.getGraphic() != null)
            ((FontIcon) btnComments.getGraphic()).setIconColor(Color.web("#64748b"));
        if (btnUsers.getGraphic() != null)
            ((FontIcon) btnUsers.getGraphic()).setIconColor(Color.web("#64748b"));
    }

    private void setActiveWithIcon(Button btn) {
        updateIconColors();
        setActive(btn);

        if (btn.getGraphic() != null && btn.getGraphic() instanceof FontIcon) {
            ((FontIcon) btn.getGraphic()).setIconColor(Color.WHITE);
        }
    }

    private void setActive(Button btn) {
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-padding: 10 18; -fx-font-size: 13px; -fx-border-width: 0;";
        String activeStyle = "-fx-background-color: #2c5ff6; -fx-text-fill: white; -fx-padding: 10 18; -fx-font-size: 13px; -fx-border-width: 0; -fx-background-radius: 8;";

        btnDashboard.setStyle(inactiveStyle);
        btnCars.setStyle(inactiveStyle);
        btnRentals.setStyle(inactiveStyle);
        btnInvoices.setStyle(inactiveStyle);
        btnComments.setStyle(inactiveStyle);
        btnUsers.setStyle(inactiveStyle);
        btnProfile.setStyle(inactiveStyle);

        btn.setStyle(activeStyle);
    }

    @FXML
    private void handleDashboard() {
        loadDashboardContent();
    }

    @FXML
    private void handleCars() {
        setActiveWithIcon(btnCars);
        contentBox.getChildren().clear();
        Label placeholder = new Label("Cars Page - Coming Soon!");
        placeholder.setStyle("-fx-font-size: 24px; -fx-text-fill: #64748b;");
        contentBox.getChildren().add(placeholder);
    }

    @FXML
    private void handleRentals() {
        setActiveWithIcon(btnRentals);
        contentBox.getChildren().clear();
        Label placeholder = new Label("Rentals Page - Coming Soon!");
        placeholder.setStyle("-fx-font-size: 24px; -fx-text-fill: #64748b;");
        contentBox.getChildren().add(placeholder);
    }

    @FXML
    private void handleInvoices() {
        setActiveWithIcon(btnInvoices);
        contentBox.getChildren().clear();
        Label placeholder = new Label("Invoices Page - Coming Soon!");
        placeholder.setStyle("-fx-font-size: 24px; -fx-text-fill: #64748b;");
        contentBox.getChildren().add(placeholder);
    }

    @FXML
    private void handleComments() {
        setActiveWithIcon(btnComments);
        contentBox.getChildren().clear();
        Label placeholder = new Label("Reviews Page - Coming Soon!");
        placeholder.setStyle("-fx-font-size: 24px; -fx-text-fill: #64748b;");
        contentBox.getChildren().add(placeholder);
    }

    @FXML
    private void handleUsers() {
        setActiveWithIcon(btnUsers);
        contentBox.getChildren().clear();
        Label placeholder = new Label("Users Management Page - Coming Soon!");
        placeholder.setStyle("-fx-font-size: 24px; -fx-text-fill: #64748b;");
        contentBox.getChildren().add(placeholder);
    }

    @FXML
    private void handleLogout() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(
                    getClass().getResource("/fxml/login.fxml")
            );
            javafx.stage.Stage stage = (javafx.stage.Stage) contentBox.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfile() {
        setActiveWithIcon(btnProfile);
        contentBox.getChildren().clear();

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(8);
        Label title = new Label("Profile Settings");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        Label subtitle = new Label("Update your personal information");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");
        titleBox.getChildren().addAll(title, subtitle);

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setOnAction(e -> loadDashboardContent());
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563eb; -fx-padding: 12 24; -fx-background-radius: 8;");

        HBox.setHgrow(titleBox, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, backBtn);
        contentBox.getChildren().add(header);

        VBox profileCard = new VBox(20);
        profileCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 32; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        HBox avatarSection = new HBox(20);
        avatarSection.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = new StackPane();
        avatar.setPrefSize(100, 100);
        Circle avatarCircle = new Circle(48);
        avatarCircle.setFill(Color.web("#dbeafe"));
        avatarCircle.setStroke(Color.web("#2563eb"));
        FontIcon avatarIcon = new FontIcon("fas-user");
        avatarIcon.setIconSize(32);
        avatarIcon.setIconColor(Color.web("#2563eb"));
        avatar.getChildren().addAll(avatarCircle, avatarIcon);

        VBox infoSection = new VBox(12);
        Label nameLbl = new Label("Admin User");
        nameLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        Label emailLbl = new Label("admin@drivenow.com");
        emailLbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #6b7280;");
        infoSection.getChildren().addAll(nameLbl, emailLbl);

        avatarSection.getChildren().addAll(avatar, infoSection);

        VBox form = new VBox(20);
        createFormField(form, "Full Name", "Admin User", true);
        createFormField(form, "Email", "admin@drivenow.com", true);
        createFormField(form, "Phone", "+20 123 456 789", false);

        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 16 32; -fx-background-radius: 8; -fx-font-size: 16px; -fx-font-weight: bold;");
        saveBtn.setOnAction(e -> {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setHeaderText("✅ Profile Updated!");
            success.setContentText("Your information has been saved successfully.");
            success.showAndWait();
        });

        form.getChildren().add(saveBtn);
        profileCard.getChildren().addAll(avatarSection, form);
        contentBox.getChildren().add(profileCard);
    }

    private void createFormField(VBox parent, String labelText, String valueText, boolean editable) {
        VBox fieldBox = new VBox(8);
        Label label = new Label(labelText + ":");
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        TextField field = new TextField(valueText);
        field.setEditable(editable);
        field.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #d1d5db; -fx-border-width: 1 1 2 1; -fx-border-radius: 8; -fx-padding: 12 16; -fx-font-size: 14px;");
        fieldBox.getChildren().addAll(label, field);
        parent.getChildren().add(fieldBox);
    }

    private void loadDashboardContent() {
        contentBox.getChildren().clear();
        setActiveWithIcon(btnDashboard);

        contentBox.getChildren().add(createHeader());
        contentBox.getChildren().add(createStatsRow());
        contentBox.getChildren().add(createQuickActions());
        contentBox.getChildren().add(createFleetSection());
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox textBox = new VBox(8);
        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label subtitle = new Label("Welcome back! Here's an overview of your rental business");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
        textBox.getChildren().addAll(title, subtitle);

        HBox.setHgrow(textBox, Priority.ALWAYS);

        // في الـ method createHeader() غيّر الزرار Add Car:

        Button addBtn = new Button("Add Car");
        FontIcon addIcon = new FontIcon("fas-plus");
        addIcon.setIconSize(14);
        addIcon.setIconColor(Color.WHITE);
        addBtn.setGraphic(addIcon);
        addBtn.setGraphicTextGap(8);
        addBtn.setStyle("-fx-background-color: #2c5ff6; -fx-text-fill: white; -fx-padding: 12 24; -fx-font-size: 14px; -fx-background-radius: 8; -fx-cursor: hand;");
        addBtn.setOnAction(e -> openAddCarModal());

        Button invoiceBtn = new Button("Invoices");
        FontIcon invoiceIcon = new FontIcon("fas-file-invoice");
        invoiceIcon.setIconSize(14);
        invoiceIcon.setIconColor(Color.web("#0f172a"));
        invoiceBtn.setGraphic(invoiceIcon);
        invoiceBtn.setGraphicTextGap(8);
        invoiceBtn.setStyle("-fx-background-color: white; -fx-text-fill: #0f172a; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-padding: 12 24; -fx-font-size: 14px; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); -fx-cursor: hand;");

        header.getChildren().addAll(textBox, addBtn, invoiceBtn);
        return header;
    }

    private HBox createStatsRow() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox statCard1 = createStatCard("Total Cars", "6", "In fleet", "fas-car", "#2c5ff6");
        VBox statCard2 = createStatCard("Cars Rented", "2", "Currently active", "fas-key", "#f59e0b");
        VBox statCard3 = createStatCard("Revenue Today", "$2,450", "+15% from yesterday", "fas-dollar-sign", "#10b981");
        VBox statCard4 = createStatCard("Active Rentals", "2", "Ongoing contracts", "fas-chart-line", "#8b5cf6");

        HBox.setHgrow(statCard1, Priority.ALWAYS);
        HBox.setHgrow(statCard2, Priority.ALWAYS);
        HBox.setHgrow(statCard3, Priority.ALWAYS);
        HBox.setHgrow(statCard4, Priority.ALWAYS);

        row.getChildren().addAll(statCard1, statCard2, statCard3, statCard4);
        return row;
    }

    private VBox createStatCard(String label, String value, String description, String icon, String color) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 3);");
        card.setMaxWidth(Double.MAX_VALUE);

        FontIcon iconNode = new FontIcon(icon);
        iconNode.setIconSize(40);
        iconNode.setIconColor(Color.valueOf(color));
        iconNode.setOpacity(0.15);

        Label labelLbl = new Label(label);
        labelLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: normal;");

        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label descLbl = new Label(description);
        descLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        HBox content = new HBox(16);
        content.setAlignment(Pos.CENTER_LEFT);
        VBox textBox = new VBox(4, labelLbl, valueLbl, descLbl);
        content.getChildren().addAll(textBox, iconNode);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        card.getChildren().add(content);
        return card;
    }

    private VBox createQuickActions() {
        VBox section = new VBox(16);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 3);");

        Label title = new Label("Quick Actions");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label desc = new Label("Common administrative tasks");
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        HBox buttonsBox = new HBox(16);


        Button btn1 = createActionButton("fas-plus-circle", "Add New Car");
        btn1.setOnAction(e -> openAddCarModal());  // ← ضيف دي
        Button btn2 = createActionButton("fas-eye", "View Rentals");
        Button btn3 = createActionButton("fas-file-alt", "Generate Invoice");

        HBox.setHgrow(btn1, Priority.ALWAYS);
        HBox.setHgrow(btn2, Priority.ALWAYS);
        HBox.setHgrow(btn3, Priority.ALWAYS);

        buttonsBox.getChildren().addAll(btn1, btn2, btn3);

        section.getChildren().addAll(title, desc, buttonsBox);
        return section;
    }

    private Button createActionButton(String icon, String text) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);

        VBox container = new VBox(12);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(Double.MAX_VALUE);

        FontIcon iconNode = new FontIcon(icon);
        iconNode.setIconSize(24);
        iconNode.setIconColor(Color.web("#0f172a"));

        Label textLbl = new Label(text);
        textLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #1e293b; -fx-font-weight: normal;");

        container.getChildren().addAll(iconNode, textLbl);
        btn.setGraphic(container);
        btn.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-background-radius: 12; -fx-padding: 24; -fx-border-radius: 12;");

        return btn;
    }

    private VBox createFleetSection() {
        VBox section = new VBox(16);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 3);");

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Car Fleet Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label desc = new Label("Add, edit, or remove vehicles from your fleet");
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        titleBox.getChildren().addAll(title, desc);

        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Button viewAllBtn = new Button("View All Cars");
        viewAllBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2c5ff6; -fx-font-size: 14px; -fx-border-width: 0; -fx-padding: 0;");

        headerBox.getChildren().addAll(titleBox, viewAllBtn);
        section.getChildren().add(headerBox);

        VBox carsBox = new VBox(16);
        carsBox.getChildren().addAll(
                createCarCard("Tesla Model S", "Luxury Sedan", "2024", "Automatic", "Electric", "5 Seats", "$120/day", "Available", "#10b981"),
                createCarCard("Range Rover Sport", "Luxury SUV", "2023", "Automatic", "Diesel", "7 Seats", "$150/day", "Rented", "#f59e0b")
        );

        section.getChildren().add(carsBox);
        return section;
    }

    private HBox createCarCard(String name, String type, String year, String trans, String fuel, String seats, String price, String status, String statusColor) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-padding: 20;");

        VBox imageBox = new VBox();
        imageBox.setPrefSize(140, 100);
        imageBox.setStyle("-fx-background-color: #e2e8f0; -fx-background-radius: 8;");
        imageBox.setAlignment(Pos.CENTER);

        FontIcon carIcon = new FontIcon("fas-car");
        carIcon.setIconSize(48);
        carIcon.setIconColor(Color.web("#64748b"));
        imageBox.getChildren().add(carIcon);

        VBox infoBox = new VBox(8);
        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label typeLbl = new Label(type);
        typeLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        HBox specsBox = new HBox(20);
        specsBox.getChildren().addAll(
                createSpecItem("far-calendar", year),
                createSpecItem("fas-cog", trans),
                createSpecItem("fas-gas-pump", fuel),
                createSpecItem("fas-users", seats)
        );

        infoBox.getChildren().addAll(nameLbl, typeLbl, specsBox);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        VBox priceBox = new VBox(8);
        priceBox.setAlignment(Pos.CENTER_RIGHT);
        Label priceLbl = new Label(price);
        priceLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label statusLbl = new Label(status);
        statusLbl.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; -fx-padding: 6 16; -fx-background-radius: 20; -fx-font-size: 12px; -fx-font-weight: bold;");
        priceBox.getChildren().addAll(priceLbl, statusLbl);

        VBox actionsBox = new VBox(8);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        Button editBtn = new Button();
        editBtn.setGraphic(new FontIcon("fas-edit"));
        editBtn.setStyle("-fx-background-color: #dbeafe; -fx-border-width: 0; -fx-padding: 10; -fx-background-radius: 8; -fx-text-fill: #0284c7;");

        Button delBtn = new Button();
        delBtn.setGraphic(new FontIcon("fas-trash"));
        delBtn.setStyle("-fx-background-color: #fee2e2; -fx-border-width: 0; -fx-padding: 10; -fx-background-radius: 8; -fx-text-fill: #dc2626;");

        actionsBox.getChildren().addAll(editBtn, delBtn);

        card.getChildren().addAll(imageBox, infoBox, priceBox, actionsBox);
        return card;
    }

    private HBox createSpecItem(String icon, String text) {
        HBox spec = new HBox(6);
        spec.setAlignment(Pos.CENTER_LEFT);

        FontIcon iconNode = new FontIcon(icon);
        iconNode.setIconSize(14);
        iconNode.setIconColor(Color.web("#64748b"));

        Label textLbl = new Label(text);
        textLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        spec.getChildren().addAll(iconNode, textLbl);
        return spec;
    }

    @FXML
    private void openAddCarModal() {
        modalOverlay.setVisible(true);
    }

    @FXML
    private void closeAddCarModal() {
        modalOverlay.setVisible(false);
        clearForm();
    }

    private void clearForm() {
        txtBrand.clear();
        txtCarName.clear();
        txtYear.clear();
        txtModel.clear();
        txtPricePerDay.clear();
        txtSeats.clear();
        txtImageUrl.clear();
        txtFeatures.clear();
        cbCategory.setValue(null);
        cbTransmission.setValue(null);
        cbFuelType.setValue(null);
        selectedImagePath = "";
    }

    @FXML
    private void handleUploadImage() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Select Car Image");
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        javafx.stage.Stage stage = (javafx.stage.Stage) modalOverlay.getScene().getWindow();
        java.io.File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            txtImageUrl.setText(selectedImagePath);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Image Selected!");
            alert.setContentText("Image path: " + selectedImagePath);
            alert.showAndWait();
        }
    }

    @FXML
    private void handleAddCar() {
        // Validation
        if (txtBrand.getText().isEmpty() || txtCarName.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please fill in all required fields!");
            alert.showAndWait();
            return;
        }

        // Success message
        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setHeaderText("✅ Car Added!");
        success.setContentText("Car has been added successfully to your inventory.");
        success.showAndWait();

        closeAddCarModal();
        loadDashboardContent(); // Refresh dashboard
    }


}
