package Dao;

import Models.Order;
import Models.CarrierRating;
import Database.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Kurye operasyonlarını ve veritabanı altyapı işlemlerini yöneten DAO sınıfı.
 * Member 4 sorumluluklarını (Carrier Module & Infrastructure) kapsar. [cite: 49, 84]
 */
public class CarrierDAO {

    /**
     * Boştaki (kurye atanmamış) siparişleri listeler. [cite: 56, 121]
     * @return Mevcut siparişlerin listesi.
     */
    public List<Order> getAvailableOrders() {
        List<Order> orders = new ArrayList<>();
        // carrier_id IS NULL kontrolü ile sadece boştaki siparişler çekilir [cite: 122]
        String sql = "SELECT * FROM group09_greengrocer.order_info WHERE carrier_id IS NULL AND (status = 'PLACED' OR status = 'CREATED')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * Belirli bir kuryeye atanmış siparişleri durumuna göre getirir.
     * @param carrierId Kurye ID'si.
     * @param status Sipariş durumu (ASSIGNED veya DELIVERED).
     * @return Sipariş listesi.
     */
    public List<Order> getOrdersByCarrier(int carrierId, String status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM group09_greengrocer.order_info WHERE carrier_id = ? AND status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, carrierId);
            ps.setString(2, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * Bir siparişi kuryeye zimmetler.
     * Hata Yönetimi: Aynı siparişin iki kurye tarafından seçilmesini SQL seviyesinde engeller. [cite: 57, 158]
     * @return Güncelleme başarılıysa true.
     */
    public boolean assignOrderToCarrier(int orderId, int carrierId) {
        // 'AND carrier_id IS NULL' şartı sunumda puan kaybetmeni engelleyen kritik kontroldür
        String sql = "UPDATE group09_greengrocer.order_info SET carrier_id = ?, status = 'ASSIGNED' WHERE id = ? AND carrier_id IS NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, carrierId);
            ps.setInt(2, orderId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Siparişi teslim edildi olarak işaretler ve teslimat zamanını kaydeder. [cite: 58, 123]
     */
    public boolean completeOrder(int orderId) {
        String sql = "UPDATE group09_greengrocer.order_info SET status = 'DELIVERED', delivered_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kuryenin aldığı puan ve yorumları getirir. [cite: 60, 130]
     */
    public List<CarrierRating> getRatingsForCarrier(int carrierId) {
        List<CarrierRating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM group09_greengrocer.carrier_rating WHERE carrier_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, carrierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CarrierRating cr = new CarrierRating();
                    cr.setId(rs.getInt("id"));
                    cr.setOrderId(rs.getInt("order_id"));
                    cr.setRating(rs.getInt("rating"));
                    cr.setComment(rs.getString("comment"));
                    ratings.add(cr);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    /**
     * ALTYAPI GÖREVİ: Resim dosyalarını BLOB olarak kaydetmek için yardımcı metot. [cite: 44, 135]
     */
    public void saveImage(int productId, byte[] imageBytes) throws SQLException {
        String sql = "UPDATE group09_greengrocer.product_info SET image_blob = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBytes(1, imageBytes);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    /**
     * Yardımcı Metot: ResultSet verisini Order nesnesine eşler.
     */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setCustomerId(rs.getInt("customer_id"));

        Timestamp orderTime = rs.getTimestamp("order_time");
        if (orderTime != null) order.setOrderTime(orderTime.toLocalDateTime());

        order.setStatus(rs.getString("status"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        // DBOrderDAO'da olduğu gibi `customer_address_snapshot` kullanıyoruz.
        order.setCustomerAddressSnapshot(rs.getString("customer_address_snapshot"));

        Timestamp deliveryTime = rs.getTimestamp("requested_delivery_time");
        if (deliveryTime != null) order.setRequestedDeliveryTime(deliveryTime.toLocalDateTime());

        Timestamp deliveredAt = rs.getTimestamp("delivered_at");
        if (deliveredAt != null) order.setDeliveredAt(deliveredAt.toLocalDateTime());

        return order;
    }
}