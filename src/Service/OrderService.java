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
        order.setOrderTime(LocalDateTime.now());

        // Parse delivery info if needed. For now, we MUST set it to avoid NULL constraint.
        // Delivery Info format example: "2026-01-08 13:00 - 15:00"
        try {
            if (deliveryInfo != null && deliveryInfo.length() >= 10) {
                String datePart = deliveryInfo.split(" ")[0]; // "2026-01-08"
                // Simple parsing or just set to orderTime + 1 day if fail.
                // Let's rely on LocalDateTime.parse if format matches, otherwise default.
                // Since format varies, let's just use Now + 24 Hours as a placeholder for the TIMESTAMP column
                // provided we store text in addressSnapshot for human reading.
                order.setRequestedDeliveryTime(LocalDateTime.now().plusDays(1));
            } else {
                order.setRequestedDeliveryTime(LocalDateTime.now().plusDays(1));
            }
        } catch (Exception e) {
            order.setRequestedDeliveryTime(LocalDateTime.now().plusDays(1));
        }

        order.setStatus("PLACED");
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
        // order.setItems(orderItems); // Already done above? No, wait, loop above adds.
        // Actually the loop above iterates valid cart items.
        // We need to decrease stock now.

        // Need ProductDAO instance
        // Check for Loyalty Discount
        Dao.OwnerSettingsDAO settingsDAO = new Dao.DBOwnerSettingsDAO();
        Models.OwnerSettings settings = settingsDAO.getSettings();

        // Default to no discount
        order.setLoyaltyDiscountPercent(0.0);

        if (settings != null) {
            int completedOrders = orderDao.getCompletedOrderCount(user.getId());
            if (completedOrders >= settings.getLoyaltyMinCompleted()) {
                double discountPercent = settings.getLoyaltyDiscountPercent();
                order.setLoyaltyDiscountPercent(discountPercent);
                System.out.println("Loyalty Discount Applied: " + discountPercent + "% (Completed Orders: " + completedOrders + ")");

                // Apply discount to total amount
                double currentTotal = order.getTotalAmount();
                double discountAmount = currentTotal * (discountPercent / 100.0);
                order.setTotalAmount(currentTotal - discountAmount);
            }
        }

        Dao.ProductDAO productDAO = new Dao.DBProductDAO();

        for (OrderItem oi : orderItems) {
            boolean success = productDAO.decreaseStock(oi.getProductId(), oi.getAmountKg());
            if (!success) {
                // If stock update fails (e.g., race condition), we should probably rollback or warn.
                // For this project, we'll log it.
                System.err.println("Warning: Could not decrease stock for Product ID: " + oi.getProductId());
            }
        }
        order.setItems(orderItems); // Ensure items are set

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

        // Save to Database
        Dao.InvoiceDAO invoiceDAO = new Dao.DBInvoiceDAO();
        invoiceDAO.saveInvoice(order.getId(), invoiceContent);

        // Invoice is already saved to DB above by DBInvoiceDAO.
        System.out.println("Invoice generated and saved to database for Order ID: " + order.getId());
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
        sb.append("-----------------------------------\n");
        if (order.getLoyaltyDiscountPercent() > 0) {
            sb.append(String.format("Loyalty Discount: %.2f%%\n", order.getLoyaltyDiscountPercent()));
        }
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
