package Dao;

import Models.Order;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MockOrderDao implements OrderDao {
    private static MockOrderDao instance;
    private List<Order> orders = new ArrayList<>();
    private int lastId = 0;

    private MockOrderDao() {}

    public static MockOrderDao getInstance() {
        if (instance == null) {
            instance = new MockOrderDao();
        }
        return instance;
    }

    @Override
    public void saveOrder(Order order) {
        order.setId(++lastId);
        orders.add(order);
    }

    @Override
    public List<Order> getOrdersByCustomer(int customerId) {
        return orders.stream()
                .filter(o -> o.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }
}
