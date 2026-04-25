package edu.dccc.mobilephonebook;

import edu.dccc.store.CSVTemplate;
import java.time.LocalDateTime;

/**
 * THE MODEL (MVC Pattern):
 * Represents a single contact. This class defines the "Natural Order" of
 * your data and how it is transformed to and from a physical file.
 */
public class Contact implements Comparable<Contact>, CSVTemplate {
    private String name;
    private String phone;
    private LocalDateTime lastModified;

    /**
     * CRITICAL ARCHITECTURE NOTE:
     * This empty constructor is REQUIRED. The CSVReaderWriter uses "Reflection"
     * to create a blank object before filling it with data via fromCSV().
     */
    public Contact() {}

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.lastModified = LocalDateTime.now();
    }

    // --- Standard Getters and Setters ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }

    @Override
    public String toString() {
        // TASK 1: Return a formatted string for the UI ListView.
        return String.format("%-25s - %s", name, phone);
    }

    /**
     * TASK 2 - NATURAL ORDERING (The Tie-Breaker)
     * This method determines how a TreeSet sorts your contacts.
     */
    @Override
    public int compareTo(Contact other) {
        // Primary Sort: Compare names case-insensitively
        int nameCompare = this.name.compareToIgnoreCase(other.name);
        if (nameCompare != 0) return nameCompare;

        // Secondary Sort: Phone Number
        int phoneCompare = this.phone.compareTo(other.phone);
        if (phoneCompare != 0) return phoneCompare;

        // Tertiary Sort: Timestamp
        return this.lastModified.compareTo(other.lastModified);
    }

    /**
     * TASK 3 - CSV SERIALIZATION
     */
    @Override
    public String toCSV() {
        return name + "," + phone + "," + lastModified.toString();
    }

    /**
     * TASK 4 - CSV DESERIALIZATION
     */
    @Override
    public void fromCSV(String[] data) {
        if (data.length >= 2) {
            this.name = data[0].trim();
            this.phone = data[1].trim();
        }

        // Logic for parsing the timestamp if it exists...
        if (data.length >= 3) {
            try {
                this.lastModified = LocalDateTime.parse(data[2]);
            } catch (Exception e) {
                this.lastModified = LocalDateTime.now();
            }
        } else {
            // Legacy Data Handling
            this.lastModified = LocalDateTime.now();
        }
    }
}