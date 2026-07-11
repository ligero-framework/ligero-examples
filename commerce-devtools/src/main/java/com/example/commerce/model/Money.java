package com.example.commerce.model;

/** Money as minor units (cents) to avoid floating point. */
public record Money(String currency, long cents) {
    public Money plus(Money other) { return new Money(currency, cents + other.cents); }
    public Money times(int qty) { return new Money(currency, cents * qty); }
    public static Money usd(long cents) { return new Money("USD", cents); }
}
