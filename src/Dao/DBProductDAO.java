package Dao;

import Database.DatabaseConnection;
import Models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBProductDAO implements ProductDAO {

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM group09_greengrocer.product_info";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("DBProductDAO Error: " + e.getMessage());
        }
        System.out.println("DBProductDAO: Loaded " + products.size() + " products.");
        return products;
    }

    @Override
    public void addProduct(Product product) {
        String sql = "INSERT INTO group09_greengrocer.product_info (name, type, price_per_kg, stock_kg, threshold_kg, image_blob, image_mime, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getType());
            ps.setDouble(3, product.getPricePerKg());
            ps.setDouble(4, product.getStockKg());
            ps.setDouble(5, product.getThresholdKg());
            // Store imageContent if available, else null
            ps.setBytes(6, product.getImageContent());
            ps.setString(7, product.getMimeType());
            ps.setBoolean(8, product.isActive());

            ps.executeUpdate();
            System.out.println("DB: Product added -> " + product.getName());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateProduct(Product product) {
        // Assuming update by ID is better, but interface might be loose.
        // If ID is available use it, otherwise name.
        // Based on MockProductDAO, it updates by name, but DB should use ID.
        // I will use ID if > 0, else try Name for backward compatibility/safety if ID not set?
        // Actually Product has ID. Let's rely on ID for safe updates if possible.
        // However, the object passed from UI might just be updated fields.

        String sql = "UPDATE group09_greengrocer.product_info SET name=?, type=?, price_per_kg=?, stock_kg=?, threshold_kg=?, image_blob=?, image_mime=?, is_active=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getType());
            ps.setDouble(3, product.getPricePerKg());
            ps.setDouble(4, product.getStockKg());
            ps.setDouble(5, product.getThresholdKg());
            // Store imageContent if available, else null
            ps.setBytes(6, product.getImageContent());
            ps.setString(7, product.getMimeType());
            ps.setBoolean(8, product.isActive());
            ps.setInt(9, product.getId());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                // Fallback or error? For now just log.
                System.out.println("DB Warning: Product update affected 0 rows. ID might be wrong: " + product.getId());
            } else {
                System.out.println("DB: Product updated -> " + product.getName());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProduct(int productId) {
        String sql = "DELETE FROM group09_greengrocer.product_info WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.executeUpdate();
            System.out.println("DB: Product deleted ID -> " + productId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean decreaseStock(int productId, double amount) {
        String sql = "UPDATE group09_greengrocer.product_info SET stock_kg = stock_kg - ? WHERE id = ? AND stock_kg >= ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setInt(2, productId);
            ps.setDouble(3, amount); // Check if enough stock exists atomically

            int affected = ps.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setType(rs.getString("type"));
        p.setPricePerKg(rs.getDouble("price_per_kg"));
        p.setStockKg(rs.getDouble("stock_kg"));
        p.setThresholdKg(rs.getDouble("threshold_kg"));
        p.setImageContent(rs.getBytes("image_blob"));
        try {
            p.setMimeType(rs.getString("image_mime"));
        } catch (SQLException e) {
            // Column might not exist in older DB versions, ignore
            p.setMimeType(null);
        }
        // p.setImagePath(rs.getString("image_blob")); // usually this fails if blob, redundant now
        p.setActive(rs.getBoolean("is_active"));
        return p;
    }
}
