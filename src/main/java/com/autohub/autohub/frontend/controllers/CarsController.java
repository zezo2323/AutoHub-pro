package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.Car;
import com.autohub.autohub.backend.models.CarStoreTemp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class CarsController implements Initializable {

    @FXML
    private Button btnAddNewCarTop;

    @FXML
    private TableView<Car> carsTable;

    @FXML
    private TableColumn<Car, String> colStatus;

    @FXML
    private TableColumn<Car, String> colSpecs;

    @FXML
    private TableColumn<Car, String> colPrice;

    @FXML
    private TableColumn<Car, String> colYear;

    @FXML
    private TableColumn<Car, String> colCategory;

    @FXML
    private TableColumn<Car, String> colName;

    @FXML
    private TableColumn<Car, Void> colActions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (carsTable != null) {

            // ربط الأعمدة بخصائص Car
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            // Specs = seats + fuel مؤقتاً
            colSpecs.setCellValueFactory(new PropertyValueFactory<>("seats"));

            colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
            colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
            colCategory.setCellValueFactory(new PropertyValueFactory<>("type"));
            colName.setCellValueFactory(new PropertyValueFactory<>("name"));

            // ربط الجدول بقائمة العربيات المؤقتة
            carsTable.setItems(CarStoreTemp.getCars());

            // عمود الأكشنز (Edit / Delete) بسيط
            setupActionsColumn();
        }
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Car, Void>, TableCell<Car, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnEdit = new Button("✏");
            private final Button btnDelete = new Button("🗑");

            {
                btnEdit.setStyle("-fx-background-color: transparent; -fx-text-fill: #4b5563;");
                btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444;");

                btnEdit.setOnAction(e -> {
                    Car car = getTableView().getItems().get(getIndex());
                    System.out.println("Edit car: " + car.getName());
                    // هنا بعدين نفتح مودال Edit
                });

                btnDelete.setOnAction(e -> {
                    Car car = getTableView().getItems().get(getIndex());
                    CarStoreTemp.getCars().remove(car);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(8, btnEdit, btnDelete);
                    setGraphic(box);
                }
            }
        };

        colActions.setCellFactory(cellFactory);
    }

    @FXML
    private void handleAddNewCarTop() {
        DashboardController dashboard = DashboardController.getInstance();
        if (dashboard != null) {
            dashboard.openAddCarModalPublic();
        }
    }
}
