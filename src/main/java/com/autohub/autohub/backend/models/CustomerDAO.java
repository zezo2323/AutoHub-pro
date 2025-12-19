package com.autohub.autohub.backend.models;

import com.autohub.autohub.backend.database.DBconnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    /**
     * إضافة عميل جديد
     *
     * @param customer كائن العميل
     * @return customer_id أو -1 في حالة الفشل
     */
    public static int addCustomer(Customer customer) {
        String query = "INSERT INTO customers (name, email, phone, license_number) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getLicenseNumber());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int customerId = rs.getInt(1);
                    System.out.println("✅ Customer added successfully with ID: " + customerId);
                    return customerId;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error adding customer: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * جلب جميع العملاء
     *
     * @return قائمة بكل العملاء
     */
    public static List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers ORDER BY customer_id DESC";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }

            System.out.println("✅ Retrieved " + customers.size() + " customers");

        } catch (SQLException e) {
            System.err.println("❌ Error getting all customers: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * جلب عميل بواسطة ID
     *
     * @param customerId رقم العميل
     * @return كائن Customer أو null
     */
    public static Customer getCustomerById(int customerId) {
        String query = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractCustomerFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting customer by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * جلب عميل بواسطة الإيميل
     *
     * @param email إيميل العميل
     * @return كائن Customer أو null
     */
    public static Customer getCustomerByEmail(String email) {
        String query = "SELECT * FROM customers WHERE email = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractCustomerFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting customer by email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * تحديث بيانات العميل
     *
     * @param customer كائن العميل المحدث
     * @return true في حالة النجاح
     */
    public static boolean updateCustomer(Customer customer) {
        String query = "UPDATE customers SET name = ?, email = ?, phone = ?, license_number = ? WHERE customer_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getLicenseNumber());
            stmt.setInt(5, customer.getCustomerId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Customer updated successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating customer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * حذف عميل
     *
     * @param customerId رقم العميل
     * @return true في حالة النجاح
     */
    public static boolean deleteCustomer(int customerId) {
        String query = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, customerId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Customer deleted successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error deleting customer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * البحث عن عملاء
     *
     * @param searchTerm نص البحث
     * @return قائمة بالعملاء المطابقين
     */
    public static List<Customer> searchCustomers(String searchTerm) {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers WHERE name LIKE ? OR email LIKE ? OR phone LIKE ? ORDER BY name";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }

            System.out.println("✅ Found " + customers.size() + " customers matching: " + searchTerm);

        } catch (SQLException e) {
            System.err.println("❌ Error searching customers: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * التحقق من وجود عميل بنفس الإيميل
     *
     * @param email الإيميل
     * @return true إذا كان موجود
     */
    public static boolean isEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM customers WHERE email = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * الحصول على عدد العملاء الكلي
     *
     * @return عدد العملاء
     */
    public static int getTotalCustomersCount() {
        String query = "SELECT COUNT(*) FROM customers";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting total customers count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * الحصول على عملاء لهم إيجارات نشطة
     *
     * @return قائمة بالعملاء
     */
    public static List<Customer> getActiveCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT DISTINCT c.* FROM customers c " +
                "INNER JOIN rentals r ON c.customer_id = r.customer_id " +
                "WHERE r.status = 'active' " +
                "ORDER BY c.name";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }

            System.out.println("✅ Retrieved " + customers.size() + " active customers");

        } catch (SQLException e) {
            System.err.println("❌ Error getting active customers: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * الحصول على إجمالي المبلغ الذي دفعه عميل معين
     *
     * @param customerId رقم العميل
     * @return المبلغ الإجمالي
     */
    public static double getTotalSpentByCustomer(int customerId) {
        String query = "SELECT COALESCE(SUM(total_amount), 0) FROM rentals WHERE customer_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting total spent by customer: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * استخراج بيانات العميل من ResultSet
     *
     * @param rs ResultSet
     * @return كائن Customer
     */
    private static Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setName(rs.getString("name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setLicenseNumber(rs.getString("license_number"));
        return customer;
    }

    /**
     * اختبار الاتصال وطباعة جميع العملاء
     */
    static void main(String[] args) {
        System.out.println("========== Testing CustomerDAO ==========\n");

        // Test 1: Get all customers
        System.out.println("Test 1: Getting all customers...");
        List<Customer> customers = getAllCustomers();
        for (Customer c : customers) {
            System.out.println("- " + c.getName() + " | " + c.getEmail() + " | " + c.getPhone());
        }

        // Test 2: Get customer by ID
        System.out.println("\nTest 2: Getting customer by ID = 1...");
        Customer customer = getCustomerById(1);
        if (customer != null) {
            System.out.println("Found: " + customer.getName());
        }

        // Test 3: Search customers
        System.out.println("\nTest 3: Searching for 'john'...");
        List<Customer> results = searchCustomers("john");
        System.out.println("Found " + results.size() + " results");

        // Test 4: Total customers
        System.out.println("\nTest 4: Total customers count...");
        int total = getTotalCustomersCount();
        System.out.println("Total: " + total);

        System.out.println("\n========== Tests Complete ==========");
    }
}
