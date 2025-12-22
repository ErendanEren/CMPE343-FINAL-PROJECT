package Models;

public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private double amountKg;
    private double unitPrice;
    private double lineTotal;

    public OrderItem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public double getAmountKg() { return amountKg; }
    public void setAmountKg(double amountKg) { this.amountKg = amountKg; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getLineTotal() { return lineTotal; }
    public void setLineTotal(double lineTotal) { this.lineTotal = lineTotal; }
}
