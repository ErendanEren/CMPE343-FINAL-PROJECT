package Service;

import Dao.DBOrderDAO;
import Dao.OrderDao;
import Models.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private OrderDao orderDao = new DBOrderDAO();

    public Order placeOrder(User user, ShoppingCart cart) {
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setCustomerId(user.getId());

        order.setOrderTime(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setTotalAmount(cart.calculateTotal());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cart.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setProductId(ci.getProduct().getId());
            oi.setAmountKg(ci.getQuantity());
            oi.setUnitPrice(ci.getProduct().getEffectivePrice());
            oi.setLineTotal(ci.getItemTotal());
            // oi.setOrderId(order.getId()); // ID generated after save in DB usually
            orderItems.add(oi);
        }
        order.setItems(orderItems);
        order.setCustomerAddressSnapshot(user.getAddress()); // Snapshot address

        orderDao.saveOrder(order);

        // Clear cart
        cart.clearCart();

        return order;
    }

    public String generateInvoice(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("INVOICE\n");
        sb.append("Order ID: ").append(order.getId()).append("\n");
        sb.append("Date: ").append(order.getOrderTime()).append("\n");
        sb.append("Customer ID: ").append(order.getCustomerId()).append("\n");
        sb.append("----------------------------\n");
        for (OrderItem item : order.getItems()) {
            sb.append(String.format("Product ID %d : %.2f kg x %.2f TL = %.2f TL\n",
                    item.getProductId(), item.getAmountKg(), item.getUnitPrice(), item.getLineTotal()));
        }
        sb.append("----------------------------\n");
        sb.append(String.format("TOTAL: %.2f TL\n", order.getTotalAmount()));
        return sb.toString();
    }
}
