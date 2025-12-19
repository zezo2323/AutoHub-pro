package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.User;
import com.autohub.autohub.backend.models.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UserDialogController implements Initializable {

    @FXML
    private Label lblTitle;
    @FXML
    private TextField txtFullName;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtPhone;
    @FXML
    private ComboBox<String> cmbRole;
    @FXML
    private Label lblError;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    private User userToEdit = null;
    private boolean isEditMode = false;
    private boolean saveSuccessful = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupRoleComboBox();
    }

    private void setupRoleComboBox() {
        cmbRole.getItems().addAll("user", "admin");
        cmbRole.setValue("user");
    }

    // Set user for editing
    public void setUser(User user) {
        if (user != null) {
            this.userToEdit = user;
            this.isEditMode = true;

            lblTitle.setText("Edit User");
            btnSave.setText("Update User");

            // Fill fields with user data
            txtFullName.setText(user.getFullName());
            txtEmail.setText(user.getEmail());
            txtPassword.setText(user.getPassword());
            txtPhone.setText(user.getPhone());
            cmbRole.setValue(user.getRole());
        }
    }

    @FXML
    private void handleSave() {
        // Hide previous error
        hideError();

        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        if (isEditMode) {
            updateUser();
        } else {
            addUser();
        }
    }

    private boolean validateInputs() {
        // Full Name validation
        if (txtFullName.getText().trim().isEmpty()) {
            showError("Please enter full name");
            txtFullName.requestFocus();
            return false;
        }

        // Email validation
        if (txtEmail.getText().trim().isEmpty()) {
            showError("Please enter email address");
            txtEmail.requestFocus();
            return false;
        }

        if (!isValidEmail(txtEmail.getText().trim())) {
            showError("Please enter a valid email address");
            txtEmail.requestFocus();
            return false;
        }

        // Check if email exists (exclude current user in edit mode)
        int excludeUserId = isEditMode ? userToEdit.getUserId() : 0;
        if (UserDAO.emailExists(txtEmail.getText().trim(), excludeUserId)) {
            showError("Email already exists");
            txtEmail.requestFocus();
            return false;
        }

        // Password validation
        if (txtPassword.getText().trim().isEmpty()) {
            showError("Please enter password");
            txtPassword.requestFocus();
            return false;
        }

        if (txtPassword.getText().length() < 6) {
            showError("Password must be at least 6 characters");
            txtPassword.requestFocus();
            return false;
        }

        // Role validation
        if (cmbRole.getValue() == null || cmbRole.getValue().isEmpty()) {
            showError("Please select a role");
            cmbRole.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void addUser() {
        User newUser = new User(
                0, // ID will be auto-generated
                txtFullName.getText().trim(),
                txtEmail.getText().trim(),
                txtPassword.getText().trim(),
                txtPhone.getText().trim(),
                cmbRole.getValue(),
                null // avatar
        );

        if (UserDAO.addUser(newUser)) {
            saveSuccessful = true;
            showSuccessAndClose("User added successfully!");
        } else {
            showError("Failed to add user. Please try again.");
        }
    }

    private void updateUser() {
        userToEdit.setFullName(txtFullName.getText().trim());
        userToEdit.setEmail(txtEmail.getText().trim());
        userToEdit.setPassword(txtPassword.getText().trim());
        userToEdit.setPhone(txtPhone.getText().trim());
        userToEdit.setRole(cmbRole.getValue());

        if (UserDAO.updateUser(userToEdit)) {
            saveSuccessful = true;
            showSuccessAndClose("User updated successfully!");
        } else {
            showError("Failed to update user. Please try again.");
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

    private void showSuccessAndClose(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public boolean isSaveSuccessful() {
        return saveSuccessful;
    }
}
