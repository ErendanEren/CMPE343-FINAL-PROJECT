package Service;

import Dao.DBOrderDAO;
import Dao.OrderDao;
import Models.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for managing the order lifecycle, including placement,
 * loyalty discount application, stock management, and invoice persistence.
 *
 * @author eren çakır
 * @author mert serinken
 */
public class OrderService {

    private OrderDao orderDao = new DBOrderDAO();

    /**
     * Processes the placement of a new order. This includes validating the cart,
     * calculating delivery times, applying loyalty discounts if applicable,
     * updating product stock levels, and persisting the order and invoice.
     *
     * @param user         The user placing the order.
     * @param cart         The shopping cart containing the items to be purchased.
     * @param deliveryInfo A string containing delivery preferences or time slots.
     * @return The completed {@link Order} object.
     * @throws IllegalStateException If the provided shopping cart is empty.
     */
    public Order placeOrder(User user, ShoppingCart cart, String deliveryInfo) {
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setCustomerId(user.getId());

        order.setOrderTime(LocalDateTime.now());
        order.setOrderTime(LocalDateTime.now());

        try {
            if (deliveryInfo != null && deliveryInfo.length() >= 10) {
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

        Dao.OwnerSettingsDAO settingsDAO = new Dao.DBOwnerSettingsDAO();
        Models.OwnerSettings settings = settingsDAO.getSettings();

        order.setLoyaltyDiscountPercent(0.0);

        if (settings != null) {
            int completedOrders = orderDao.getCompletedOrderCount(user.getId());
            if (completedOrders >= settings.getLoyaltyMinCompleted()) {
                double discountPercent = settings.getLoyaltyDiscountPercent();
                order.setLoyaltyDiscountPercent(discountPercent);
                System.out.println("Loyalty Discount Applied: " + discountPercent + "% (Completed Orders: " + completedOrders + ")");

                double currentTotal = order.getTotalAmount();
                double discountAmount = currentTotal * (discountPercent / 100.0);
                order.setTotalAmount(currentTotal - discountAmount);
            }
        }

        Dao.ProductDAO productDAO = new Dao.DBProductDAO();

        for (OrderItem oi : orderItems) {
            boolean success = productDAO.decreaseStock(oi.getProductId(), oi.getAmountKg());
            if (!success) {
                System.err.println("Warning: Could not decrease stock for Product ID: " + oi.getProductId());
            }
        }
        order.setItems(orderItems);

        String fullAddressInfo = user.getAddress() + " | Delivery: " + deliveryInfo;
        order.setCustomerAddressSnapshot(fullAddressInfo);

        orderDao.saveOrder(order);

        saveInvoiceToFile(order, user, deliveryInfo);

        cart.clearCart();

        return order;
    }

    /**
     * Generates the invoice content and persists it to the database for a specific order.
     *
     * @param order        The finalized order object.
     * @param user         The user who placed the order.
     * @param deliveryInfo Information regarding the delivery.
     */
    private void saveInvoiceToFile(Order order, User user, String deliveryInfo) {
        String invoiceContent = generateInvoice(order, user, deliveryInfo);

        Dao.InvoiceDAO invoiceDAO = new Dao.DBInvoiceDAO();
        invoiceDAO.saveInvoice(order.getId(), invoiceContent);

        System.out.println("Invoice generated and saved to database for Order ID: " + order.getId());
    }

    /**
     * Generates a detailed string representation of the invoice, including customer details,
     * product breakdowns, and applied loyalty discounts.
     *
     * @param order        The order for which the invoice is generated.
     * @param user         The user associated with the order.
     * @param deliveryInfo The delivery notes provided during checkout.
     * @return A formatted string containing the full invoice details.
     */
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

    /**
     * Generates a summary string representation of the invoice using only the order data.
     *
     * @param order The order for which the summary is generated.
     * @return A formatted string containing the summary invoice.
     */
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