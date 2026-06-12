package com.airline.exception;

public class PassengerNotFoundException    extends RuntimeException {
    public PassengerNotFoundException(String id) { super("Passenger not found: " + id); }
}