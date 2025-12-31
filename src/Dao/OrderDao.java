package Dao;

import Models.Order;
import java.util.List;

public interface OrderDao {
    void saveOrder(Order order);
    List<Order> getOrdersByCustomer(int customerId);
}
