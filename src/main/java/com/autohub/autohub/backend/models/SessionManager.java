package com.autohub.autohub.backend.models;

public class SessionManager {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
        System.out.println("✅ Session created for: " + user.getFullName() + " (ID: " + user.getUserId() + ")");
    }

    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }

    public static String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    public static void logout() {
        if (currentUser != null) {
            System.out.println("✅ User logged out: " + currentUser.getFullName());
        }
        currentUser = null;
    }
}
