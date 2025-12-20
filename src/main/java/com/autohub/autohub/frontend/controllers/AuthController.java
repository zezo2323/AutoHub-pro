package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AuthController {

    @FXML
    private VBox signInPane;
    @FXML
    private VBox signUpPane;
    @FXML
    private Label lblRating;    // للـ Rating (4.9)
    @FXML
    private Label lblCustomers; // للـ Customers (+1000)
    @FXML
    private Label lblCars;      // للـ Cars (+50)

    // Sign In Fields
    @FXML
    private TextField txtLoginEmail;
    @FXML
    private PasswordField txtLoginPassword;
    @FXML
    private Label lblLoginError;

    // Sign Up Fields
    @FXML
    private TextField txtRegisterName;
    @FXML
    private TextField txtRegisterEmail;
    @FXML
    private PasswordField txtRegisterPassword;
    @FXML
    private TextField txtRegisterPhone;
    @FXML
    private Label lblRegisterError;

    @FXML
    private void initialize() {

        System.out.println("✅ AuthController initialized");

        // إخفاء رسائل الخطأ في البداية
        if (lblLoginError != null) lblLoginError.setVisible(false);
        if (lblRegisterError != null) lblRegisterError.setVisible(false);
        loadStatistics();
    }


    @FXML
    private void showSignUp() {
        signInPane.setVisible(false);
        signUpPane.setVisible(true);
        clearLoginFields();
        if (lblLoginError != null) lblLoginError.setVisible(false);
    }

    @FXML
    private void showSignIn() {
        signUpPane.setVisible(false);
        signInPane.setVisible(true);
        clearRegisterFields();
        if (lblRegisterError != null) lblRegisterError.setVisible(false);
    }

    @FXML
    private void handleSignIn() {
        // إخفاء رسالة الخطأ السابقة
        if (lblLoginError != null) lblLoginError.setVisible(false);

        String email = txtLoginEmail.getText().trim();
        String password = txtLoginPassword.getText().trim();

        // التحقق من الحقول
        if (email.isEmpty() || password.isEmpty()) {
            showLoginError("Please fill in all fields");
            return;
        }

        // التحقق من البريد الإلكتروني
        if (!isValidEmail(email)) {
            showLoginError("Invalid email format");
            return;
        }

        // محاولة تسجيل الدخول
        User user = UserDAO.login(email, password);

        if (user != null) {
            System.out.println("✅ Login successful: " + user.getFullName() + " - Role: " + user.getRole());

            // حفظ بيانات اليوزر في Session
            SessionManager.setCurrentUser(user);

            // فتح الصفحة المناسبة حسب الـ role
            if ("admin".equalsIgnoreCase(user.getRole())) {
                openDashboard();
            } else {
                openUserBrowseCars();
            }
        } else {
            showLoginError("Invalid email or password");
        }
    }

    @FXML
    private void handleSignUp() {
        System.out.println("🔵 Sign Up button clicked");

        // إخفاء رسالة الخطأ السابقة
        if (lblRegisterError != null) lblRegisterError.setVisible(false);

        String fullName = txtRegisterName.getText().trim();
        String email = txtRegisterEmail.getText().trim();
        String password = txtRegisterPassword.getText().trim();
        String phone = txtRegisterPhone.getText().trim();

        System.out.println("📝 Form Data:");
        System.out.println("   Full Name: " + fullName);
        System.out.println("   Email: " + email);
        System.out.println("   Password Length: " + password.length());
        System.out.println("   Phone: " + phone);

        // التحقق من الحقول الإجبارية
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Validation failed: Empty fields");
            showRegisterError("Please fill in all required fields");
            return;
        }

        // التحقق من البريد الإلكتروني
        if (!isValidEmail(email)) {
            System.out.println("❌ Validation failed: Invalid email format");
            showRegisterError("Invalid email format");
            return;
        }

        // التحقق من قوة كلمة المرور
        if (password.length() < 6) {
            System.out.println("❌ Validation failed: Password too short");
            showRegisterError("Password must be at least 6 characters");
            return;
        }

        System.out.println("✅ Validation passed - Attempting registration...");

        // محاولة التسجيل
        boolean success = UserDAO.register(fullName, email, password, phone);

        System.out.println("📊 Registration Result: " + (success ? "SUCCESS" : "FAILED"));

        if (success) {
            System.out.println("✅ Registration successful!");

            // رسالة نجاح
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Account Created Successfully!");
            alert.setContentText("Welcome " + fullName + "!\n\nYour account has been created.\nYou can now sign in with your credentials.");

            // تنسيق الـ Alert
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-font-family: 'Segoe UI';");

            alert.showAndWait();

            System.out.println("✅ Success alert shown");

            // مسح الحقول
            clearRegisterFields();

            // الانتقال لصفحة تسجيل الدخول
            showSignIn();

            System.out.println("✅ Redirected to Sign In page");
        } else {
            System.out.println("❌ Registration failed - Email might already exist");
            showRegisterError("Email already exists. Please use a different email.");
        }
    }

    private void showLoginError(String message) {
        if (lblLoginError != null) {
            lblLoginError.setText(message);
            lblLoginError.setVisible(true);
        }
    }

    private void showRegisterError(String message) {
        if (lblRegisterError != null) {
            lblRegisterError.setText(message);
            lblRegisterError.setVisible(true);
        }
        System.out.println("❌ Error shown: " + message);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private void clearLoginFields() {
        if (txtLoginEmail != null) txtLoginEmail.clear();
        if (txtLoginPassword != null) txtLoginPassword.clear();
    }

    private void clearRegisterFields() {
        if (txtRegisterName != null) txtRegisterName.clear();
        if (txtRegisterEmail != null) txtRegisterEmail.clear();
        if (txtRegisterPassword != null) txtRegisterPassword.clear();
        if (txtRegisterPhone != null) txtRegisterPhone.clear();
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) signInPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("DriveNow - Admin Dashboard");
            stage.setMaximized(true);
            stage.show();

            System.out.println("✅ Dashboard opened for Admin");
        } catch (Exception e) {
            e.printStackTrace();
            showLoginError("Error opening dashboard: " + e.getMessage());
        }
    }

    private void openUserBrowseCars() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserBrowseCars.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) signInPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("DriveNow - Browse Cars");
            stage.setMaximized(true);
            stage.show();

            System.out.println("✅ User Browse Cars opened for Customer");
        } catch (Exception e) {
            e.printStackTrace();
            showLoginError("Error opening browse cars page: " + e.getMessage());
        }
    }

    private void loadStatistics() {
        try {
            // جلب البيانات من الـ Database
            int totalUsers = UserDAO.getTotalUsersCount();

            int totalCars = CarDAO.getTotalCarsCount();
            double averageRating = CommentDAO.getAverageRating();

            // تحديث الـ Labels
            if (lblRating != null) {
                lblRating.setText(String.format("%.1f", averageRating));
            }

            if (lblCustomers != null) {
                lblCustomers.setText("+" + totalUsers);
            }

            if (lblCars != null) {
                lblCars.setText("+" + totalCars);
            }

            System.out.println("✅ Statistics loaded: Rating=" + averageRating + ", Customers=" + totalUsers + ", Cars=" + totalCars);

        } catch (Exception e) {
            System.err.println("❌ Error loading statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
