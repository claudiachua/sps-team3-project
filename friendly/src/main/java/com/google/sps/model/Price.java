package com.google.sps.model;

/**
 * Represents a Restaurant's Price
 */
public class Price {
    public final String price;

    public Price(String price) {
        this.price = price;
    }

    public String getPrice() {
        return this.price;
    }

    @Override
    public String toString() {
        return this.price;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
            || (other instanceof Price
            && this.price.equals(((Price) other).getPrice()));
    }

    @Override
    public int hashCode() {
        return this.price.hashCode();
    }
}
