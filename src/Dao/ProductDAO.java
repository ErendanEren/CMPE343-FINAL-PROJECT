package Dao;

import Models.Product;
import java.util.List;

public interface ProductDAO
{
    List<Product> getAllProducts();
    void addProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(int productId);
    boolean decreaseStock(int productId, double amount);
}