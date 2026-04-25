package com.amazon.order;

public class Address {
    private String street, city, state, zip, country;

    public Address(String street, String city, String state, String zip, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
    }

    public boolean hasSameAddress(Address other) {
        if (other == null) return false;
        return this.street.equalsIgnoreCase(other.street) &&
                this.zip.equalsIgnoreCase(other.zip);
    }

    public String getFormattedAddress() {
        return street + "\n" + city + ", " + state + " " + zip + "\n" + country;
    }

    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZip() { return zip; }
    public String getCountry() { return country; }
}