package Dao;

import Database.DatabaseConnection;
import Models.Coupon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBCouponDAO implements CouponDAO {

    @Override
    public List<Coupon> getAllCoupons() {
        List<Coupon> coupons = new ArrayList<>();
        String sql = "SELECT * FROM coupons"; // Assuming table name is coupons

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                coupons.add(mapResultSetToCoupon(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching coupons: " + e.getMessage());
            e.printStackTrace();
        }
        return coupons;
    }

    @Override
    public Coupon getCouponByCode(String code) {
        String sql = "SELECT * FROM coupons WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCoupon(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addCoupon(Coupon coupon) {
        String sql = "INSERT INTO coupons (code, discount_percent, min_total, valid_from, valid_to, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, coupon.getCode());
            ps.setDouble(2, coupon.getDiscountPercent());
            ps.setDouble(3, coupon.getMinTotal());
            // Handling null dates
            ps.setTimestamp(4, coupon.getValidFrom() != null ? Timestamp.valueOf(coupon.getValidFrom()) : null);
            ps.setTimestamp(5, coupon.getValidTo() != null ? Timestamp.valueOf(coupon.getValidTo()) : null);
            ps.setBoolean(6, coupon.isActive());

            ps.executeUpdate();
            System.out.println("DB: Coupon added -> " + coupon.getCode());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCoupon(int id) {
        String sql = "DELETE FROM coupons WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("DB: Coupon deleted ID -> " + id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Coupon mapResultSetToCoupon(ResultSet rs) throws SQLException {
        Coupon c = new Coupon();
        c.setId(rs.getInt("id"));
        c.setCode(rs.getString("code"));
        c.setDiscountPercent(rs.getDouble("discount_percent"));
        c.setMinTotal(rs.getDouble("min_total"));

        Timestamp validFrom = rs.getTimestamp("valid_from");
        if (validFrom != null) c.setValidFrom(validFrom.toLocalDateTime());

        Timestamp validTo = rs.getTimestamp("valid_to");
        if (validTo != null) c.setValidTo(validTo.toLocalDateTime());

        c.setActive(rs.getBoolean("is_active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) c.setCreatedAt(createdAt.toLocalDateTime());

        return c;
    }
}
