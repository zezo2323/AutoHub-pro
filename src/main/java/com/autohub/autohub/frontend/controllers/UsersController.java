package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.User;
import com.autohub.autohub.backend.models.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UsersController implements Initializable {

    private final ObservableList<User> allUsers = FXCollections.observableArrayList();
    private final ObservableList<User> filteredUsers = FXCollections.observableArrayList();

    @FXML
    private HBox statsRow;
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cmbRoleFilter;
    @FXML
    private Button btnClearFilter;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnAddUser;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Void> colUserId;
    @FXML
    private TableColumn<User, Void> colName;
    @FXML
    private TableColumn<User, Void> colEmail;
    @FXML
    private TableColumn<User, Void> colPhone;
    @FXML
    private TableColumn<User, Void> colRole;
    @FXML
    private TableColumn<User, Void> colCreatedAt;
    @FXML
    private TableColumn<User, Void> colActions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupRoleFilter();
        setupTableColumns();
        loadUsers();
        setupSearchAndFilter();
        setupStatsCards();
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void setupStatsCards() {
        statsRow.getChildren().clear();

        int totalUsers = allUsers.size();
        long adminCount = allUsers.stream()
                .filter(u -> "admin".equalsIgnoreCase(u.getRole())).count();
        long userCount = allUsers.stream()
                .filter(u -> "user".equalsIgnoreCase(u.getRole())).count();

        VBox card1 = createStatCard(String.valueOf(totalUsers), "Total Users",
                "#3b82f6", "fas-users");
        VBox card2 = createStatCard(String.valueOf(adminCount), "Admins",
                "#ef4444", "fas-user-shield");
        VBox card3 = createStatCard(String.valueOf(userCount), "Regular Users",
                "#10b981", "fas-user");
        VBox card4 = createStatCard(String.valueOf(totalUsers), "All Active",
                "#8b5cf6", "fas-user-check");

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);

        statsRow.getChildren().addAll(card1, card2, card3, card4);
    }

    private VBox createStatCard(String value, String label, String color, String icon) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-padding: 24;");
        card.setMaxWidth(Double.MAX_VALUE);

        FontIcon iconNode = new FontIcon(icon);
        iconNode.setIconSize(40);
        iconNode.setIconColor(Color.valueOf(color));
        iconNode.setOpacity(0.15);

        Label labelLbl = new Label(label);
        labelLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

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

    private void setupRoleFilter() {
        cmbRoleFilter.getItems().addAll("All", "Admin", "User");
        cmbRoleFilter.setValue("All");
        cmbRoleFilter.setOnAction(event -> applyFilters());
    }

    private void setupSearchAndFilter() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String searchText = txtSearch.getText().toLowerCase().trim();
        String roleFilter = cmbRoleFilter.getValue();

        filteredUsers.clear();

        List<User> filtered = allUsers.stream()
                .filter(user -> {
                    boolean matchesSearch = searchText.isEmpty() ||
                            user.getFullName().toLowerCase().contains(searchText) ||
                            user.getEmail().toLowerCase().contains(searchText) ||
                            (user.getPhone() != null && user.getPhone().toLowerCase().contains(searchText));

                    boolean matchesRole = roleFilter.equals("All") ||
                            user.getRole().equalsIgnoreCase(roleFilter);

                    return matchesSearch && matchesRole;
                })
                .toList();

        filteredUsers.addAll(filtered);
        usersTable.setItems(filteredUsers);
    }

    @FXML
    private void handleClearFilters() {
        txtSearch.clear();
        cmbRoleFilter.setValue("All");
        applyFilters();
    }

    @FXML
    private void handleRefresh() {
        loadUsers();
        setupStatsCards();
        showAlert(Alert.AlertType.INFORMATION, "Refreshed", "Users list refreshed successfully");
    }

    @FXML
    private void handleAddUser() {
        showAddUserDialog();
    }

    private void setupTableColumns() {
        colUserId.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    Label idLabel = new Label("#" + user.getUserId());
                    idLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #64748b;");
                    setAlignment(Pos.CENTER);
                    setGraphic(idLabel);
                }
            }
        });

        colName.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    Label nameLabel = new Label(user.getFullName());
                    nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #0f172a;");
                    setGraphic(nameLabel);
                    setPadding(new Insets(8));
                }
            }
        });

        colEmail.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    Label emailLabel = new Label(user.getEmail());
                    emailLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
                    setGraphic(emailLabel);
                    setPadding(new Insets(8));
                }
            }
        });

        colPhone.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    Label phoneLabel = new Label(user.getPhone() != null ? user.getPhone() : "N/A");
                    phoneLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
                    setAlignment(Pos.CENTER);
                    setGraphic(phoneLabel);
                }
            }
        });

        colRole.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    Label roleLabel = new Label(user.getRole().toUpperCase());

                    String bgColor = user.getRole().equalsIgnoreCase("admin") ? "#ef4444" : "#10b981";

                    roleLabel.setStyle("-fx-background-color: " + bgColor + "; " +
                            "-fx-text-fill: white; -fx-padding: 6 14; " +
                            "-fx-background-radius: 20; -fx-font-size: 11px; " +
                            "-fx-font-weight: bold;");
                    setAlignment(Pos.CENTER);
                    setGraphic(roleLabel);
                }
            }
        });

        colCreatedAt.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Label dateLabel = new Label("N/A");
                    dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
                    setAlignment(Pos.CENTER);
                    setGraphic(dateLabel);
                }
            }
        });

        colActions.setCellFactory(column -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox actionBox = new HBox(8, editBtn, deleteBtn);

            {
                FontIcon editIcon = new FontIcon("fas-edit");
                editIcon.setIconSize(14);
                editIcon.setIconColor(Color.WHITE);
                editBtn.setGraphic(editIcon);
                editBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; " +
                        "-fx-padding: 6 12; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: 600;");

                FontIcon deleteIcon = new FontIcon("fas-trash");
                deleteIcon.setIconSize(14);
                deleteIcon.setIconColor(Color.WHITE);
                deleteBtn.setGraphic(deleteIcon);
                deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                        "-fx-padding: 6 12; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: 600;");

                actionBox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    User currentUser = getTableView().getItems().get(getIndex());
                    editBtn.setOnAction(event -> handleEditUser(currentUser));
                    deleteBtn.setOnAction(event -> handleDeleteUser(currentUser));
                    setAlignment(Pos.CENTER);
                    setGraphic(actionBox);
                }
            }
        });
    }

    private void loadUsers() {
        allUsers.clear();
        allUsers.addAll(UserDAO.getAllUsers());
        filteredUsers.clear();
        filteredUsers.addAll(allUsers);
        usersTable.setItems(filteredUsers);
    }

    private void handleEditUser(User user) {
        showEditUserDialog(user);
    }

    private void handleDeleteUser(User user) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Are you sure you want to delete this user?");
        confirmAlert.setContentText("User: " + user.getFullName() + "\nEmail: " + user.getEmail());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (UserDAO.deleteUser(user.getUserId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully");
                loadUsers();
                setupStatsCards();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user");
            }
        }
    }

    private void showAddUserDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user_dialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New User");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            // Reload data after dialog closes
            UserDialogController controller = loader.getController();
            if (controller.isSaveSuccessful()) {
                loadUsers();
                setupStatsCards();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open add user dialog: " + e.getMessage());
        }
    }

    private void showEditUserDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user_dialog.fxml"));
            Parent root = loader.load();

            // Pass user data to dialog
            UserDialogController controller = loader.getController();
            controller.setUser(user);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit User");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            // Reload data after dialog closes
            if (controller.isSaveSuccessful()) {
                loadUsers();
                setupStatsCards();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open edit user dialog: " + e.getMessage());
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
