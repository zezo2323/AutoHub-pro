package com.autohub.autohub.backend.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CarStoreTemp {

    private static final ObservableList<Car> CARS =
            FXCollections.observableArrayList();

    public static ObservableList<Car> getCars() {
        return CARS;
    }

    public static void addCar(Car car) {
        CARS.add(car);
    }
}
