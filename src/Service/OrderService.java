package Service;

import Dao.DBOrderDAO;
import Dao.OrderDao;
import Models.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private OrderDao orderDao = new DBOrderDAO();

    public Order placeOrder(User user, ShoppingCart cart, String deliveryInfo) {
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setCustomerId(user.getId());

        order.setOrderTime(LocalDateTime.now());
        // Parse delivery info if needed, or just store it.
        // For simple requirements, we might store it in requestedDeliveryTime if we parse it,
        // but for now, let's append it to address snapshot or status for visibility.
        // Actually, let's try to parse the DatePicker output or just keep it as a comment in address.
        order.setStatus("PENDING");
        order.setTotalAmount(cart.calculateTotal());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cart.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setProductId(ci.getProduct().getId());
            oi.setAmountKg(ci.getQuantity());
            oi.setUnitPrice(ci.getProduct().getEffectivePrice());
            oi.setLineTotal(ci.getItemTotal());
            orderItems.add(oi);
        }
        order.setItems(orderItems);

        // Append delivery info to address snapshot for simple persistence
        String fullAddressInfo = user.getAddress() + " | Delivery: " + deliveryInfo;
        order.setCustomerAddressSnapshot(fullAddressInfo);

        orderDao.saveOrder(order);

        // Generate Invoice File
        saveInvoiceToFile(order, user, deliveryInfo);

        // Clear cart
        cart.clearCart();

        return order;
    }

    private void saveInvoiceToFile(Order order, User user, String deliveryInfo) {
        String invoiceContent = generateInvoice(order, user, deliveryInfo);
        String filename = "Invoice_" + order.getId() + ".txt";

        try (java.io.PrintWriter out = new java.io.PrintWriter(filename)) {
            out.println(invoiceContent);
            System.out.println("Invoice saved to: " + filename);
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Overloaded to support old usage if any, or just helper
    public String generateInvoice(Order order, User user, String deliveryInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("============= INVOICE =============\n");
        sb.append("Order ID: ").append(order.getId()).append("\n");
        sb.append("Date: ").append(order.getOrderTime()).append("\n");
        sb.append("Customer: ").append(user.getFullName()).append("\n");
        sb.append("Address: ").append(user.getAddress()).append("\n");
        sb.append("Phone: ").append(user.getPhone()).append("\n");
        sb.append("Delivery Info: ").append(deliveryInfo).append("\n");
        sb.append("-----------------------------------\n");
        for (OrderItem item : order.getItems()) {
            sb.append(String.format("Product ID %d : %.2f kg x %.2f TL = %.2f TL\n",
                    item.getProductId(), item.getAmountKg(), item.getUnitPrice(), item.getLineTotal()));
        }
        sb.append("-----------------------------------\n");
        sb.append(String.format("TOTAL AMOUNT: %.2f TL\n", order.getTotalAmount()));
        sb.append("===================================\n");
        return sb.toString();
    }

    public String generateInvoice(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("INVOICE\n");
        sb.append("Order ID: ").append(order.getId()).append("\n");
        sb.append("Date: ").append(order.getOrderTime()).append("\n");
        sb.append("Customer ID: ").append(order.getCustomerId()).append("\n");
        sb.append("Details: ").append(order.getCustomerAddressSnapshot()).append("\n");
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
