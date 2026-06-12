package com.airline.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Flight implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String flightNumber;
    private String source;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int totalSeats;
    private int availableSeats;
    private double ticketPrice;
    private String status;

    public Flight(String flightNumber, String source, String destination,
                  LocalDateTime departureTime, LocalDateTime arrivalTime,
                  int totalSeats, double ticketPrice) {
        this.flightNumber  = flightNumber.toUpperCase();
        this.source        = source;
        this.destination   = destination;
        this.departureTime = departureTime;
        this.arrivalTime   = arrivalTime;
        this.totalSeats    = totalSeats;
        this.availableSeats = totalSeats;
        this.ticketPrice   = ticketPrice;
        this.status        = "SCHEDULED";
    }


    public String         getFlightNumber()  { return flightNumber; }
    public String         getSource()        { return source; }
    public String         getDestination()   { return destination; }
    public LocalDateTime  getDepartureTime() { return departureTime; }
    public LocalDateTime  getArrivalTime()   { return arrivalTime; }
    public int            getTotalSeats()    { return totalSeats; }
    public int            getAvailableSeats(){ return availableSeats; }
    public double         getTicketPrice()   { return ticketPrice; }
    public String         getStatus()        { return status; }
    public int            getBookedSeats()   { return totalSeats - availableSeats; }

    public void setSource(String s)               { this.source = s; }
    public void setDestination(String d)          { this.destination = d; }
    public void setDepartureTime(LocalDateTime dt){ this.departureTime = dt; }
    public void setArrivalTime(LocalDateTime at)  { this.arrivalTime = at; }
    public void setTotalSeats(int t)              { this.totalSeats = t; }
    public void setAvailableSeats(int a)          { this.availableSeats = a; }
    public void setTicketPrice(double p)          { this.ticketPrice = p; }
    public void setStatus(String s)               { this.status = s; }

    public boolean hasAvailableSeats() { return availableSeats > 0; }

    public boolean bookSeat() {
        if (availableSeats <= 0) return false;
        availableSeats--;
        return true;
    }

    public void releaseSeat() {
        if (availableSeats < totalSeats) availableSeats++;
    }


    public String toCsv() {
        return String.join("|",
                flightNumber, source, destination,
                departureTime.format(DT_FMT),
                arrivalTime.format(DT_FMT),
                String.valueOf(totalSeats),
                String.valueOf(availableSeats),
                String.valueOf(ticketPrice),
                status);
    }

    public static Flight fromCsv(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 9) throw new IllegalArgumentException("Invalid flight CSV: " + line);
        Flight f = new Flight(
                p[0], p[1], p[2],
                LocalDateTime.parse(p[3], DT_FMT),
                LocalDateTime.parse(p[4], DT_FMT),
                Integer.parseInt(p[5]),
                Double.parseDouble(p[7]));
        f.setAvailableSeats(Integer.parseInt(p[6]));
        f.setStatus(p[8]);
        return f;
    }

    @Override public String toString() {
        return String.format(
                "%-10s %-18s %-18s %-17s %-17s %5d/%5d  PKR %,10.2f  [%s]",
                flightNumber, source, destination,
                departureTime.format(DT_FMT), arrivalTime.format(DT_FMT),
                availableSeats, totalSeats, ticketPrice, status);
    }
}
