package Models;

public class CarrierRating {
    private int id;
    private int carrierId;
    private int customerId;
    private int orderId;
    private int rating; // 1 ile 5 arası
    private String comment;

    public CarrierRating() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
}