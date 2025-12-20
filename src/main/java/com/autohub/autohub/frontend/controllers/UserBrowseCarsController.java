package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.Car;
import com.autohub.autohub.backend.models.CarDAO;
import com.autohub.autohub.backend.models.SessionManager;
import com.autohub.autohub.backend.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.stream.Collectors;

public class UserBrowseCarsController {

    private static final int CARDS_PER_ROW = 3;
    @FXML
    private BorderPane rootBorderPane;
    @FXML
    private VBox contentBox;
    @FXML
    private GridPane carsContainer;
    @FXML
    private TextField txtSearch;
    // Navigation Buttons
    @FXML
    private Button btnBrowseCars;
    @FXML
    private Button btnMyRentals;
    @FXML
    private Button btnReviews;
    @FXML
    private Button btnProfile;
    @FXML
    private Button btnLogout;
    @FXML
    private Label lblUserName;
    // Filter Buttons
    @FXML
    private Button btnFilterAll;
    @FXML
    private Button btnFilterElectric;
    @FXML
    private Button btnFilterLuxury;
    @FXML
    private Button btnFilterSports;
    @FXML
    private Button btnFilterSUV;
    @FXML
    private Button btnFilterSedan;
    private List<Car> allCars;
    private String selectedFilter = "All";

    // استخدام الـ Session بدل القيمة المؤقتة
    private int getCurrentUserId() {
        return SessionManager.getCurrentUserId();
    }

    @FXML
    private void initialize() {
        System.out.println("✅ UserBrowseCarsController initialized");

        loadUserName(); // ✅ تحميل اسم اليوزر
        setupGridPane();
        setupFilterButtons();
        loadCarsFromDatabase();
        setupSearch();
    }

    /**
     * ✅ تحميل اسم اليوزر من الداتابيز
     */
    private void loadUserName() {
        try {
            User user = SessionManager.getCurrentUser();

            if (user != null && lblUserName != null) {
                lblUserName.setText(user.getFullName());
                System.out.println("✅ User name loaded: " + user.getFullName());
            } else {
                lblUserName.setText("User");
            }

        } catch (Exception e) {
            System.err.println("❌ Error loading user name: " + e.getMessage());
            if (lblUserName != null) {
                lblUserName.setText("User");
            }
        }
    }


