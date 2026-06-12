package com.airline.exception;

public class NoSeatsAvailableException extends AirlineException {
    public NoSeatsAvailableException(String fn) { super("No seats available on flight: " + fn); }
}