package com.airline.service;

import com.airline.exception.*;
import com.airline.model.*;
import com.airline.storage.DataStore;
import com.airline.util.AppUtil;

import java.util.*;
import java.util.stream.Collectors;

public class ReservationService {

    private final DataStore        store;
    private final FlightService    flightService;
    private final PassengerService passengerService;

    public ReservationService(DataStore store,
                              FlightService flightService,
                              PassengerService passengerService) {
        this.store            = store;
        this.flightService    = flightService;
        this.passengerService = passengerService;
    }

    public Reservation bookFlight(String passengerId, String flightNumber) {
        Passenger passenger = passengerService.getPassengerOrThrow(passengerId);

        Flight flight = flightService.getFlightOrThrow(flightNumber);
        if ("CANCELLED".equals(flight.getStatus()))
            throw new AirlineException("Cannot book a cancelled flight.");
        if (!flight.hasAvailableSeats())
            throw new NoSeatsAvailableException(flightNumber);

        boolean alreadyBooked = store.getReservationsByPassenger(passengerId).stream()
                .anyMatch(r -> r.getFlightNumber().equalsIgnoreCase(flightNumber) && r.isActive());
        if (alreadyBooked)
            throw new AirlineException("Passenger already has an active booking on this flight.");

        Set<String> takenSeats = store.getReservationsByFlight(flightNumber).stream()
                .filter(Reservation::isActive)
                .map(Reservation::getSeatNumber)
                .collect(Collectors.toSet());

        String seatNumber    = flightService.assignNextSeat(flightNumber, takenSeats);
        String reservationId = AppUtil.newReservationId();
        double fare          = flight.getTicketPrice();

        boolean seated = flightService.bookSeat(flightNumber);
        if (!seated) throw new NoSeatsAvailableException(flightNumber);

        Reservation reservation = new Reservation(reservationId, passengerId, flightNumber, seatNumber, fare);
        store.addReservation(reservation);
        passengerService.linkReservation(passengerId, reservationId);

        return reservation;
    }

    public void cancelReservation(String reservationId) {
        Reservation r = getOrThrow(reservationId);
        if (r.isCancelled())
            throw new AirlineException("Reservation is already cancelled.");
        r.setStatus(Reservation.Status.CANCELLED);
        store.updateReservation(r);
        flightService.releaseSeat(r.getFlightNumber());
        passengerService.unlinkReservation(r.getPassengerId(), reservationId);
    }

    public void checkIn(String reservationId) {
        Reservation r = getOrThrow(reservationId);
        if (r.getStatus() != Reservation.Status.CONFIRMED)
            throw new AirlineException("Only CONFIRMED reservations can be checked in.");
        r.setStatus(Reservation.Status.CHECKED_IN);
        store.updateReservation(r);
    }

    public Reservation getReservationOrThrow(String id) { return getOrThrow(id); }
    public List<Reservation> getAllReservations() {
        return new ArrayList<>(store.getAllReservations());
    }

    public List<Reservation> getReservationsForPassenger(String passengerId) {
        return store.getReservationsByPassenger(passengerId);
    }

    public List<Reservation> getReservationsForFlight(String flightNumber) {
        return store.getReservationsByFlight(flightNumber);
    }

    public double getTotalRevenue() {
        return store.getAllReservations().stream()
                .filter(r -> !r.isCancelled())
                .mapToDouble(Reservation::getTotalFare).sum();
    }

    public Map<String, Double> getRevenueByFlight() {
        Map<String, Double> map = new LinkedHashMap<>();
        for (Reservation r : store.getAllReservations()) {
            if (!r.isCancelled())
                map.merge(r.getFlightNumber(), r.getTotalFare(), Double::sum);
        }
        return map;
    }

    private Reservation getOrThrow(String id) {
        Reservation r = store.getReservation(id);
        if (r == null) throw new ReservationNotFoundException(id);
        return r;
    }
}