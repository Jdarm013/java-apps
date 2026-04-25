package com.amazon.order;

public class Product {
    public enum Condition { New, Used, Reconditioned, Refurbished }

    private String productId, description, imagePath;
    private double price;
    private Condition condition;

    public Product(String productId, String description, double price, Condition condition, String imagePath) {
        this.productId = productId;
        this.description = description;
        this.price = price;
        this.condition = condition;
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-30s (%-13s) - $%.2f", productId, description, condition, price);
    }

    public String getProductId() { return productId; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
}