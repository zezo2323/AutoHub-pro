package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.Car;
import com.autohub.autohub.backend.models.CarDAO;
import com.autohub.autohub.backend.models.Rental;
import com.autohub.autohub.backend.models.RentalDAO;
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

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

public class CreateInvoiceDialogController implements Initializable {

    @FXML
    private TextField txtCustomerName;
    @FXML
    private TextField txtCustomerEmail;
    @FXML
    private TextField txtCustomerPhone;
    @FXML
    private ComboBox<Car> cmbCar;
    @FXML
    private DatePicker dpStartDate;
    @FXML
    private DatePicker dpEndDate;
    @FXML
    private Label lblDuration;
    @FXML
    private Label lblError;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnCancel;

    // Car Preview
    @FXML
    private HBox carPreviewBox;
    @FXML
    private ImageView carImageView;
    @FXML
    private Label lblCarName;
    @FXML
    private Label lblCarDetails;
    @FXML
    private Label lblDailyRate;

    // Payment Summary
    @FXML
    private Label lblDailyRateValue;
    @FXML
    private Label lblNumDays;
    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblTax;
    @FXML
    private Label lblTotalAmount;

    private Car selectedCar = null;
    private boolean invoiceCreated = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCarComboBox();
        setupDatePickers();
        setupListeners();
    }

    private void setupCarComboBox() {
        // Load available cars only
        List<Car> availableCars = CarDAO.getAvailableCars();

        cmbCar.getItems().addAll(availableCars);

        // Custom cell factory to display car info nicely
        cmbCar.setCellFactory(param -> new ListCell<Car>() {
            @Override
            protected void updateItem(Car car, boolean empty) {
                super.updateItem(car, empty);
                if (empty || car == null) {
                    setText(null);
                } else {
                    setText(car.getBrand() + " " + car.getModel() + " (" + car.getYear() + ") - $" +
                            String.format("%.2f", car.getPricePerDay()) + "/day");
                }
            }
        });

        // Button cell (what shows when item is selected)
        cmbCar.setButtonCell(new ListCell<Car>() {
            @Override
            protected void updateItem(Car car, boolean empty) {
                super.updateItem(car, empty);
                if (empty || car == null) {
                    setText("Choose an available car");
                } else {
                    setText(car.getBrand() + " " + car.getModel() + " (" + car.getYear() + ")");
                }
            }
        });

        // Handle car selection
        cmbCar.setOnAction(event -> {
            selectedCar = cmbCar.getValue();
            if (selectedCar != null) {
                showCarPreview();
                calculateTotal();
            }
        });
    }

    private void setupDatePickers() {
        // Set min date to today
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

    private void setupListeners() {
        // Date change listeners
        dpStartDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            calculateDuration();
            calculateTotal();
        });

        dpEndDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            calculateDuration();
            calculateTotal();
        });
    }

    private void showCarPreview() {
        if (selectedCar == null) {
            carPreviewBox.setVisible(false);
            carPreviewBox.setManaged(false);
            return;
        }

        // Show car image
        try {
            String imageUrl = selectedCar.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Image image = new Image(imageUrl, true);
                carImageView.setImage(image);
            }
        } catch (Exception e) {
            System.err.println("Failed to load car image: " + e.getMessage());
        }

        // Show car details
        lblCarName.setText(selectedCar.getBrand() + " " + selectedCar.getModel());
        lblCarDetails.setText("Year: " + selectedCar.getYear() + " | " +
                selectedCar.getTransmission() + " | " +
                selectedCar.getFuelType());
        lblDailyRate.setText("$" + String.format("%.2f", selectedCar.getPricePerDay()) + " / day");

        carPreviewBox.setVisible(true);
        carPreviewBox.setManaged(true);
    }

    private void calculateDuration() {
        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();

        if (startDate != null && endDate != null && !endDate.isBefore(startDate)) {
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            lblDuration.setText(days + " days");
            lblNumDays.setText(String.valueOf(days));
        } else {
            lblDuration.setText("0 days");
            lblNumDays.setText("0");
        }
    }

    private void calculateTotal() {
        if (selectedCar == null || dpStartDate.getValue() == null || dpEndDate.getValue() == null) {
            resetPaymentSummary();
            return;
        }

        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();

        if (endDate.isBefore(startDate)) {
            resetPaymentSummary();
            return;
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        double dailyRate = selectedCar.getPricePerDay();
        double subtotal = dailyRate * days;
        double tax = subtotal * 0.10; // 10% tax
        double total = subtotal + tax;

        lblDailyRateValue.setText("$" + String.format("%.2f", dailyRate));
        lblNumDays.setText(String.valueOf(days));
        lblSubtotal.setText("$" + String.format("%.2f", subtotal));
        lblTax.setText("$" + String.format("%.2f", tax));
        lblTotalAmount.setText("$" + String.format("%.2f", total));
    }

    private void resetPaymentSummary() {
        lblDailyRateValue.setText("$0.00");
        lblNumDays.setText("0");
        lblSubtotal.setText("$0.00");
        lblTax.setText("$0.00");
        lblTotalAmount.setText("$0.00");
    }

    @FXML
    private void handleCreateInvoice() {
        hideError();

        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Create rental object
        Rental rental = new Rental();
        rental.setUserId(null); // No user_id for manual entry
        rental.setCarId(selectedCar.getCarId());
        rental.setCustomerName(txtCustomerName.getText().trim());
        rental.setCustomerEmail(txtCustomerEmail.getText().trim());
        rental.setCustomerPhone(txtCustomerPhone.getText().trim());
        rental.setStartDate(dpStartDate.getValue());
        rental.setEndDate(dpEndDate.getValue());

        // Calculate total
        long days = ChronoUnit.DAYS.between(dpStartDate.getValue(), dpEndDate.getValue());
        double subtotal = selectedCar.getPricePerDay() * days;
        double total = subtotal * 1.10; // Add 10% tax
        rental.setTotalAmount(total);

        rental.setPaymentStatus("unpaid");
        rental.setStatus("active");

        // Save to database
        int rentalId = RentalDAO.createRental(rental);

        if (rentalId > 0) {
            // Update car status to 'rented'
            CarDAO.updateCarStatus(selectedCar.getCarId(), "rented");

            // Show success message
            showSuccess();

            // Open invoice viewer
            openInvoiceViewer(rentalId);

            // Mark as created
            invoiceCreated = true;

            // Close dialog
            closeDialog();
        } else {
            showError("Failed to create rental. Please try again.");
        }
    }

    private boolean validateInputs() {
        // Customer name
        if (txtCustomerName.getText().trim().isEmpty()) {
            showError("Please enter customer name");
            txtCustomerName.requestFocus();
            return false;
        }

        // Customer email
        if (txtCustomerEmail.getText().trim().isEmpty()) {
            showError("Please enter customer email");
            txtCustomerEmail.requestFocus();
            return false;
        }

        if (!isValidEmail(txtCustomerEmail.getText().trim())) {
            showError("Please enter a valid email address");
            txtCustomerEmail.requestFocus();
            return false;
        }

        // Car selection
        if (selectedCar == null) {
            showError("Please select a car");
            cmbCar.requestFocus();
            return false;
        }

        // Start date
        if (dpStartDate.getValue() == null) {
            showError("Please select start date");
            dpStartDate.requestFocus();
            return false;
        }

        // End date
        if (dpEndDate.getValue() == null) {
            showError("Please select end date");
            dpEndDate.requestFocus();
            return false;
        }

        // Date validation
        if (dpEndDate.getValue().isBefore(dpStartDate.getValue())) {
            showError("End date must be after start date");
            dpEndDate.requestFocus();
            return false;
        }

        long days = ChronoUnit.DAYS.between(dpStartDate.getValue(), dpEndDate.getValue());
        if (days < 1) {
            showError("Rental period must be at least 1 day");
            dpEndDate.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void openInvoiceViewer(int rentalId) {
        try {
            // Get the rental with all details
            Rental rental = RentalDAO.getRentalById(rentalId);
            Car car = CarDAO.getCarById(rental.getCarId());

            // Load invoice view
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

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void hideError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
    }

    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Invoice created successfully!");
        alert.showAndWait();
    }

    private void closeDialog() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public boolean isInvoiceCreated() {
        return invoiceCreated;
    }
}
