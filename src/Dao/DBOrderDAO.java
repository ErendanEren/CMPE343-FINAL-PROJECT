package Dao;

import Database.DatabaseConnection;
import Models.Order;
import Models.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBOrderDAO implements OrderDao {

    @Override
    public void saveOrder(Order order) {
        String insertOrderSQL = "INSERT INTO group09_greengrocer.order_info (customer_id, carrier_id, order_time, requested_delivery_time, delivered_at, status, total_amount, customer_address_snapshot) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertItemSQL = "INSERT INTO group09_greengrocer.order_item (order_id, product_id, amount_kg, unit_price, line_total) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psItem = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            // Start transaction
            conn.setAutoCommit(false);

            psOrder = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, order.getCustomerId());
            // Handling nullable carrier_id (might be 0 or null logic, assuming 0 means none for now or check SetNull)
            if (order.getCarrierId() > 0) {
                psOrder.setInt(2, order.getCarrierId());
            } else {
                psOrder.setNull(2, java.sql.Types.INTEGER);
            }

            psOrder.setTimestamp(3, Timestamp.valueOf(order.getOrderTime()));
            psOrder.setTimestamp(4, order.getRequestedDeliveryTime() != null ? Timestamp.valueOf(order.getRequestedDeliveryTime()) : null);
            psOrder.setTimestamp(5, order.getDeliveredAt() != null ? Timestamp.valueOf(order.getDeliveredAt()) : null);
            psOrder.setString(6, order.getStatus());
            psOrder.setDouble(7, order.getTotalAmount());
            psOrder.setString(8, order.getCustomerAddressSnapshot());

            psOrder.executeUpdate();

            rs = psOrder.getGeneratedKeys();
            if (rs.next()) {
                int orderId = rs.getInt(1);
                order.setId(orderId);

                psItem = conn.prepareStatement(insertItemSQL);
                for (OrderItem item : order.getItems()) {
                    psItem.setInt(1, orderId);
                    psItem.setInt(2, item.getProductId());
                    psItem.setDouble(3, item.getAmountKg());
                    psItem.setDouble(4, item.getUnitPrice());
                    psItem.setDouble(5, item.getLineTotal());
                    psItem.addBatch();
                }
                psItem.executeBatch();
            }

            conn.commit();
            System.out.println("DB: Order saved -> ID: " + order.getId());

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true); // Reset auto-commit
                if (rs != null) rs.close();
                if (psOrder != null) psOrder.close();
                if (psItem != null) psItem.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Order> getOrdersByCustomer(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM group09_greengrocer.order_info WHERE customer_id = ? ORDER BY order_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setCustomerId(rs.getInt("customer_id"));
                    order.setCarrierId(rs.getInt("carrier_id"));
                    order.setOrderTime(rs.getTimestamp("order_time").toLocalDateTime());

                    Timestamp reqDel = rs.getTimestamp("requested_delivery_time");
                    if (reqDel != null) order.setRequestedDeliveryTime(reqDel.toLocalDateTime());

                    Timestamp delAt = rs.getTimestamp("delivered_at");
                    if (delAt != null) order.setDeliveredAt(delAt.toLocalDateTime());

                    order.setStatus(rs.getString("status"));
                    order.setTotalAmount(rs.getDouble("total_amount"));
                    order.setCustomerAddressSnapshot(rs.getString("customer_address_snapshot"));

                    // We could fetch items here if needed, but for history list, usually header is enough.
                    // If detailed view needed, fetch items lazily or differently.

                    orders.add(order);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}
