package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.Car;
import com.autohub.autohub.backend.models.CarDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    // في DashboardController.java - أول الكلاس بعد public class DashboardController

    private static DashboardController instance;
    // ← تأكد موجود
    @FXML
    private Button btnUploadImage;
    @FXML
    private Button btnAddCar;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnCloseModal;
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
    private Car currentEditingCar = null;  // ← السيارة اللي بنعدلها
    @FXML
    private ComboBox<String> cbStatus;

    public DashboardController() {
        instance = this;
    }

    public static DashboardController getInstance() {
        return instance;
    }

    public void openAddCarModalPublic() {
        modalOverlay.setVisible(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupNavigationIcons();
        loadDashboardContent();
        setupComboBoxes();

        // إضافة options للـ ComboBoxes
        if (cbCategory != null) {
            cbCategory.getItems().addAll("Sedan", "SUV", "Sports", "Luxury", "Electric");
        }
        if (cbTransmission != null) {
            cbTransmission.getItems().addAll("Automatic", "Manual");
        }
        if (cbFuelType != null) {
            cbFuelType.getItems().addAll("Petrol", "Diesel", "Electric", "Hybrid");
        }
        if (cbStatus != null) {  // ← جديد
            cbStatus.getItems().addAll("available", "rented");
            cbStatus.setValue("available");
        }
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

    // فتح modal للتعديل
    public void openEditCarModal(Car car) {
        currentEditingCar = car;

        // تغيير نص الزر
        btnAddCar.setText("Save Changes");  // ← بدل "Add Car"

        // ملء الحقول بالبيانات
        if (txtCarName != null) txtCarName.setText(car.getCarName());
        if (txtBrand != null) txtBrand.setText(car.getBrand() != null ? car.getBrand() : "");
        if (txtYear != null) txtYear.setText(car.getYear());
        if (txtModel != null) txtModel.setText(car.getModel());
        if (txtPricePerDay != null) txtPricePerDay.setText(String.valueOf(car.getPricePerDay()));
        if (txtSeats != null) txtSeats.setText(String.valueOf(car.getSeats()));
        if (txtImageUrl != null) txtImageUrl.setText(car.getImageUrl());
        if (txtFeatures != null) txtFeatures.setText(car.getFeatures() != null ? car.getFeatures() : "");

        if (cbCategory != null && car.getModel() != null && cbCategory.getItems().contains(car.getModel())) {
            cbCategory.setValue(car.getModel());
        }

        if (cbTransmission != null && car.getTransmission() != null && cbTransmission.getItems().contains(car.getTransmission())) {
            cbTransmission.setValue(car.getTransmission());
        }

        if (cbFuelType != null && car.getFuelType() != null && cbFuelType.getItems().contains(car.getFuelType())) {
            cbFuelType.setValue(car.getFuelType());
        }

        if (cbStatus != null && car.getStatus() != null && cbStatus.getItems().contains(car.getStatus())) {
            cbStatus.setValue(car.getStatus());
        }

        modalOverlay.setVisible(true);
        System.out.println("Editing car: " + car.getCarName());
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

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/cars.fxml")
            );
            javafx.scene.Parent carsRoot = loader.load();

            // تأكد من تحميل الـ controller
            CarsController carsController = loader.getController();

            contentBox.getChildren().add(carsRoot);

            System.out.println("✅ Cars page loaded successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            Label error = new Label("Failed to load Cars page: " + e.getMessage());
            error.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            contentBox.getChildren().add(error);
        }
    }


    @FXML
    private void handleRentals() {
        setActiveWithIcon(btnRentals);
        contentBox.getChildren().clear();

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/rentals.fxml")
            );
            javafx.scene.Parent rentalsRoot = loader.load();
            contentBox.getChildren().add(rentalsRoot);

            System.out.println("✅ Rentals page loaded!");
        } catch (Exception e) {
            e.printStackTrace();
            Label error = new Label("Failed to load Rentals page: " + e.getMessage());
            error.setStyle("-fx-text-fill: red;");
            contentBox.getChildren().add(error);
        }
    }


    @FXML
    private void handleInvoices() {
        setActiveWithIcon(btnInvoices);
        contentBox.getChildren().clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/invoices.fxml"));
            Parent invoicesRoot = loader.load();
            contentBox.getChildren().add(invoicesRoot);
        } catch (Exception e) {
            e.printStackTrace();
            Label error = new Label("Failed to load Invoices page");
            error.setStyle("-fx-text-fill: red;");
            contentBox.getChildren().add(error);
        }
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

        // جلب البيانات من الداتابيز
        int totalCars = CarDAO.getTotalCarsCount();
        int rentedCars = CarDAO.getRentedCarsCount();
        int availableCars = totalCars - rentedCars;
        double weeklyRevenue = CarDAO.getWeeklyRevenue();  // ← الإيرادات الأسبوعية

        // إنشاء الـ stat cards
        VBox statCard1 = createStatCard(
                "Total Cars",
                String.valueOf(totalCars),
                "In fleet",
                "fas-car",
                "#2c5ff6"
        );

        VBox statCard2 = createStatCard(
                "Cars Rented",
                String.valueOf(rentedCars),
                "Currently active",
                "fas-key",
                "#f59e0b"
        );

        // ← الكارت الثالثة (بدل Avg Price)
        VBox statCard3 = createStatCard(
                "Weekly Revenue",           // ← العنوان
                String.format("$%.0f", weeklyRevenue),  // ← القيمة
                availableCars + " cars available",       // ← الوصف
                "fas-dollar-sign",          // ← الأيقونة
                "#10b981"                   // ← اللون الأخضر
        );

        VBox statCard4 = createStatCard(
                "Active Rentals",
                String.valueOf(rentedCars),
                "Ongoing contracts",
                "fas-chart-line",
                "#8b5cf6"
        );

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
        btn2.setOnAction(e -> handleRentals());
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
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-padding: 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 3);");

        // Header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Car Fleet Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label desc = new Label("Most rented vehicles in your fleet");
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        titleBox.getChildren().addAll(title, desc);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Button viewAllBtn = new Button("View All Cars");
        viewAllBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2c5ff6; " +
                "-fx-font-size: 14px; -fx-border-width: 0; -fx-padding: 0; -fx-cursor: hand;");
        viewAllBtn.setOnAction(e -> handleCars()); // ← يروح لصفحة Cars

        headerBox.getChildren().addAll(titleBox, viewAllBtn);
        section.getChildren().add(headerBox);

        // جلب أشهر العربيات من الداتابيز
        List<Car> topCars = CarDAO.getMostRentedCars(3); // ← أشهر 3 عربيات

        VBox carsBox = new VBox(16);

        if (topCars.isEmpty()) {
            // لو مفيش سيارات
            Label emptyLabel = new Label("No cars available yet. Add your first car!");
            emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b; -fx-padding: 20;");
            carsBox.getChildren().add(emptyLabel);
        } else {
            // عرض السيارات
            for (Car car : topCars) {
                carsBox.getChildren().add(createCarCardDynamic(car));
            }
        }

        section.getChildren().add(carsBox);
        return section;
    }

    private HBox createCarCardDynamic(Car car) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; " +
                "-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-padding: 20;");

        // صورة العربية
        VBox imageBox = new VBox();
        imageBox.setPrefSize(140, 100);
        imageBox.setStyle("-fx-background-color: #e2e8f0; -fx-background-radius: 8;");
        imageBox.setAlignment(Pos.CENTER);

        // استخدام أيقونة كـ placeholder
        FontIcon placeholderIcon = new FontIcon("fas-car");
        placeholderIcon.setIconSize(48);
        placeholderIcon.setIconColor(Color.web("#64748b"));
        imageBox.getChildren().add(placeholderIcon);

        // تحميل الصورة في الخلفية (optional)
        if (car.getImageUrl() != null && !car.getImageUrl().isEmpty()) {
            javafx.concurrent.Task<javafx.scene.image.Image> loadImageTask =
                    new javafx.concurrent.Task<>() {
                        @Override
                        protected javafx.scene.image.Image call() {
                            try {
                                return new javafx.scene.image.Image(
                                        car.getImageUrl(),
                                        140, 100, true, true, true  // ← background loading
                                );
                            } catch (Exception e) {
                                return null;
                            }
                        }
                    };

            loadImageTask.setOnSucceeded(e -> {
                javafx.scene.image.Image image = loadImageTask.getValue();
                if (image != null && !image.isError()) {
                    imageBox.getChildren().clear();
                    javafx.scene.image.ImageView imageView =
                            new javafx.scene.image.ImageView(image);
                    imageView.setFitWidth(140);
                    imageView.setFitHeight(100);
                    imageView.setPreserveRatio(false);
                    imageBox.getChildren().add(imageView);
                }
            });

            // تشغيل الـ task
            new Thread(loadImageTask).start();
        }

        // معلومات العربية
        VBox infoBox = new VBox(8);
        Label nameLbl = new Label(car.getCarName());
        nameLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label typeLbl = new Label(car.getModel() != null ? car.getModel() : "Sedan");
        typeLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        HBox specsBox = new HBox(20);
        specsBox.getChildren().addAll(
                createSpecItem("far-calendar", car.getYear()),
                createSpecItem("fas-cog", car.getTransmission()),
                createSpecItem("fas-gas-pump", car.getFuelType()),
                createSpecItem("fas-users", car.getSeats() + " Seats")
        );

        infoBox.getChildren().addAll(nameLbl, typeLbl, specsBox);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // السعر والحالة
        VBox priceBox = new VBox(8);
        priceBox.setAlignment(Pos.CENTER_RIGHT);

        Label priceLbl = new Label(String.format("$%.0f/day", car.getPricePerDay()));
        priceLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        String statusColor = car.getStatus().equalsIgnoreCase("available") ? "#10b981" : "#f59e0b";
        String statusText = car.getStatus().equalsIgnoreCase("available") ? "Available" : "Rented";

        Label statusLbl = new Label(statusText);
        statusLbl.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; " +
                "-fx-padding: 6 16; -fx-background-radius: 20; " +
                "-fx-font-size: 12px; -fx-font-weight: bold;");

        priceBox.getChildren().addAll(priceLbl, statusLbl);

        // أزرار Edit و Delete
        VBox actionsBox = new VBox(8);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        // زر Edit
        Button editBtn = new Button();
        FontIcon editIcon = new FontIcon("fas-edit");
        editIcon.setIconSize(14);
        editIcon.setIconColor(Color.web("#0284c7"));
        editBtn.setGraphic(editIcon);
        editBtn.setStyle("-fx-background-color: #dbeafe; -fx-border-width: 0; " +
                "-fx-padding: 10; -fx-background-radius: 8; -fx-cursor: hand;");
        editBtn.setOnAction(e -> {
            openEditCarModal(car);
        });

        // زر Delete
        Button delBtn = new Button();
        FontIcon delIcon = new FontIcon("fas-trash");
        delIcon.setIconSize(14);
        delIcon.setIconColor(Color.web("#dc2626"));
        delBtn.setGraphic(delIcon);
        delBtn.setStyle("-fx-background-color: #fee2e2; -fx-border-width: 0; " +
                "-fx-padding: 10; -fx-background-radius: 8; -fx-cursor: hand;");
        delBtn.setOnAction(e -> {
            handleDeleteCar(car);
        });

        actionsBox.getChildren().addAll(editBtn, delBtn);

        card.getChildren().addAll(imageBox, infoBox, priceBox, actionsBox);
        return card;
    }


    private void handleDeleteCar(Car car) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Car");
        confirm.setHeaderText("Delete " + car.getCarName() + "?");
        confirm.setContentText("Are you sure you want to delete this car? This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = CarDAO.deleteCar(car.getCarId());

                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("✅ Car Deleted!");
                    alert.setContentText(car.getCarName() + " has been removed from your fleet.");
                    alert.showAndWait();

                    // تحديث الداشبورد
                    loadDashboardContent();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("❌ Error");
                    alert.setContentText("Failed to delete the car. Please try again.");
                    alert.showAndWait();
                }
            }
        });
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
        // إخفاء الـ modal
        modalOverlay.setVisible(false);

        // رجوع نص الزر للوضع الطبيعي
        btnAddCar.setText("Add Car");  // ← مهم!

        // تنضيف الحقول
        currentEditingCar = null;
        txtCarName.clear();
        txtBrand.clear();
        txtYear.clear();
        txtModel.clear();
        txtPricePerDay.clear();
        txtSeats.clear();
        txtImageUrl.clear();
        txtFeatures.clear();
        cbCategory.setValue(null);
        cbTransmission.setValue(null);
        cbFuelType.setValue(null);
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
        // Validation with null checks
        if (txtCarName == null || txtCarName.getText().isEmpty() ||
                txtPricePerDay == null || txtPricePerDay.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please fill Car Name and Price.");
            alert.showAndWait();
            return;
        }

        try {
            Car car;
            boolean isEdit = (currentEditingCar != null);

            if (isEdit) {
                car = currentEditingCar;
            } else {
                car = new Car();
            }

            // Set data with null checks
            car.setCarName(txtCarName.getText().trim());
            car.setBrand(txtBrand != null && !txtBrand.getText().isEmpty() ? txtBrand.getText().trim() : "Unknown");
            car.setModel(cbCategory != null && cbCategory.getValue() != null ? cbCategory.getValue() : "Sedan");
            car.setYear(txtYear != null && !txtYear.getText().isEmpty() ? txtYear.getText().trim() : "2024");
            car.setPricePerDay(Double.parseDouble(txtPricePerDay.getText().trim()));
            car.setSeats(txtSeats != null && !txtSeats.getText().isEmpty() ? Integer.parseInt(txtSeats.getText().trim()) : 5);
            car.setTransmission(cbTransmission != null && cbTransmission.getValue() != null ? cbTransmission.getValue() : "Automatic");
            car.setFuelType(cbFuelType != null && cbFuelType.getValue() != null ? cbFuelType.getValue() : "Petrol");
            car.setImageUrl(txtImageUrl != null && !txtImageUrl.getText().isEmpty() ? txtImageUrl.getText().trim() : "");
            car.setStatus(cbStatus != null && cbStatus.getValue() != null ? cbStatus.getValue() : "available");
            car.setFeatures(txtFeatures != null && !txtFeatures.getText().isEmpty() ? txtFeatures.getText().trim() : "");

            boolean success;
            if (isEdit) {
                success = CarDAO.updateCar(car);
            } else {
                success = CarDAO.addCar(car);
            }

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Success!");
                alert.setContentText(isEdit ? "Car updated successfully." : "Car added successfully.");
                alert.showAndWait();

                closeAddCarModal();
                refreshCarsPage();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("Failed to save car.");
                alert.showAndWait();
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error!");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }


    // Helper method للـ alerts
    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    // Method لتحديث صفحة Cars بعد الإضافة
    // Method لتحديث الداشبورد بعد التعديل
    private void refreshCarsPage() {
        try {
            // ببساطة، نعيد تحميل الداشبورد
            loadDashboardContent();
            System.out.println("✅ Dashboard refreshed!");
        } catch (Exception e) {
            System.err.println("Could not refresh dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
