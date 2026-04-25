package com.amazon.order;
import java.util.*;

public class AmazonOrdersMain {
    private List<Product> productCatalog = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private ReceiptService receipt = new ReceiptService();

    public void populateSystemData() {
        Address sherlockHome = new Address("221Baker St","London","London","NW1 6XE","England");
        Address homerHome = new Address("742 Evergreen Terrace","Springfield","IL","62704","USA");
        Address wayneManor = new Address("1007 Mountain Dr","Gotham","NJ","07001","USA");
        Address wayneTower = new Address("Wayne Tower, Suite 1","Gotham","NJ","07002","USA");
        Address griffinHome = new Address("31 Spooner St","Quahog","RI","02801","USA");
        Address cartmanHome = new Address("213 Towley Dr","SouthPark","Colorado","804401","USA");

        customers.add(new Customer("C-001", "Sherlock Holmes", "detective@scotlandyard.com", "555-010-2211", sherlockHome, sherlockHome));
        customers.add(new Customer("C-002", "Homer Simpson", "chunkylover53@aol.com", "555-733-4422", homerHome, homerHome));
        customers.add(new Customer("C-003", "Bruce Wayne", "bwayne@waynetech.com", "555-999-0000", wayneManor, wayneTower));
        customers.add(new Customer("C-004", "Peter Griffin", "pgriffin@pawtucket.com", "555-123-4567", griffinHome, griffinHome));
        customers.add(new Customer("C-005", "Eric Cartman", "respectma@authority.com", "555-000-1234", cartmanHome, cartmanHome));

        productCatalog.add(new Product("P-001", "Batmobile (Scale Model)", 49.99, Product.Condition.New, "images/batmobile.png"));
        productCatalog.add(new Product("P-002", "Magnifying Glass", 15.50, Product.Condition.Used, "images/glass.png"));
        productCatalog.add(new Product("P-003", "Utility Belt", 120.00, Product.Condition.Refurbished, "images/belt.png"));
        productCatalog.add(new Product("P-004", "Box of Donuts", 12.99, Product.Condition.New, "images/donuts.png"));
        productCatalog.add(new Product("P-005", "Peter's Glasses", 25.00, Product.Condition.Used, "images/glasses.png"));
        productCatalog.add(new Product("P-006", "Red Jacket", 85.00, Product.Condition.New, "images/jacket.png"));
        productCatalog.add(new Product("P-007", "KFC Bucket", 19.99, Product.Condition.New, "images/kfc.png"));
    }

    public void simulateOrderLifecycle() {
        Scanner scanner = new Scanner(System.in);
        Customer currentCustomer = null;

        while (currentCustomer == null) {
            System.out.println("Welcome to Amazon! Enter Customer Name or ID: ");
            String loginInput = scanner.nextLine();
            for (Customer c : customers) {
                if (c.getCustomerId().equalsIgnoreCase(loginInput) || c.getCustomerName().equalsIgnoreCase(loginInput)) {
                    currentCustomer = c;
                }
            }
            if (currentCustomer == null) System.out.println("Customer not found. Try again.");
        }

        Order newOrder = new Order("ORD-701-12345", currentCustomer, "PENDING");
        boolean shopping = true;
        while (shopping) {
            System.out.println("\n--- Current Catalog ---");
            for (Product p : productCatalog) System.out.println(p);
            System.out.print("\nEnter Product Name/ID (or 'done' to checkout): ");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("done")) {
                shopping = false;
            } else {
                Product found = null;
                for (Product p : productCatalog) {
                    if (p.getProductId().equalsIgnoreCase(choice) || p.getDescription().toLowerCase().contains(choice.toLowerCase())) {
                        found = p;
                    }
                }

                if (found != null) {
                    int qty = 0;
                    boolean validQty = false;
                    while (!validQty) {
                        System.out.print("Enter quantity for " + found.getDescription() + ": ");
                        try {
                            qty = Integer.parseInt(scanner.nextLine());
                            if (qty > 0) {
                                validQty = true;
                            } else {
                                System.out.println("Invalid input. Please Try Again.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please try again.");
                        }
                    }
                    newOrder.addOrderItem(new OrderItem(found, qty));
                    System.out.println("Added to cart.");
                } else {
                    System.out.println("Invalid selection. '" + choice + "' not found in catalog. Please try again.");
                }
            }
        }

        if (newOrder.getItems().isEmpty()) {
            System.out.println("Cart is empty. Exiting simulation.");
            return;
        }

        System.out.println("\n>>> STEP 1: ORDER PLACEMENT");
        ReceiptService.sendOrderStatus(newOrder);
        ReceiptService.sendOrderConfirmationEmail(newOrder);

        System.out.println("\n...2 Days Pass: Items are being sourced ...");
        System.out.println(">>> STEP 2: WAREHOUSE PROCESSING");
        newOrder.setOrderStatus(Order.OrderStatus.PROCESSING);
        ReceiptService.sendOrderStatus(newOrder);

        System.out.println(">>> STEP 3: ITEM SHIPPED & PAYMENT CHARGED");
        newOrder.setOrderStatus(Order.OrderStatus.SHIPPED);
        Shipment s = new Shipment("1Z3Y67380", "UPS", Shipment.ShipmentSpeed.TWO_DAY, newOrder.getEstimatedDeliveryDate());
        Payment p = new Payment(Payment.PaymentType.CREDIT_CARD, "5678", "Amazon Visa");
        newOrder.setShipment(s);
        newOrder.setPayment(p);

        receipt.sendShippingSMS(newOrder, s);
        receipt.printFinalReceipt(newOrder, p);

        System.out.println("\n>>> STEP 4: CARRIER TRANSIT UPDATE (DELAY ENCOUNTERED)");
        s.updateDeliveryEstimate(2);
        receipt.sendShipmentDelayAlert(newOrder, s);

        System.out.println("\n... Package travels through carrier network ...");
        System.out.println(">>> STEP 5: FINAL DELIVERY");
        newOrder.setOrderStatus(Order.OrderStatus.DELIVERED);
        ReceiptService.sendOrderStatus(newOrder);
        System.out.println("📱 SMS TO: " + newOrder.getCustomer().getMobilePhone());
        System.out.println("[AMAZON] DELIVERED at 12:07 PM. Your package is at the front door.");
    }

    public static void main(String[] args) {
        AmazonOrdersMain app = new AmazonOrdersMain();
        app.populateSystemData();
        app.simulateOrderLifecycle();
    }
}