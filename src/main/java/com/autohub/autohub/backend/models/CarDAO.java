package com.autohub.autohub.backend.models;

import com.autohub.autohub.backend.database.DBconnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {
    private static final ObservableList<Car> carsList = FXCollections.observableArrayList();

    // جلب جميع السيارات من الداتا بيز
    public static List<Car> getCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars";

        try (Connection conn = DBconnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Car car = new Car();
                car.setCarId(rs.getInt("car_id"));
                car.setCarName(rs.getString("car_name"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getString("year"));
                car.setPricePerDay(rs.getDouble("price_per_day"));
                car.setSeats(rs.getInt("seats"));
                car.setTransmission(rs.getString("transmission"));
                car.setFuelType(rs.getString("fuel_type"));
                car.setImageUrl(rs.getString("image_url"));
                car.setStatus(rs.getString("status"));
                car.setDescription(rs.getString("description"));
                car.setFeatures(rs.getString("features"));

                cars.add(car);
            }

        } catch (SQLException e) {
            System.err.println("Error loading cars: " + e.getMessage());
            e.printStackTrace();
        }

        return cars;
    }


    public static boolean addCar(Car car) {
        String sql = "INSERT INTO cars (car_name, brand, model, year, price_per_day, seats, transmission, fuel_type, image_url, status, description, features) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, car.getCarName());
            pstmt.setString(2, car.getBrand());
            pstmt.setString(3, car.getModel());
            pstmt.setString(4, car.getYear());
            pstmt.setDouble(5, car.getPricePerDay());
            pstmt.setInt(6, car.getSeats());
            pstmt.setString(7, car.getTransmission());
            pstmt.setString(8, car.getFuelType());
            pstmt.setString(9, car.getImageUrl());
            pstmt.setString(10, car.getStatus());
            pstmt.setString(11, car.getDescription());
            pstmt.setString(12, car.getFeatures());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Rows affected: " + rowsAffected);

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public static boolean updateCar(Car car) {
        String sql = "UPDATE cars SET car_name=?, brand=?, model=?, year=?, price_per_day=?, " +
                "seats=?, transmission=?, fuel_type=?, image_url=?, status=?, features=? " +
                "WHERE car_id=?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, car.getCarName());
            pstmt.setString(2, car.getBrand());
            pstmt.setString(3, car.getModel());
            pstmt.setString(4, car.getYear());
            pstmt.setDouble(5, car.getPricePerDay());
            pstmt.setInt(6, car.getSeats());
            pstmt.setString(7, car.getTransmission());
            pstmt.setString(8, car.getFuelType());
            pstmt.setString(9, car.getImageUrl());
            pstmt.setString(10, car.getStatus());
            pstmt.setString(11, car.getFeatures());
            pstmt.setInt(12, car.getCarId());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Rows updated: " + rowsAffected);

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ SQL Error while updating car: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // حذف سيارة
    public static boolean deleteCar(int carId) {
        String query = "DELETE FROM cars WHERE car_id=?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, carId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                getCars(); // تحديث الليست
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting car: " + e.getMessage());
        }

        return false;
    }

    // ============================================
// DASHBOARD STATISTICS METHODS
// ============================================

    /**
     * الحصول على عدد السيارات الكلي
     */
    public static int getTotalCarsCount() {
        String query = "SELECT COUNT(*) FROM cars";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total cars count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * الحصول على عدد السيارات المؤجرة
     */
    public static int getRentedCarsCount() {
        String query = "SELECT COUNT(*) FROM cars WHERE status = 'rented'";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting rented cars count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * الحصول على متوسط سعر التأجير
     */
    public static double getAveragePricePerDay() {
        String query = "SELECT AVG(price_per_day) FROM cars";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting average price: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * الحصول على إيرادات الأسبوع الحالي
     */
    public static double getWeeklyRevenue() {
        String query = "SELECT COALESCE(SUM(total_amount), 0) FROM rentals " +
                "WHERE YEARWEEK(start_date, 1) = YEARWEEK(CURDATE(), 1)";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting weekly revenue: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * الحصول على أشهر السيارات (الأكثر إيجاراً)
     *
     * @param limit عدد السيارات المطلوبة
     */
    public static List<Car> getMostRentedCars(int limit) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.*, COUNT(r.rental_id) as rental_count " +
                "FROM cars c " +
                "LEFT JOIN rentals r ON c.car_id = r.car_id " +
                "GROUP BY c.car_id " +
                "ORDER BY rental_count DESC " +
                "LIMIT ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Car car = new Car();
                car.setCarId(rs.getInt("car_id"));
                car.setCarName(rs.getString("car_name"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getString("year"));
                car.setPricePerDay(rs.getDouble("price_per_day"));
                car.setSeats(rs.getInt("seats"));
                car.setTransmission(rs.getString("transmission"));
                car.setFuelType(rs.getString("fuel_type"));
                car.setImageUrl(rs.getString("image_url"));
                car.setStatus(rs.getString("status"));
                car.setFeatures(rs.getString("features"));
                cars.add(car);
            }
        } catch (SQLException e) {
            System.err.println("Error getting most rented cars: " + e.getMessage());
            e.printStackTrace();
        }
        return cars;
    }


}
