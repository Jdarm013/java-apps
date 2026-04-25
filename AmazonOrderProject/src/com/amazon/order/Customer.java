package com.amazon.order;

public class Customer {
    private String customerId, customerName, email, mobilePhone;
    private Address billingAddress, shippingAddress;

    public Customer(String id, String name, String email, String phone, Address billing, Address shipping) {
        this.customerId = id;
        this.customerName = name;
        this.email = email;
        this.mobilePhone = phone;
        this.billingAddress = billing;
        this.shippingAddress = shipping;
    }

    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getEmail() { return email; }
    public String getMobilePhone() { return mobilePhone; }
    public Address getShippingAddress() { return shippingAddress; }
    public Address getBillingAddress() { return billingAddress; }
}