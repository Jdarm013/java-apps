package com.amazon.order;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

public class ReceiptService {
    // FIXED: Added static keyword to allow reference from static methods
    private static NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    public static void sendOrderStatus(Order order) {
        System.out.println("\n--- [AMAZON NOTIFICATION] ---");
        System.out.println("To: " + order.getCustomer().getEmail());
        System.out.println("Status Update: " + order.getOrderStatus()); // Raw enum as requested
        System.out.println("-----------------------------\n");
    }

    public static void sendOrderConfirmationEmail(Order order) {
        System.out.println("--------------------------------------------------");
        System.out.println("✉️   EMAIL TO: " + order.getCustomer().getEmail());
        System.out.println("SUBJECT: Order Confirmation - #" + order.getOrderNumber());
        System.out.println("--------------------------------------------------");
        System.out.println("Hello " + order.getCustomer().getCustomerName() + ",");
        System.out.println("We've received your order! We'll notify you when it ships.");
        // Now works because dateFormat is static
        System.out.println("Initial Est. Delivery: " + order.getEstimatedDeliveryDate().format(dateFormat));
        System.out.println("--------------------------------------------------");
    }

    public void printFinalReceipt(Order order, Payment payment) {
        Customer c = order.getCustomer();
        Address bill = c.getBillingAddress();
        Address ship = c.getShippingAddress();

        System.out.println("\n==================================================");
        System.out.println("                  AMAZON INVOICE                    ");
        System.out.println("==================================================");
        System.out.println("Order Number: " + order.getOrderNumber());
        System.out.println("Order Date:   " + java.time.LocalDate.now().format(dateFormat));
        System.out.println("Status:       " + order.getOrderStatus());
        System.out.println("--------------------------------------------------");

        System.out.println("SOLD TO:");
        System.out.println(c.getCustomerName());
        System.out.println(c.getEmail());

        if (bill.hasSameAddress(ship)) {
            System.out.println(ship.getFormattedAddress());
        } else {
            System.out.println(bill.getStreet());
            System.out.println(bill.getCity() + ", " + bill.getState() + " " + bill.getZip());
            System.out.println(bill.getCountry());
            System.out.println("\nSHIPPING TO:");
            System.out.println(ship.getFormattedAddress());
        }

        System.out.println("--------------------------------------------------");
        System.out.printf("%-35s %-5s %-10s\n", "ITEM", "QTY", "TOTAL");
        for (OrderItem item : order.getItems()) {
            System.out.printf("%-35s %-5d %-10s\n",
                    item.getProduct().getDescription(), item.getQuantity(),
                    moneyFormat.format(item.calculateItemTotal()));
        }
        System.out.println("--------------------------------------------------");
        System.out.println("Estimated Sales Tax :          " + moneyFormat.format(order.salesTax()));
        System.out.println("Shipping & Handling:           " + moneyFormat.format(5.00));
        System.out.println("GRAND TOTAL:                   " + moneyFormat.format(order.calculateGrandTotal()));
        System.out.println("--------------------------------------------------");
        System.out.println("PAID VIA: " + payment.getIssuer() + " (" + payment.getMaskedAccountNumber() + ")");
        System.out.println("==================================================\n");
    }

    public void sendShippingSMS(Order order, Shipment shipment) {
        System.out.println("📱 SMS TO: " + order.getCustomer().getMobilePhone());
        System.out.println("[AMAZON] Your order #" + order.getOrderNumber() + " has shipped via " + shipment.getCarrier() + "!");
        System.out.println("Track here: " + shipment.getTrackingNumber());
    }

    public void sendShipmentDelayAlert(Order order, Shipment s) {
        System.out.println("📱 SMS TO: " + order.getCustomer().getMobilePhone());
        System.out.println("--------------------------------------------------");
        System.out.println("[AMAZON ALERT] Delay for Order #" + order.getOrderNumber());
        System.out.println("Status: Transit delay due to carrier logistics.");
        System.out.println("NOW: " + s.getExpectedArrival().format(dateFormat));
        System.out.println("Check your email for more details.");
        System.out.println("--------------------------------------------------");
    }
}