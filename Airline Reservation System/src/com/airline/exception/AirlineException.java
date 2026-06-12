package com.airline.exception;

public class AirlineException extends RuntimeException {
    public AirlineException(String message) { super(message); }
    public AirlineException(String message, Throwable cause) { super(message, cause); }
}