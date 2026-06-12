package com.airline.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public enum Status { CONFIRMED, CANCELLED, CHECKED_IN, COMPLETED }

    private String        reservationId;
    private String        passengerId;
    private String        flightNumber;
    private String        seatNumber;
    private double        totalFare;
    private LocalDateTime bookingTime;
    private Status        status;

    public Reservation(String reservationId, String passengerId,
                       String flightNumber, String seatNumber, double totalFare) {
        this.reservationId = reservationId;
        this.passengerId   = passengerId;
        this.flightNumber  = flightNumber;
        this.seatNumber    = seatNumber;
        this.totalFare     = totalFare;
        this.bookingTime   = LocalDateTime.now();
        this.status        = Status.CONFIRMED;
    }


    public String        getReservationId() { return reservationId; }
    public String        getPassengerId()   { return passengerId; }
    public String        getFlightNumber()  { return flightNumber; }
    public String        getSeatNumber()    { return seatNumber; }
    public double        getTotalFare()     { return totalFare; }
    public LocalDateTime getBookingTime()   { return bookingTime; }
    public Status        getStatus()        { return status; }


    public void setStatus(Status s)        { this.status = s; }
    public void setSeatNumber(String s)    { this.seatNumber = s; }

    public boolean isActive()    { return status == Status.CONFIRMED || status == Status.CHECKED_IN; }
    public boolean isCancelled() { return status == Status.CANCELLED; }


    public String toCsv() {
        return String.join("|",
                reservationId, passengerId, flightNumber,
                seatNumber, String.valueOf(totalFare),
                bookingTime.format(DT_FMT), status.name());
    }

    public static Reservation fromCsv(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 7) throw new IllegalArgumentException("Invalid reservation CSV: " + line);
        Reservation r = new Reservation(p[0], p[1], p[2], p[3], Double.parseDouble(p[4]));
        r.bookingTime = LocalDateTime.parse(p[5], DT_FMT);
        r.status      = Status.valueOf(p[6]);
        return r;
    }

    @Override public String toString() {
        return String.format(
                "ResID: %-14s  Flight: %-10s  Seat: %-6s  Fare: PKR %,10.2f  Booked: %-17s  [%s]",
                reservationId, flightNumber, seatNumber,
                totalFare, bookingTime.format(DT_FMT), status);
    }
}