package Dao;

import Database.DatabaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DBReportDAO implements ReportDAO {

    @Override
    public Map<String, Double> getStockDistribution() {
        Map<String, Double> data = new HashMap<>();
        String sql = "SELECT type, SUM(stock_kg) as total_stock FROM group09_greengrocer.product_info GROUP BY type";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String type = rs.getString("type");
                double stock = rs.getDouble("total_stock");
                data.put(type, stock);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public Map<String, Double> getDailyIncome() {
        // TreeMap to keep dates sorted
        Map<String, Double> data = new TreeMap<>();
        // Get last 7 days income
        String sql = "SELECT DATE(order_time) as order_date, SUM(total_amount) as daily_total " +
                "FROM group09_greengrocer.order_info " +
                "WHERE status != 'CANCELLED' " +
                "GROUP BY DATE(order_time) " +
                "ORDER BY order_date DESC LIMIT 7";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String date = rs.getString("order_date");
                double total = rs.getDouble("daily_total");
                data.put(date, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}
