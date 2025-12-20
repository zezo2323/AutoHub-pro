package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class BookingController implements Initializable {

    // Date Pickers
    @FXML
    private DatePicker dpStartDate;

    @FXML
    private DatePicker dpEndDate;

    // Time Fields
    @FXML
    private TextField txtPickupTime;

    @FXML
    private TextField txtReturnTime;

    // Customer Info
    @FXML
    private Label lblCustomerName;

    @FXML
    private Label lblCustomerEmail;

    @FXML
    private Label lblCustomerPhone;

    // Car Info
    @FXML
    private ImageView carImageView;

    @FXML
    private Label lblCarName;

    @FXML
    private Label lblCarDetails;

    @FXML
    private Label lblDailyRate;

    @FXML
    private HBox featuresBox;

    // Button
    @FXML
    private Button btnConfirmBooking;

    @FXML
    private Label lblBackToCars;

    // Data
    private Car selectedCar;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDatePickers();
        setupListeners();
        loadCurrentUser();
    }

    /**
     * استقبال بيانات العربية من الصفحة السابقة
     */
    public void setCar(Car car) {
        this.selectedCar = car;
        loadCarDetails();
    }

    /**
     * جلب بيانات اليوزر الحالي
     */
    private void loadCurrentUser() {
        currentUser = SessionManager.getCurrentUser();

        if (currentUser != null) {
            lblCustomerName.setText(currentUser.getFullName());
            lblCustomerEmail.setText(currentUser.getEmail());
            lblCustomerPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "N/A");
        } else {
            lblCustomerName.setText("Guest User");
            lblCustomerEmail.setText("guest@example.com");
            lblCustomerPhone.setText("N/A");
        }
    }

    /**
     * تحميل تفاصيل العربية
     */
    private void loadCarDetails() {
        if (selectedCar == null) return;

        // Car Name
        String carFullName = (selectedCar.getBrand() != null ? selectedCar.getBrand() + " " : "") +
                (selectedCar.getModel() != null ? selectedCar.getModel() : "");
        lblCarName.setText(carFullName.isEmpty() ? "Unknown Car" : carFullName);

        // Car Details
        String details = (selectedCar.getBrand() != null ? selectedCar.getBrand() : "") + " " +
                (selectedCar.getCarName() != null ? selectedCar.getCarName() : "") + " • " +
                (selectedCar.getYear() != null ? selectedCar.getYear() : "N/A");
        lblCarDetails.setText(details);

        // Daily Rate
        lblDailyRate.setText("$" + String.format("%.0f", selectedCar.getPricePerDay()));

        // Load Car Image
        loadCarImage();

        // Load Features
        loadFeatures();
    }

    /**
     * تحميل صورة العربية
     */
    private void loadCarImage() {
        try {
            String imageUrl = selectedCar.getImageUrl();

            if (imageUrl != null && !imageUrl.isEmpty()) {
                // لو URL من الإنترنت
                if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                    Image image = new Image(imageUrl, true);
                    carImageView.setImage(image);
                }
                // لو مسار ملف محلي
                else if (new File(imageUrl).exists()) {
                    Image image = new Image(new File(imageUrl).toURI().toString());
                    carImageView.setImage(image);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load car image: " + e.getMessage());
        }
    }

    /**
     * تحميل المميزات
     */
    private void loadFeatures() {
        featuresBox.getChildren().clear();

        if (selectedCar.getFeatures() != null && !selectedCar.getFeatures().isEmpty()) {
            String[] features = selectedCar.getFeatures().split(",");

            for (String feature : features) {
                if (features.length <= 4) { // عرض أول 4 مميزات بس
                    Label featureLabel = new Label(feature.trim() + " ✓");
                    featureLabel.setStyle("-fx-background-color: #F5F5F5; -fx-padding: 5 15; " +
                            "-fx-background-radius: 15; -fx-text-fill: #616161;");
                    featuresBox.getChildren().add(featureLabel);
                }
            }
        } else {
            // مميزات افتراضية
            String[] defaultFeatures = {"Leather Seats ✓", "Navigation ✓", "Sunroof ✓", "Bluetooth ✓"};
            for (String feature : defaultFeatures) {
                Label featureLabel = new Label(feature);
                featureLabel.setStyle("-fx-background-color: #F5F5F5; -fx-padding: 5 15; " +
                        "-fx-background-radius: 15; -fx-text-fill: #616161;");
                featuresBox.getChildren().add(featureLabel);
            }
        }
    }

    /**
     * إعداد الـ DatePickers
     */
    private void setupDatePickers() {
        // منع اختيار تاريخ قبل اليوم
        dpStartDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        dpEndDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate startDate = dpStartDate.getValue();
                setDisable(empty || (startDate != null && date.isBefore(startDate.plusDays(1))));
            }
        });
    }

    /**
     * إعداد الـ Listeners
     */
    private void setupListeners() {
        // عند تغيير التواريخ - تحديث السعر الإجمالي
        dpStartDate.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        dpEndDate.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
    }

    /**
     * تحديث السعر الإجمالي على الزرار
     */
    private void updateTotalPrice() {
        if (dpStartDate.getValue() != null && dpEndDate.getValue() != null && selectedCar != null) {
            LocalDate start = dpStartDate.getValue();
            LocalDate end = dpEndDate.getValue();

            if (!end.isBefore(start)) {
                long days = ChronoUnit.DAYS.between(start, end);
                double subtotal = selectedCar.getPricePerDay() * days;
                double total = subtotal * 1.10; // إضافة 10% ضريبة

                btnConfirmBooking.setText("Confirm Booking $" + String.format("%.0f", total));
                btnConfirmBooking.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; " +
                        "-fx-background-radius: 10; -fx-cursor: hand;");
            } else {
                btnConfirmBooking.setText("Confirm Booking $0");
            }
        }
    }

    /**
     * معالج زر Confirm Booking
     */
    @FXML
    private void handleConfirmBooking() {
        // التحقق من البيانات
        if (!validateInputs()) {
            return;
        }

        try {
            // إنشاء Rental جديد
            Rental rental = new Rental();
            rental.setUserId(currentUser != null ? currentUser.getUserId() : null);
            rental.setCarId(selectedCar.getCarId());
            rental.setCustomerName(currentUser != null ? currentUser.getFullName() : "Guest");
            rental.setCustomerEmail(currentUser != null ? currentUser.getEmail() : "guest@example.com");
            rental.setCustomerPhone(currentUser != null ? currentUser.getPhone() : "N/A");
            rental.setStartDate(dpStartDate.getValue());
            rental.setEndDate(dpEndDate.getValue());

            // حساب المبلغ الإجمالي
            long days = ChronoUnit.DAYS.between(dpStartDate.getValue(), dpEndDate.getValue());
            double subtotal = selectedCar.getPricePerDay() * days;
            double total = subtotal * 1.10; // إضافة 10% ضريبة
            rental.setTotalAmount(total);

            rental.setPaymentStatus("unpaid");
            rental.setStatus("active");

            // حفظ في الـ Database
            int rentalId = RentalDAO.createRental(rental);

            if (rentalId > 0) {
                // تحديث حالة العربية لـ "rented"
                CarDAO.updateCarStatus(selectedCar.getCarId(), "rented");

                // رسالة نجاح
                showSuccess("Booking confirmed successfully!");

                // فتح صفحة الفاتورة
                openInvoiceViewer(rentalId);

                // إغلاق صفحة Booking
                closeBookingPage();
            } else {
                showError("Failed to create booking. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error: " + e.getMessage());
        }
    }

    /**
     * التحقق من صحة البيانات المدخلة
     */
    private boolean validateInputs() {
        // التحقق من تسجيل الدخول
        if (currentUser == null) {
            showError("Please login first to book a car");
            return false;
        }

        // التحقق من اختيار العربية
        if (selectedCar == null) {
            showError("No car selected");
            return false;
        }

        // التحقق من Start Date
        if (dpStartDate.getValue() == null) {
            showError("Please select start date");
            dpStartDate.requestFocus();
            return false;
        }

        // التحقق من End Date
        if (dpEndDate.getValue() == null) {
            showError("Please select end date");
            dpEndDate.requestFocus();
            return false;
        }

        // التحقق من صحة التواريخ
        if (dpEndDate.getValue().isBefore(dpStartDate.getValue())) {
            showError("End date must be after start date");
            dpEndDate.requestFocus();
            return false;
        }

        // التحقق من مدة الإيجار (على الأقل يوم واحد)
        long days = ChronoUnit.DAYS.between(dpStartDate.getValue(), dpEndDate.getValue());
        if (days < 1) {
            showError("Rental period must be at least 1 day");
            dpEndDate.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * فتح صفحة عرض الفاتورة
     */
    private void openInvoiceViewer(int rentalId) {
        try {
            // جلب بيانات الـ Rental
            Rental rental = RentalDAO.getRentalById(rentalId);
            Car car = CarDAO.getCarById(rental.getCarId());

            // تحميل صفحة الفاتورة
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/invoice.fxml"));
            Parent invoiceRoot = loader.load();

            InvoiceController invoiceController = loader.getController();
            invoiceController.setRental(rental, car);

            Stage invoiceStage = new Stage();
            invoiceStage.setTitle("Invoice - R" + rentalId);
            invoiceStage.setScene(new Scene(invoiceRoot));
            invoiceStage.initModality(Modality.APPLICATION_MODAL);
            invoiceStage.setResizable(false);
            invoiceStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open invoice: " + e.getMessage());
        }
    }

    /**
     * الرجوع للصفحة السابقة
     */
    @FXML
    private void handleBackToCars() {
        closeBookingPage();
    }

    /**
     * إغلاق صفحة Booking
     */
    private void closeBookingPage() {
        Stage stage = (Stage) btnConfirmBooking.getScene().getWindow();
        stage.close();
    }

    /**
     * عرض رسالة نجاح
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * عرض رسالة خطأ
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
