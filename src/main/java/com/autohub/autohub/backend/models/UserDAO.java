package com.autohub.autohub.backend.models;

import com.autohub.autohub.backend.database.DBconnector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public static User getUserById(int userId) {
        String query = "SELECT user_id, full_name, email, phone, avatar FROM users WHERE user_id = " + userId;

        try (Connection conn = DBconnector.getConnection()) {
            if (conn != null) {
                ResultSet rs = conn.createStatement().executeQuery(query);
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setAvatar(rs.getString("avatar"));
                    return user;
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
        }
        return null;
    }

    public static boolean updateUser(User user) {
        String query = "UPDATE users SET full_name = '" + user.getFullName() +
                "', email = '" + user.getEmail() +
                "', phone = '" + user.getPhone() +
                "' WHERE user_id = " + user.getUserId();

        try (Connection conn = DBconnector.getConnection()) {
            if (conn != null) {
                conn.createStatement().executeUpdate(query);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateAvatar(int userId, String avatarPath) {
        if (avatarPath == null || avatarPath.isEmpty()) {
            return false;
        }

        // تحويل الـ backslashes لـ forward slashes (compatible مع كل الأنظمة)
        String normalizedPath = avatarPath.replace("\\", "/");

        // Escape single quotes في الـ path
        String escapedPath = normalizedPath.replace("'", "''");

        String query = "UPDATE users SET avatar = '" + escapedPath + "' WHERE user_id = " + userId;

        System.out.println("💾 Saving avatar: " + escapedPath);

        try (Connection conn = DBconnector.getConnection()) {
            if (conn != null) {
                conn.createStatement().executeUpdate(query);
                System.out.println("✅ Avatar path saved successfully");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error updating avatar: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

}
