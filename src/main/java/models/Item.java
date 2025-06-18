package models;

public class Item {
    private String name;
    private String price; // Price as a string (e.g., "50.00")
    private String quantity; // Quantity as a string (e.g., "10")

    public Item(String name, String price, String quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    // Getters and Setters
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
}
