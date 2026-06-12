package com.airline.service;

import com.airline.exception.AirlineException;
import com.airline.exception.FlightNotFoundException;
import com.airline.model.Flight;
import com.airline.storage.DataStore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightService {

    private final DataStore store;

    public FlightService(DataStore store) { this.store = store; }


    public Flight addFlight(String flightNumber, String source, String destination,
                            LocalDateTime departure, LocalDateTime arrival,
                            int totalSeats, double price) {
        if (flightNumber == null || flightNumber.isBlank())
            throw new AirlineException("Flight number cannot be blank.");
        if (store.getFlight(flightNumber) != null)
            throw new AirlineException("Flight already exists: " + flightNumber);
        if (departure == null || arrival == null)
            throw new AirlineException("Departure and arrival times are required.");
        if (!arrival.isAfter(departure))
            throw new AirlineException("Arrival time must be after departure time.");
        if (totalSeats <= 0)
            throw new AirlineException("Total seats must be > 0.");
        if (price < 0)
            throw new AirlineException("Ticket price cannot be negative.");

        Flight f = new Flight(flightNumber, source, destination, departure, arrival, totalSeats, price);
        store.addFlight(f);
        return f;
    }


    public void updateFlight(String flightNumber, String source, String destination,
                             LocalDateTime departure, LocalDateTime arrival,
                             int totalSeats, double price) {
        Flight f = getOrThrow(flightNumber);
        int booked = f.getBookedSeats();
        if (totalSeats < booked)
            throw new AirlineException("Cannot reduce total seats below already-booked count (" + booked + ").");
        if (!arrival.isAfter(departure))
            throw new AirlineException("Arrival must be after departure.");

        f.setSource(source);
        f.setDestination(destination);
        f.setDepartureTime(departure);
        f.setArrivalTime(arrival);
        f.setTotalSeats(totalSeats);
        f.setAvailableSeats(totalSeats - booked);
        f.setTicketPrice(price);
        store.updateFlight(f);
    }


    public void cancelFlight(String flightNumber) {
        Flight f = getOrThrow(flightNumber);
        if ("CANCELLED".equals(f.getStatus()))
            throw new AirlineException("Flight is already cancelled.");
        f.setStatus("CANCELLED");
        store.updateFlight(f);
    }


    public void deleteFlight(String flightNumber) {
        getOrThrow(flightNumber);
        store.deleteFlight(flightNumber.toUpperCase());
    }


    public Flight getFlightOrThrow(String fn) { return getOrThrow(fn); }

    public List<Flight> getAllFlights() { return new ArrayList<>(store.getAllFlights()); }

    public List<Flight> searchFlights(String source, String destination, LocalDateTime date) {
        return store.getAllFlights().stream()
                .filter(f -> (source == null || f.getSource().equalsIgnoreCase(source)))
                .filter(f -> (destination == null || f.getDestination().equalsIgnoreCase(destination)))
                .filter(f -> (date == null || f.getDepartureTime().toLocalDate().equals(date.toLocalDate())))
                .filter(f -> !"CANCELLED".equals(f.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Flight> getAvailableFlights() {
        return store.getAllFlights().stream()
                .filter(f -> f.hasAvailableSeats() && "SCHEDULED".equals(f.getStatus()))
                .collect(Collectors.toList());
    }


    public boolean bookSeat(String flightNumber) {
        Flight f = getOrThrow(flightNumber);
        boolean ok = f.bookSeat();
        if (ok) store.updateFlight(f);
        return ok;
    }

    public void releaseSeat(String flightNumber) {
        Flight f = store.getFlight(flightNumber.toUpperCase());
        if (f != null) { f.releaseSeat(); store.updateFlight(f); }
    }

    public String assignNextSeat(String flightNumber, java.util.Set<String> takenSeats) {
        Flight f = getOrThrow(flightNumber);
        String[] cols = {"A","B","C","D","E","F"};
        for (int row = 1; row <= f.getTotalSeats(); row++) {
            for (String col : cols) {
                String seat = row + col;
                if (!takenSeats.contains(seat)) return seat;
            }
        }
        return "?";
    }


    private Flight getOrThrow(String fn) {
        Flight f = store.getFlight(fn.toUpperCase());
        if (f == null) throw new FlightNotFoundException(fn);
        return f;
    }
}