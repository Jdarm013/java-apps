package com.amazon.order;
import java.util.*;
import java.time.LocalDateTime;

public class Order {
    public enum OrderStatus { PENDING, PROCESSING, SHIPPED, DELIVERED }
    private String orderNumber;
    private Customer customer;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private Shipment shipment;
    private Payment payment;
    private List<OrderItem> orderItems = new ArrayList<>();
    private final double shippingCost = 5.00;

    public Order(String orderNumber, Customer customer, String initialStatus) {
        this.orderNumber = orderNumber;
        this.customer = customer;
        this.status = OrderStatus.valueOf(initialStatus);
        this.orderDate = LocalDateTime.now();
    }

    public void addOrderItem(OrderItem item) { this.orderItems.add(item); }

   public double salesTax() {
        double total = 0;

        for (OrderItem item : orderItems) {total=item.calculateItemTotal();}
            double salesTax = total*.06;
            return salesTax;
        }



    public double calculateGrandTotal() {
        double total = 0;
        for (OrderItem item : orderItems) total += item.calculateItemTotal();
        double salesTax = (total*.06);
        return (total) +(salesTax)+ shippingCost;
    }

    public LocalDateTime getEstimatedDeliveryDate() { return orderDate.plusDays(2); }
    public void setOrderStatus(OrderStatus status) { this.status = status; }
    public OrderStatus getOrderStatus() { return status; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }

    public void setPayment(Payment payment) { this.payment = payment; }

    public Customer getCustomer() { return customer; }
    public String getOrderNumber() { return orderNumber; }
    public List<OrderItem> getItems() { return orderItems; }
}