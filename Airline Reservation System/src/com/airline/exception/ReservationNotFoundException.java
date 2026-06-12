package com.airline.exception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String id) { super("Reservation not found: " + id); }
}