package com.autohub.autohub.backend.models;

import com.autohub.autohub.backend.database.DBconnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class UserDAO {

    /**
     * تسجيل دخول المستخدم
     */
    public static User login(String email, String password) {
        String query = "SELECT user_id, full_name, email, password, phone, role, avatar, created_at FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setAvatar(rs.getString("avatar"));

                Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    user.setCreatedAt(timestamp.toLocalDateTime());
                }

                System.out.println("✅ Login successful: " + user.getFullName() + " (" + user.getRole() + ")");
                return user;
            } else {
                System.out.println("❌ Invalid email or password");
            }
        } catch (SQLException e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * تسجيل مستخدم جديد (Customer)
     */
    public static boolean register(String fullName, String email, String password, String phone) {
        // التحقق من وجود البريد مسبقاً
        if (isEmailExists(email)) {
            System.out.println("❌ Email already exists: " + email);
            return false;
        }

        String query = "INSERT INTO users (full_name, email, password, phone, role) VALUES (?, ?, ?, ?, 'customer')";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, phone != null && !phone.trim().isEmpty() ? phone : null);

            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("✅ User registered successfully: " + fullName + " (" + email + ")");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error during registration: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * التحقق من وجود البريد الإلكتروني
     */
    public static boolean isEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error checking email: " + e.getMessage());
        }

        return false;
    }

    // Get user by ID
    public static User getUserById(int userId) {
        String query = "SELECT user_id, full_name, email, password, phone, role, avatar, created_at FROM users WHERE user_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setAvatar(rs.getString("avatar"));

                Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    user.setCreatedAt(timestamp.toLocalDateTime());
                }

                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
        }
        return null;
    }

    // Get all users
    public static ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String query = "SELECT user_id, full_name, email, password, phone, role, avatar FROM users ORDER BY user_id DESC";

        try (Connection conn = DBconnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setAvatar(rs.getString("avatar"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }

        return users;
    }

    // Add new user
    public static boolean addUser(User user) {
        String query = "INSERT INTO users (full_name, email, password, phone, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getPhone());
            pstmt.setString(5, user.getRole());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }

    // Update user
    public static boolean updateUser(User user) {
        String query = "UPDATE users SET full_name = ?, email = ?, password = ?, phone = ?, role = ? WHERE user_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getPhone());
            pstmt.setString(5, user.getRole());
            pstmt.setInt(6, user.getUserId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    // Delete user
    public static boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    // Search users
    public static ObservableList<User> searchUsers(String keyword) {
        ObservableList<User> users = FXCollections.observableArrayList();
        String query = "SELECT user_id, full_name, email, password, phone, role, avatar FROM users " +
                "WHERE full_name LIKE ? OR email LIKE ? ORDER BY user_id DESC";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setAvatar(rs.getString("avatar"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
        }

        return users;
    }

    // Check if email exists (for updates)
    public static boolean emailExists(String email, int excludeUserId) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ? AND user_id != ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setInt(2, excludeUserId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
        }

        return false;
    }

    // Update avatar
    public static boolean updateAvatar(int userId, String avatarPath) {
        if (avatarPath == null || avatarPath.isEmpty()) {
            return false;
        }

        String normalizedPath = avatarPath.replace("\\", "/");
        String escapedPath = normalizedPath.replace("'", "''");
        String query = "UPDATE users SET avatar = ? WHERE user_id = ?";

        System.out.println("💾 Saving avatar: " + escapedPath);

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, escapedPath);
            pstmt.setInt(2, userId);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("✅ Avatar path saved successfully");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error updating avatar: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * الحصول على عدد المستخدمين الكلي (Admins + Customers)
     */
    public static int getTotalUsersCount() {
        String query = "SELECT COUNT(*) FROM users";

        try (Connection conn = DBconnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("✅ Total users: " + count);
                return count;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting total users count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

}
