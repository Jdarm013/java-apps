package com.amazon.order;

public class Payment {
    public enum PaymentType { CREDIT_CARD, BANK_TRANSFER, GIFT_CARD }
    private String issuer;
    private String accountNumber;

    public Payment(PaymentType type, String accountNumber, String issuer) {
        this.accountNumber = accountNumber;
        this.issuer = issuer;
    }

    public String getMaskedAccountNumber() {
        if (accountNumber == null || accountNumber.length() < 4) return "****";
        return "**** **** **** " + accountNumber.substring(accountNumber.length() - 4);
    }

    public String getIssuer() { return issuer; }
}