package com.amazon.order;

import java.time.LocalDateTime;

public class Shipment {
    public enum ShipmentSpeed { STANDARD, TWO_DAY, NEXT_DAY }

    private String trackingNumber;
    private String carrier;
    private ShipmentSpeed speed;
    private LocalDateTime expectedArrival;

    public Shipment(String trackingNumber, String carrier, ShipmentSpeed speed, LocalDateTime initialArrival) {
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.speed = speed;
        this.expectedArrival = initialArrival;
    }

    public void updateDeliveryEstimate(int daysToAdd) {
        this.expectedArrival = this.expectedArrival.plusDays(daysToAdd);
    }

    public String getTrackingNumber() { return trackingNumber; }
    public String getCarrier() { return carrier; }
    public LocalDateTime getExpectedArrival() { return expectedArrival; }
}