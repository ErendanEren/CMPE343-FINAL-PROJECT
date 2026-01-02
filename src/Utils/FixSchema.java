package Utils;

import Database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FixSchema {
    public static void main(String[] args) {
        System.out.println("Starting Schema Fix...");
        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1. Fix status column length
            try (PreparedStatement ps = conn.prepareStatement(
                    "ALTER TABLE group09_greengrocer.order_info MODIFY COLUMN status VARCHAR(255)")) {
                ps.executeUpdate();
                System.out.println("FIXED: order_info.status column length increased to 255.");
            } catch (SQLException e) {
                System.err.println("Note: status column fix might have failed or not needed: " + e.getMessage());
            }

            // 2. Ensure ID is Auto Increment (Just in case)
            try (PreparedStatement ps = conn.prepareStatement(
                    "ALTER TABLE group09_greengrocer.order_info MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT")) {
                ps.executeUpdate();
                System.out.println("FIXED: order_info.id set to AUTO_INCREMENT.");
            } catch (SQLException e) {
                System.err.println("Note: ID auto-increment fix might have failed or not needed: " + e.getMessage());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
