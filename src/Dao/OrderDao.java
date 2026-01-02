package Dao;

import Models.Order;
import java.util.List;

public interface OrderDao {
    void saveOrder(Order order);
    List<Order> getAvailableOrders();
    List<Order> getOrdersByCarrier(int carrierId, String status);
    void updateOrderStatus(int orderId, String status, int carrierId);
    List<Order> getOrdersByCustomer(int customerId);
    int getCompletedOrderCount(int customerId);
}
