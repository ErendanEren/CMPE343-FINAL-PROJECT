package Dao;

import Database.DatabaseConnection;
import Models.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {

    // Boştaki siparişleri getirir
    public List<Order> getAvailableOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM OrderInfo WHERE carrier_id IS NULL AND (status = 'PLACED' OR status = 'CREATED')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return orders;
    }

    // Belirli bir kuryeye ait ve belirli bir durumdaki siparişleri getirir
    public List<Order> getOrdersByCarrier(int carrierId, String status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM OrderInfo WHERE carrier_id = ? AND status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carrierId);
            ps.setString(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return orders;
    }

    // Sipariş durumunu ve kurye atamasını günceller
    public void updateOrderStatus(int orderId, String status, int carrierId) {
        String sql = "UPDATE OrderInfo SET status = ?, carrier_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, carrierId);
            ps.setInt(3, orderId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setCustomerId(rs.getInt("customer_id"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setCustomerAddressSnapshot(rs.getString("customer_address"));
        order.setStatus(rs.getString("status"));
        return order;
    }
}
