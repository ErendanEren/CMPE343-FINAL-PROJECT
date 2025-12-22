package Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private int customerId;
    private int carrierId;
    private LocalDateTime orderTime;
    private LocalDateTime requestedDeliveryTime;
    private LocalDateTime deliveredAt;
    private String status;
    private double totalAmount;
    private String customerAddressSnapshot;
    
    // Transient list for processing
    private List<OrderItem> items = new ArrayList<>();

    public Order() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getCarrierId() { return carrierId; }
    public void setCarrierId(int carrierId) { this.carrierId = carrierId; }

    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }

    public LocalDateTime getRequestedDeliveryTime() { return requestedDeliveryTime; }
    public void setRequestedDeliveryTime(LocalDateTime requestedDeliveryTime) { this.requestedDeliveryTime = requestedDeliveryTime; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getCustomerAddressSnapshot() { return customerAddressSnapshot; }
    public void setCustomerAddressSnapshot(String customerAddressSnapshot) { this.customerAddressSnapshot = customerAddressSnapshot; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
