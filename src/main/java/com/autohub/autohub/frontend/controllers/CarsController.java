package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.Car;
import com.autohub.autohub.backend.models.CarDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CarsController implements Initializable {

    @FXML
    private Button btnAddNewCar;
    @FXML
    private TextField txtSearch;
    @FXML
    private HBox statsContainer;
    @FXML
    private TableView<Car> carsTable;

    @FXML
    private TableColumn<Car, Void> colActions;
    @FXML
    private TableColumn<Car, Void> colStatus;
    @FXML
    private TableColumn<Car, Void> colSpecs;
    @FXML
    private TableColumn<Car, Void> colPrice;
    @FXML
    private TableColumn<Car, Void> colYear;
    @FXML
    private TableColumn<Car, Void> colCategory;
    @FXML
    private TableColumn<Car, Void> colCar;

    private FilteredList<Car> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("CarsController initialized!");

        try {
            createStatisticsCards();
            setupTableColumns();
            loadTableData();
            setupSearch();
            System.out.println("✅ Cars page loaded successfully!");
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
        carsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void createStatisticsCards() {
        try {
            List<Car> cars = CarDAO.getCars();

            int totalCars = cars.size();
            long available = cars.stream().filter(c -> "available".equalsIgnoreCase(c.getStatus())).count();
            long rented = cars.stream().filter(c -> "rented".equalsIgnoreCase(c.getStatus())).count();
            double avgPrice = cars.stream().mapToDouble(Car::getPricePerDay).average().orElse(0.0);

            VBox card1 = createStatCard(String.format("$%.0f", avgPrice), "Avg Price/Day", "fas-dollar-sign", "#3b82f6");
            VBox card2 = createStatCard(String.valueOf(rented), "Rented", "fas-car", "#ef4444");
            VBox card3 = createStatCard(String.valueOf(available), "Available", "fas-check-circle", "#10b981");
            VBox card4 = createStatCard(String.valueOf(totalCars), "Total Cars", "fas-car-side", "#8b5cf6");

            statsContainer.getChildren().clear();
            statsContainer.getChildren().addAll(card1, card2, card3, card4);

            HBox.setHgrow(card1, Priority.ALWAYS);
            HBox.setHgrow(card2, Priority.ALWAYS);
            HBox.setHgrow(card3, Priority.ALWAYS);
            HBox.setHgrow(card4, Priority.ALWAYS);

        } catch (Exception e) {
            System.err.println("Error loading statistics: " + e.getMessage());
        }
    }

    private VBox createStatCard(String value, String label, String iconCode, String iconColor) {
        VBox card = new VBox(8);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        FontIcon icon = new FontIcon(iconCode);
        icon.setIconSize(24);
        icon.setIconColor(Color.web(iconColor));
        icon.setOpacity(0.2);

        topRow.getChildren().addAll(valueLabel, spacer, icon);

        Label descLabel = new Label(label);
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        card.getChildren().addAll(topRow, descLabel);
        return card;
    }

    private void setupTableColumns() {
        // Actions Column
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button();
            private final Button btnDelete = new Button();

            {
                FontIcon editIcon = new FontIcon("fas-edit");
                editIcon.setIconSize(16);
                editIcon.setIconColor(Color.web("#64748b"));
                btnEdit.setGraphic(editIcon);
                btnEdit.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 6;");
                btnEdit.setOnMouseEntered(e -> {
                    btnEdit.setStyle("-fx-background-color: #f1f5f9; -fx-cursor: hand; -fx-padding: 6; -fx-background-radius: 6;");
                    editIcon.setIconColor(Color.web("#2563eb"));
                });
                btnEdit.setOnMouseExited(e -> {
                    btnEdit.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 6;");
                    editIcon.setIconColor(Color.web("#64748b"));
                });
                btnEdit.setOnAction(e -> handleEditCar(getTableView().getItems().get(getIndex())));

                FontIcon deleteIcon = new FontIcon("fas-trash");
                deleteIcon.setIconSize(16);
                deleteIcon.setIconColor(Color.web("#64748b"));
                btnDelete.setGraphic(deleteIcon);
                btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 6;");
                btnDelete.setOnMouseEntered(e -> {
                    btnDelete.setStyle("-fx-background-color: #fef2f2; -fx-cursor: hand; -fx-padding: 6; -fx-background-radius: 6;");
                    deleteIcon.setIconColor(Color.web("#ef4444"));
                });
                btnDelete.setOnMouseExited(e -> {
                    btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 6;");
                    deleteIcon.setIconColor(Color.web("#64748b"));
                });
                btnDelete.setOnAction(e -> handleDeleteCar(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(4, btnEdit, btnDelete);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
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
                    Car car = getTableView().getItems().get(getIndex());
                    Label badge = new Label(car.getStatus());

                    if ("available".equalsIgnoreCase(car.getStatus())) {
                        badge.setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #065f46; " +
                                "-fx-padding: 6 14; -fx-background-radius: 16; -fx-font-size: 12px; -fx-font-weight: 600;");
                    } else if ("rented".equalsIgnoreCase(car.getStatus())) {
                        badge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; " +
                                "-fx-padding: 6 14; -fx-background-radius: 16; -fx-font-size: 12px; -fx-font-weight: 600;");
                    }

                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Specs Column
// Specs Column
        colSpecs.setCellFactory(param -> new TableCell<Car, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Car car = getTableView().getItems().get(getIndex());

                    VBox specsBox = new VBox(4);

                    // جلب البيانات الحقيقية من الـ Car object
                    String line1 = car.getSeats() + " seats • " +
                            (car.getTransmission() != null ? car.getTransmission() : "Automatic");

                    String line2 = car.getFuelType() != null ? car.getFuelType() : "Petrol";

                    Label label1 = new Label(line1);
                    label1.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

                    Label label2 = new Label(line2);
                    label2.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

                    specsBox.getChildren().addAll(label1, label2);
                    setGraphic(specsBox);
                }
            }
        });


        // Price Column
        colPrice.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setText(null);
                } else {
                    Car car = getTableView().getItems().get(getIndex());
                    setText(String.format("$%.0f", car.getPricePerDay()));
                    setStyle("-fx-font-weight: 600; -fx-text-fill: #059669; -fx-font-size: 14px;");
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Year Column
        colYear.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setText(null);
                } else {
                    Car car = getTableView().getItems().get(getIndex());
                    setText(car.getYear());
                    setStyle("-fx-font-size: 14px; -fx-text-fill: #1e293b;");
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Category Column
        colCategory.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Car car = getTableView().getItems().get(getIndex());
                    Label badge = new Label(car.getModel());
                    badge.setStyle("-fx-background-color: #ede9fe; -fx-text-fill: #7c3aed; " +
                            "-fx-padding: 6 14; -fx-background-radius: 16; -fx-font-size: 12px; -fx-font-weight: 600;");
                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Car Column (Image + Name + Model)
// Car Column (Image + Name + Model)
        // Car Column (Image + Name + Model)
        colCar.setCellFactory(param -> new TableCell<Car, Void>() {
            private final ImageView imageView = new ImageView();
            private final Rectangle clip = new Rectangle(90, 60);
            private final StackPane imagePlaceholder = new StackPane();

            {
                imageView.setFitWidth(90);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);
                clip.setArcWidth(12);
                clip.setArcHeight(12);
                imageView.setClip(clip);

                // Placeholder
                imagePlaceholder.setPrefSize(90, 60);
                imagePlaceholder.setStyle("-fx-background-color: #e5e7eb; -fx-background-radius: 12;");
                FontIcon carIcon = new FontIcon("fas-car");
                carIcon.setIconSize(32);
                carIcon.setIconColor(Color.web("#9ca3af"));
                imagePlaceholder.getChildren().add(carIcon);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Car car = getTableView().getItems().get(getIndex());

                    // Create text box
                    VBox textBox = new VBox(4);
                    Label nameLabel = new Label(car.getCarName());
                    nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #0f172a;");

                    Label modelLabel = new Label(car.getModel());
                    modelLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

                    textBox.getChildren().addAll(nameLabel, modelLabel);

                    HBox carBox = new HBox(12);
                    carBox.setAlignment(Pos.CENTER_LEFT);

                    // عرض الـ placeholder أولاً
                    carBox.getChildren().addAll(imagePlaceholder, textBox);
                    setPadding(new Insets(12, 12, 12, 0));
                    setGraphic(carBox);

                    // تحميل الصورة في الخلفية
                    String imageUrl = car.getImageUrl();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        javafx.concurrent.Task<Image> loadImageTask = new javafx.concurrent.Task<>() {
                            @Override
                            protected Image call() {
                                try {
                                    // لو بيبدأ بـ http أو https → من النت
                                    if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                                        return new Image(imageUrl, 90, 60, true, true, true);
                                    }
                                    // لو بيبدأ بـ file: → مسار كامل
                                    else if (imageUrl.startsWith("file:")) {
                                        return new Image(imageUrl, 90, 60, true, true, true);
                                    }
                                    // لو بيبدأ بـ / → من الـ resources
                                    else if (imageUrl.startsWith("/")) {
                                        return new Image(getClass().getResourceAsStream(imageUrl));
                                    }
                                    // لو مسار عادي → نجرب نحوله لـ file
                                    else {
                                        java.io.File file = new java.io.File(imageUrl);
                                        if (file.exists()) {
                                            return new Image(file.toURI().toString(), 90, 60, true, true, true);
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("Error loading image: " + e.getMessage());
                                }
                                return null;
                            }
                        };

                        loadImageTask.setOnSucceeded(event -> {
                            Image image = loadImageTask.getValue();
                            if (image != null && !image.isError()) {
                                imageView.setImage(image);
                                // استبدال الـ placeholder بالصورة
                                carBox.getChildren().set(0, imageView);
                            }
                        });

                        loadImageTask.setOnFailed(event -> {
                            System.err.println("Failed to load image for: " + car.getCarName());
                        });

                        // تشغيل الـ Task
                        Thread thread = new Thread(loadImageTask);
                        thread.setDaemon(true);
                        thread.start();
                    }
                }
            }
        });


    }

    private void loadTableData() {
        try {
            ObservableList<Car> cars = FXCollections.observableArrayList(CarDAO.getCars());
            filteredData = new FilteredList<>(cars, p -> true);
            carsTable.setItems(filteredData);
            System.out.println("✅ Loaded " + cars.size() + " cars");
        } catch (Exception e) {
            System.err.println("❌ Error loading table: " + e.getMessage());
        }
    }

    private void setupSearch() {
        if (txtSearch != null && filteredData != null) {
            txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(car -> {
                    // لو مفيش بحث - اعرض كل حاجة
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String searchLower = newValue.toLowerCase().trim();

                    // البحث في اسم العربية
                    if (car.getCarName() != null && car.getCarName().toLowerCase().contains(searchLower)) {
                        return true;
                    }

                    // البحث في البراند
                    if (car.getBrand() != null && car.getBrand().toLowerCase().contains(searchLower)) {
                        return true;
                    }

                    // البحث في الموديل
                    if (car.getModel() != null && car.getModel().toLowerCase().contains(searchLower)) {
                        return true;
                    }

                    // البحث في السنة
                    if (car.getYear() != null && car.getYear().toLowerCase().contains(searchLower)) {
                        return true;
                    }

                    // البحث في الحالة (available/rented)
                    if (car.getStatus() != null && car.getStatus().toLowerCase().contains(searchLower)) {
                        return true;
                    }

                    // البحث في السعر
                    String priceStr = String.valueOf((int) car.getPricePerDay());
                    if (priceStr.contains(searchLower)) {
                        return true;
                    }

                    // البحث في نوع الوقود
                    if (car.getFuelType() != null && car.getFuelType().toLowerCase().contains(searchLower)) {
                        return true;
                    }

                    // البحث في الترانسميشن
                    if (car.getTransmission() != null && car.getTransmission().toLowerCase().contains(searchLower)) {
                        return true;
                    }

                    // البحث في عدد المقاعد
                    String seatsStr = String.valueOf(car.getSeats());
                    if (seatsStr.contains(searchLower)) {
                        return true;
                    }

                    // البحث في الفيتشرز
                    return car.getFeatures() != null && car.getFeatures().toLowerCase().contains(searchLower);

                    // مش لاقي حاجة
                });
            });
        }
    }


    private void handleEditCar(Car car) {
        try {
            DashboardController dashboard = DashboardController.getInstance();
            if (dashboard != null) {
                dashboard.openEditCarModal(car);  // ← نفتح modal للتعديل
            }
        } catch (Exception e) {
            System.err.println("Error opening edit modal: " + e.getMessage());
        }
    }


    private void handleDeleteCar(Car car) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Car");
        confirm.setHeaderText("Delete " + car.getCarName() + "?");
        confirm.setContentText("This action cannot be undone.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                CarDAO.deleteCar(car.getCarId());
                loadTableData();
                createStatisticsCards();
            }
        });
    }

    @FXML
    private void handleAddNewCar() {
        try {
            DashboardController dashboard = DashboardController.getInstance();
            if (dashboard != null) dashboard.openAddCarModalPublic();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void refreshData() {
        createStatisticsCards();
        loadTableData();
    }
}
