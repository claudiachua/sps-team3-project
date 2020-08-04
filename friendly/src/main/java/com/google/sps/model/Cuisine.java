package com.google.sps.model;

/**
 * Represents a Restaurant's Cuisine
 */
public class Cuisine {
    public final String cuisine;

    public Cuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getCuisine() {
        return this.cuisine;
    }

    @Override
    public String toString() {
        return this.cuisine;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
            || (other instanceof Cuisine
            && this.cuisine.equals(((Cuisine) other).getCuisine()));
    }

    @Override
    public int hashCode() {
        return this.cuisine.hashCode();
    }
}
