package Dao;

import Database.DatabaseConnection;
import Models.OwnerSettings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBOwnerSettingsDAO implements OwnerSettingsDAO {

    @Override
    public OwnerSettings getSettings() {
        OwnerSettings settings = null;
        String sql = "SELECT * FROM group09_greengrocer.owner_settings LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                settings = new OwnerSettings();
                settings.setId(rs.getInt("id"));
                settings.setMinCartValue(rs.getDouble("min_cart_value"));
                settings.setLoyaltyMinCompleted(rs.getInt("loyalty_min_completed"));
                settings.setLoyaltyDiscountPercent(rs.getDouble("loyalty_discount_percent"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settings;
    }
}
