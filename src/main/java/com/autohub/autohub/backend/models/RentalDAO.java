package com.autohub.autohub.backend.models;

import com.autohub.autohub.backend.database.DBconnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    /**
     * جلب كل الإيجارات مع بيانات العميل والسيارة
     */
    public static List<Rental> getAllRentals() {
        List<Rental> rentals = new ArrayList<>();
        String query = "SELECT r.*, " +
                "u.full_name as customer_name, u.email as customer_email, " +
                "c.car_name, c.brand as car_brand, c.image_url as car_image " +
                "FROM rentals r " +
                "LEFT JOIN users u ON r.user_id = u.user_id " +
                "LEFT JOIN cars c ON r.car_id = c.car_id " +
                "ORDER BY r.rental_id DESC";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Rental rental = extractRentalFromResultSet(rs);
                rentals.add(rental);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rentals: " + e.getMessage());
            e.printStackTrace();
        }
        return rentals;
    }


    /**
     * إلغاء إيجار
     */
    public static boolean cancelRental(int rentalId) {
        String query = "UPDATE rentals SET status = 'cancelled' WHERE rental_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, rentalId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // تحديث حالة السيارة لـ available
                updateCarStatusAfterCancel(conn, rentalId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error cancelling rental: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * تحديث حالة السيارة بعد إلغاء الإيجار
     */
    private static void updateCarStatusAfterCancel(Connection conn, int rentalId) {
        String query = "UPDATE cars c " +
                "JOIN rentals r ON c.car_id = r.car_id " +
                "SET c.status = 'available' " +
                "WHERE r.rental_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, rentalId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating car status: " + e.getMessage());
        }
    }

    /**
     * إحصائيات الإيجارات
     */
    public static int getTotalRentalsCount() {
        String query = "SELECT COUNT(*) FROM rentals";
        return executeCountQuery(query);
    }

    public static int getActiveRentalsCount() {
        String query = "SELECT COUNT(*) FROM rentals WHERE status = 'active'";
        return executeCountQuery(query);
    }

    public static int getCompletedRentalsCount() {
        String query = "SELECT COUNT(*) FROM rentals WHERE status = 'completed'";
        return executeCountQuery(query);
    }

    public static int getCancelledRentalsCount() {
        String query = "SELECT COUNT(*) FROM rentals WHERE status = 'cancelled'";
        return executeCountQuery(query);
    }

    public static double getTotalRevenue() {
        String query = "SELECT COALESCE(SUM(total_amount), 0) FROM rentals WHERE status != 'cancelled'";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Helper method للـ count queries
     */
    private static int executeCountQuery(String query) {
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error executing count query: " + e.getMessage());
        }
        return 0;
    }

    /**
     * استخراج بيانات Rental من ResultSet
     */
    private static Rental extractRentalFromResultSet(ResultSet rs) throws SQLException {
        Rental rental = new Rental();
        rental.setRentalId(rs.getInt("rental_id"));
        rental.setUserId(rs.getInt("user_id"));
        rental.setCarId(rs.getInt("car_id"));

        // التواريخ
        Date startDate = rs.getDate("start_date");
        Date endDate = rs.getDate("end_date");
        Date createdAt = rs.getDate("created_at");

        rental.setStartDate(startDate != null ? startDate.toLocalDate() : null);
        rental.setEndDate(endDate != null ? endDate.toLocalDate() : null);
        rental.setCreatedAt(createdAt != null ? createdAt.toLocalDate() : null);

        rental.setTotalAmount(rs.getDouble("total_amount"));
        rental.setPaymentStatus(rs.getString("payment_status"));
        rental.setStatus(rs.getString("status"));

        // بيانات العميل والسيارة
        rental.setCustomerName(rs.getString("customer_name"));
        rental.setCustomerEmail(rs.getString("customer_email"));
        rental.setCarName(rs.getString("car_name"));
        rental.setCarBrand(rs.getString("car_brand"));
        rental.setCarImageUrl(rs.getString("car_image"));

        return rental;
    }

    public static double getMonthlyRevenue() {
        String query = "SELECT COALESCE(SUM(total_amount), 0) FROM rentals " +
                "WHERE YEAR(start_date) = YEAR(CURDATE()) " +
                "AND MONTH(start_date) = MONTH(CURDATE())";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting monthly revenue: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Create a new rental with customer information
     */
    public static int createRental(Rental rental) {
        String query = "INSERT INTO rentals (user_id, car_id, customer_name, customer_email, customer_phone, " +
                "start_date, end_date, total_amount, payment_status, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            if (rental.getUserId() != null) {
                pstmt.setInt(1, rental.getUserId());
            } else {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            }

            pstmt.setInt(2, rental.getCarId());
            pstmt.setString(3, rental.getCustomerName());
            pstmt.setString(4, rental.getCustomerEmail());
            pstmt.setString(5, rental.getCustomerPhone());
            pstmt.setDate(6, java.sql.Date.valueOf(rental.getStartDate()));
            pstmt.setDate(7, java.sql.Date.valueOf(rental.getEndDate()));
            pstmt.setDouble(8, rental.getTotalAmount());
            pstmt.setString(9, rental.getPaymentStatus() != null ? rental.getPaymentStatus() : "unpaid");
            pstmt.setString(10, rental.getStatus() != null ? rental.getStatus() : "active");

            int result = pstmt.executeUpdate();

            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating rental: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Get rental by ID with all details
     */
    public static Rental getRentalById(int rentalId) {
        String query = "SELECT r.*, c.car_name, c.brand as car_brand, c.image_url as car_image " +
                "FROM rentals r " +
                "LEFT JOIN cars c ON r.car_id = c.car_id " +
                "WHERE r.rental_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, rentalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Rental rental = new Rental();
                rental.setRentalId(rs.getInt("rental_id"));

                Object userIdObj = rs.getObject("user_id");
                rental.setUserId(userIdObj != null ? (Integer) userIdObj : null);

                rental.setCarId(rs.getInt("car_id"));
                rental.setCustomerName(rs.getString("customer_name"));
                rental.setCustomerEmail(rs.getString("customer_email"));
                rental.setCustomerPhone(rs.getString("customer_phone"));
                rental.setStartDate(rs.getDate("start_date").toLocalDate());
                rental.setEndDate(rs.getDate("end_date").toLocalDate());
                rental.setTotalAmount(rs.getDouble("total_amount"));
                rental.setPaymentStatus(rs.getString("payment_status"));
                rental.setStatus(rs.getString("status"));
                rental.setCreatedAt(rs.getDate("created_at").toLocalDate());
                rental.setCarName(rs.getString("car_name"));
                rental.setCarBrand(rs.getString("car_brand"));
                rental.setCarImageUrl(rs.getString("car_image"));

                return rental;
            }

        } catch (SQLException e) {
            System.err.println("Error getting rental: " + e.getMessage());
        }

        return null;
    }

    /**
     * جلب الإيجارات الخاصة بمستخدم معين
     */
    public static List<Rental> getRentalsByUserId(int userId) {
        List<Rental> rentals = new ArrayList<>();
        String query = "SELECT r.*, " +
                "u.full_name as customer_name, u.email as customer_email, " +
                "c.car_name, c.brand as car_brand, c.image_url as car_image " +
                "FROM rentals r " +
                "LEFT JOIN users u ON r.user_id = u.user_id " +
                "LEFT JOIN cars c ON r.car_id = c.car_id " +
                "WHERE r.user_id = ? " +
                "ORDER BY r.rental_id DESC";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Rental rental = extractRentalFromResultSet(rs);
                rentals.add(rental);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user rentals: " + e.getMessage());
            e.printStackTrace();
        }

        return rentals;
    }

    /**
     * إحصائيات خاصة بمستخدم معين
     */
    public static int getActiveRentalsCountByUserId(int userId) {
        String query = "SELECT COUNT(*) FROM rentals WHERE user_id = ? AND status = 'active'";
        return executeCountQueryWithParam(query, userId);
    }

    public static int getCompletedRentalsCountByUserId(int userId) {
        String query = "SELECT COUNT(*) FROM rentals WHERE user_id = ? AND status = 'completed'";
        return executeCountQueryWithParam(query, userId);
    }

    public static int getCancelledRentalsCountByUserId(int userId) {
        String query = "SELECT COUNT(*) FROM rentals WHERE user_id = ? AND status = 'cancelled'";
        return executeCountQueryWithParam(query, userId);
    }

    public static double getTotalRevenueByUserId(int userId) {
        String query = "SELECT COALESCE(SUM(total_amount), 0) FROM rentals WHERE user_id = ? AND status != 'cancelled'";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting user revenue: " + e.getMessage());
        }

        return 0.0;
    }

    /**
     * Helper method للـ count queries مع parameter
     */
    private static int executeCountQueryWithParam(String query, int userId) {
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error executing count query: " + e.getMessage());
        }

        return 0;
    }


}
