package Dao;

import Models.Product;
import java.util.ArrayList;
import java.util.List;

public class MockProductDAO implements ProductDAO {
    private static List<Product> dummyDB = new ArrayList<>();

    static {
        dummyDB.add(new Product("Elma", "FRUIT", 25.50, 100, 20, "elma.png"));
        dummyDB.add(new Product("Domates", "VEGETABLE", 30.0, 50, 10, "domates.png"));
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(dummyDB);
    }

    @Override
    public void addProduct(Product product) {
        product.setId(dummyDB.size() + 1);
        dummyDB.add(product);
        System.out.println("Mock DB: Ürün eklendi -> " + product.getName());
    }

    @Override
    public void updateProduct(Product product) {
        for (Product p : dummyDB) {
            if (p.getName().equals(product.getName())) {
                p.setPricePerKg(product.getPricePerKg());
                p.setStockKg(product.getStockKg());
            }
        }
        System.out.println("Mock DB: Ürün güncellendi -> " + product.getName());
    }

    @Override
    public void deleteProduct(int productId) {
        dummyDB.removeIf(p -> p.getId() == productId);
        System.out.println("Mock DB: Ürün silindi ID -> " + productId);
    }
}