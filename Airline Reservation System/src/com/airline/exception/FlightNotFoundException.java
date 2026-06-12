package com.airline.exception;

public class FlightNotFoundException       extends RuntimeException {
    public FlightNotFoundException(String fn) { super("Flight not found: " + fn); }
}