    private void setupGridPane() {
        // Setup 3 columns with equal width
        for (int i = 0; i < CARDS_PER_ROW; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / CARDS_PER_ROW);
            col.setHgrow(Priority.ALWAYS);
            carsContainer.getColumnConstraints().add(col);
        }
        carsContainer.setHgap(20);
        carsContainer.setVgap(20);
    }

    private void setupFilterButtons() {
        setActiveFilter(btnFilterAll);

        btnFilterAll.setOnAction(e -> {
            selectedFilter = "All";
            setActiveFilter(btnFilterAll);
            displayCars(allCars);
        });

        btnFilterElectric.setOnAction(e -> {
            selectedFilter = "Electric";
            setActiveFilter(btnFilterElectric);
            filterCars("Electric");
        });

        btnFilterLuxury.setOnAction(e -> {
            selectedFilter = "Luxury";
            setActiveFilter(btnFilterLuxury);
            filterCars("Luxury");
        });

        btnFilterSports.setOnAction(e -> {
            selectedFilter = "Sports";
            setActiveFilter(btnFilterSports);
            filterCars("Sports");
        });

        btnFilterSUV.setOnAction(e -> {
            selectedFilter = "SUV";
            setActiveFilter(btnFilterSUV);
            filterCars("SUV");
        });

        btnFilterSedan.setOnAction(e -> {
            selectedFilter = "Sedan";
            setActiveFilter(btnFilterSedan);
            filterCars("Sedan");
        });
    }

    private void setActiveFilter(Button activeButton) {
        Button[] filterButtons = {btnFilterAll, btnFilterElectric, btnFilterLuxury,
                btnFilterSports, btnFilterSUV, btnFilterSedan};

        for (Button btn : filterButtons) {
            if (btn == activeButton) {
                btn.setStyle("-fx-background-color: #2c5ff6; -fx-text-fill: white; " +
                        "-fx-padding: 10 24; -fx-background-radius: 30; " +
                        "-fx-font-weight: bold; -fx-cursor: hand;");
            } else {
                btn.setStyle("-fx-background-color: #f8fafc; -fx-text-fill: #64748b; " +
                        "-fx-padding: 10 24; -fx-background-radius: 30; " +
                        "-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-cursor: hand;");
            }
        }
    }

    private void loadCarsFromDatabase() {
        try {
            allCars = CarDAO.getCars();
            displayCars(allCars);
            System.out.println("✅ Loaded " + allCars.size() + " cars from database");
        } catch (Exception e) {
            System.err.println("❌ Error loading cars: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterCars(String category) {
        List<Car> filteredCars = allCars.stream()
                .filter(car -> car.getModel() != null &&
                        car.getModel().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        displayCars(filteredCars);
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                if (selectedFilter.equals("All")) {
                    displayCars(allCars);
                } else {
                    filterCars(selectedFilter);
                }
            } else {
                searchCars(newValue.trim());
            }
        });
    }

    private void searchCars(String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        List<Car> searchResults = allCars.stream()
                .filter(car ->
                        car.getCarName().toLowerCase().contains(lowerSearchTerm) ||
                                car.getBrand().toLowerCase().contains(lowerSearchTerm) ||
                                car.getModel().toLowerCase().contains(lowerSearchTerm)
                )
                .collect(Collectors.toList());

        displayCars(searchResults);
    }

    private void displayCars(List<Car> cars) {
        carsContainer.getChildren().clear();

        if (cars == null || cars.isEmpty()) {
            Label noDataLabel = new Label("No cars found");
            noDataLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #64748b; -fx-padding: 40;");
            carsContainer.add(noDataLabel, 0, 0, CARDS_PER_ROW, 1);
            return;
        }

        int row = 0;
        int col = 0;

        for (Car car : cars) {
            VBox carCard = createCarCard(car);
            carsContainer.add(carCard, col, row);

            col++;
            if (col >= CARDS_PER_ROW) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createCarCard(Car car) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_LEFT);
        card.maxWidthProperty().bind(carsContainer.widthProperty().divide(CARDS_PER_ROW).subtract(30));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 5);");

        // Car Image
        StackPane imageContainer = new StackPane();
        imageContainer.prefHeightProperty().bind(card.widthProperty().multiply(0.6));
        imageContainer.maxHeightProperty().bind(card.widthProperty().multiply(0.6));
        imageContainer.setStyle("-fx-background-color: #e2e8f0; -fx-background-radius: 14;");

        try {
            if (car.getImageUrl() != null && !car.getImageUrl().isEmpty()) {
                ImageView imageView = new ImageView(new Image(car.getImageUrl(), true));
                imageView.fitWidthProperty().bind(card.widthProperty().subtract(40));
                imageView.fitHeightProperty().bind(imageContainer.prefHeightProperty().subtract(20));
                imageView.setPreserveRatio(true);
                imageContainer.getChildren().add(imageView);
            } else {
                FontIcon carIcon = new FontIcon("fas-car");
                carIcon.setIconSize(60);
                carIcon.setIconColor(Color.web("#94a3b8"));
                imageContainer.getChildren().add(carIcon);
            }
        } catch (Exception e) {
            FontIcon carIcon = new FontIcon("fas-car");
            carIcon.setIconSize(60);
            carIcon.setIconColor(Color.web("#94a3b8"));
            imageContainer.getChildren().add(carIcon);
        }

        // Badges
        HBox badges = new HBox(8);
        Label categoryBadge = new Label(car.getModel() != null ? car.getModel() : "Sedan");
        categoryBadge.setStyle("-fx-background-color: #fffbeb; -fx-text-fill: #92400e; " +
                "-fx-padding: 4 10; -fx-background-radius: 15; -fx-font-size: 11px;");

        Label statusBadge = new Label(car.getStatus().equalsIgnoreCase("available") ? "Available" : "Rented");
        if (car.getStatus().equalsIgnoreCase("available")) {
            statusBadge.setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #065f46; " +
                    "-fx-padding: 5 12; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 11px;");
        } else {
            statusBadge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; " +
                    "-fx-padding: 5 12; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 11px;");
        }
        badges.getChildren().addAll(categoryBadge, statusBadge);

        // Car Name
        Label carName = new Label(car.getBrand() + " " + car.getModel());
        carName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        carName.setWrapText(true);

        // Car Model
        Label carModel = new Label(car.getCarName());
        carModel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        // Specs
        HBox specs = new HBox(15);
        specs.getChildren().addAll(
                createSpec("fas-gas-pump", car.getFuelType()),
                createSpec("fas-cog", car.getTransmission()),
                createSpec("fas-chair", car.getSeats() + " Seats")
        );

        // Features
        HBox features = new HBox(8);
        features.setAlignment(Pos.CENTER_LEFT);
        if (car.getFeatures() != null && !car.getFeatures().isEmpty()) {
            String[] featureArray = car.getFeatures().split(",");
            for (int i = 0; i < Math.min(3, featureArray.length); i++) {
                Label featureBadge = new Label(featureArray[i].trim());
                featureBadge.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; " +
                        "-fx-padding: 6 12; -fx-background-radius: 15; -fx-font-size: 10px;");
                features.getChildren().add(featureBadge);
            }
        }

        // Price & Button
        HBox priceSection = new HBox(10);
        priceSection.setAlignment(Pos.CENTER_LEFT);

        Button rentButton;
        if (car.getStatus().equalsIgnoreCase("available")) {
            rentButton = new Button("Rent Now");
            rentButton.setStyle("-fx-background-color: #2c5ff6; -fx-text-fill: white; " +
                    "-fx-padding: 12 24; -fx-background-radius: 25; " +
                    "-fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 12px;");
            rentButton.setOnAction(e -> handleRentCar(car));

        } else {
            rentButton = new Button("Not Available");
            rentButton.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #64748b; " +
                    "-fx-padding: 10 20; -fx-background-radius: 25; -fx-font-size: 11px;");
            rentButton.setDisable(true);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox priceBox = new VBox(2);
        priceBox.setAlignment(Pos.CENTER_RIGHT);
        Label price = new Label("$" + String.format("%.0f", car.getPricePerDay()));
        price.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        Label perDay = new Label("per day");
        perDay.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
        priceBox.getChildren().addAll(price, perDay);

        priceSection.getChildren().addAll(rentButton, spacer, priceBox);

        card.getChildren().addAll(imageContainer, badges, carName, carModel, specs, features, priceSection);
        return card;
    }

    private HBox createSpec(String iconLiteral, String text) {
        HBox spec = new HBox(6);
        spec.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(14);
        icon.setIconColor(Color.web("#64748b"));

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

        spec.getChildren().addAll(icon, label);
        return spec;
    }

    @FXML
    private void handleBrowseCars() {
        System.out.println("📍 Browse Cars");
        setActiveNavButton(btnBrowseCars);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserBrowseCars.fxml"));
            BorderPane newRoot = loader.load();
            rootBorderPane.getScene().setRoot(newRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyRentals() {
        System.out.println("📍 Navigate to My Rentals");
        setActiveNavButton(btnMyRentals);

        try {
            // تحميل صفحة My Rentals
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/my-rentals.fxml"));
            Parent myRentalsPage = loader.load();

            // عرض الصفحة في الـ ContentBox
            contentBox.getChildren().clear();
            contentBox.getChildren().add(myRentalsPage);

            System.out.println("✅ My Rentals page loaded successfully");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error loading My Rentals page: " + e.getMessage());

            // في حالة الخطأ، عرض رسالة
            contentBox.getChildren().clear();
            VBox errorPage = createEmptyPage("Error", "Failed to load rentals page.");
            contentBox.getChildren().add(errorPage);
        }
    }

    @FXML
    private void handleReviews() {
        System.out.println("📍 Navigate to Reviews");
        setActiveNavButton(btnReviews);

        try {
            // Load the Reviews content
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reviews.fxml"));
            Parent reviewsContent = loader.load();

            // Replace content box
            contentBox.getChildren().clear();
            contentBox.getChildren().add(reviewsContent);

            System.out.println("✅ Reviews page loaded successfully");
        } catch (Exception e) {
            System.err.println("❌ Error loading reviews page: " + e.getMessage());
            e.printStackTrace();

            // Fallback
            contentBox.getChildren().clear();
            VBox emptyPage = createEmptyPage("My Reviews", "Failed to load reviews page.");
            contentBox.getChildren().add(emptyPage);
        }
    }

    private VBox createEmptyPage(String titleText, String messageText) {
        VBox page = new VBox(30);
        page.setAlignment(Pos.CENTER);
        page.setStyle("-fx-padding: 100;");

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label message = new Label(messageText);
        message.setStyle("-fx-font-size: 18px; -fx-text-fill: #64748b;");

        page.getChildren().addAll(title, message);
        return page;
    }

    @FXML
    private void handleProfile() {
        System.out.println("👤 Open Profile");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Profile");
        alert.setContentText("Profile page coming soon!");
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        System.out.println("🚪 Logout requested");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be redirected to the login page.");

        // تنسيق الـ Alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-family: 'Segoe UI';");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // تسجيل الخروج من الـ Session
                SessionManager.logout();
                System.out.println("✅ Session cleared");

                try {
                    // فتح صفحة Login
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) rootBorderPane.getScene().getWindow();
                    Scene scene = new Scene(root, 1100, 650);
                    stage.setScene(scene);
                    stage.setTitle("DriveNow - Login");
                    stage.setMaximized(false); // إلغاء Maximized
                    stage.centerOnScreen();
                    stage.show();

                    System.out.println("✅ Redirected to Login page");

                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("❌ Error opening login page: " + e.getMessage());

                    // رسالة خطأ للمستخدم
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Failed to logout");
                    errorAlert.setContentText("Error: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            } else {
                System.out.println("❌ Logout cancelled");
            }
        });
    }


    private void setActiveNavButton(Button activeButton) {
        Button[] navButtons = {btnBrowseCars, btnMyRentals, btnReviews};

        for (Button btn : navButtons) {
            if (btn == activeButton) {
                btn.setStyle("-fx-background-color: #2c5ff6; -fx-text-fill: white; " +
                        "-fx-padding: 12 24; -fx-background-radius: 8; " +
                        "-fx-font-size: 14px; -fx-font-weight: 600; -fx-cursor: hand;");
                if (btn.getGraphic() instanceof FontIcon icon) {
                    icon.setIconColor(Color.WHITE);
                }
            } else {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; " +
                        "-fx-padding: 12 24; -fx-font-size: 14px; -fx-cursor: hand;");
                if (btn.getGraphic() instanceof FontIcon icon) {
                    icon.setIconColor(Color.web("#64748b"));
                }
            }
        }
    }

    /**
     * فتح صفحة Booking لعربية معينة
     */
    private void handleRentCar(Car car) {
        try {
            // تحميل صفحة Booking
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/booking.fxml"));
            Parent bookingRoot = loader.load();

            // الحصول على الـ Controller وإرسال بيانات العربية
            BookingController bookingController = loader.getController();
            bookingController.setCar(car);

            // فتح نافذة جديدة
            Stage bookingStage = new Stage();
            bookingStage.setTitle("Book Your Ride - " + car.getBrand() + " " + car.getModel());
            bookingStage.setScene(new Scene(bookingRoot));
            bookingStage.setResizable(false);
            bookingStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            bookingStage.showAndWait();

            // بعد إغلاق صفحة Booking - تحديث العربيات
            loadCarsFromDatabase();

            System.out.println("✅ Booking page closed, cars refreshed");

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open booking page");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

}
