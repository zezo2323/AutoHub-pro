package com.autohub.autohub.backend.models;

import com.autohub.autohub.backend.database.DBconnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    /**
     * إضافة تعليق جديد
     */
    public static boolean addComment(Comment comment) {
        String sql = "INSERT INTO comments (user_id, car_id, rating, comment_text, comment_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, comment.getUserId());
            stmt.setInt(2, comment.getCarId());
            stmt.setInt(3, comment.getRating());
            stmt.setString(4, comment.getCommentText());
            stmt.setTimestamp(5, Timestamp.valueOf(comment.getCommentDate()));

            int rowsInserted = stmt.executeUpdate();
            System.out.println("✅ Comment added successfully!");
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error adding comment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * جلب جميع التعليقات مع بيانات المستخدم والسيارة
     */
    public static List<Comment> getAllComments() {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name, CONCAT(car.brand, ' ', car.model) AS car_name " +
                "FROM comments c " +
                "INNER JOIN users u ON c.user_id = u.user_id " +
                "INNER JOIN cars car ON c.car_id = car.car_id " +
                "ORDER BY c.comment_date DESC";

        try (Connection conn = DBconnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Comment comment = new Comment();
                comment.setCommentId(rs.getInt("comment_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setCarId(rs.getInt("car_id"));
                comment.setRating(rs.getInt("rating"));
                comment.setCommentText(rs.getString("comment_text"));
                comment.setCommentDate(rs.getTimestamp("comment_date").toLocalDateTime());
                comment.setUserName(rs.getString("full_name"));
                comment.setCarName(rs.getString("car_name"));

                comments.add(comment);
            }

            System.out.println("✅ Loaded " + comments.size() + " comments");

        } catch (SQLException e) {
            System.err.println("❌ Error loading comments: " + e.getMessage());
            e.printStackTrace();
        }

        return comments;
    }

    /**
     * جلب التعليقات الخاصة بسيارة معينة
     */
    public static List<Comment> getCommentsByCarId(int carId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name, CONCAT(car.brand, ' ', car.model) AS car_name " +
                "FROM comments c " +
                "INNER JOIN users u ON c.user_id = u.user_id " +
                "INNER JOIN cars car ON c.car_id = car.car_id " +
                "WHERE c.car_id = ? " +
                "ORDER BY c.comment_date DESC";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, carId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Comment comment = new Comment();
                comment.setCommentId(rs.getInt("comment_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setCarId(rs.getInt("car_id"));
                comment.setRating(rs.getInt("rating"));
                comment.setCommentText(rs.getString("comment_text"));
                comment.setCommentDate(rs.getTimestamp("comment_date").toLocalDateTime());
                comment.setUserName(rs.getString("full_name"));
                comment.setCarName(rs.getString("car_name"));

                comments.add(comment);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error loading comments by car: " + e.getMessage());
            e.printStackTrace();
        }

        return comments;
    }

    /**
     * حذف تعليق
     */
    public static boolean deleteComment(int commentId) {
        String sql = "DELETE FROM comments WHERE comment_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, commentId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting comment: " + e.getMessage());
            return false;
        }
    }

    /**
     * تحديث تعليق
     */
    public static boolean updateComment(Comment comment) {
        String sql = "UPDATE comments SET rating = ?, comment_text = ? WHERE comment_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, comment.getRating());
            stmt.setString(2, comment.getCommentText());
            stmt.setInt(3, comment.getCommentId());

            int rowsUpdated = stmt.executeUpdate();
            System.out.println("✅ Comment updated successfully!");
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating comment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
