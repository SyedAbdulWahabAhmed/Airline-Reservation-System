package com.airline.service;

import com.airline.exception.AirlineException;
import com.airline.exception.PassengerNotFoundException;
import com.airline.model.Passenger;
import com.airline.storage.DataStore;
import com.airline.util.AppUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PassengerService {

    private final DataStore store;

    public PassengerService(DataStore store) { this.store = store; }


    public Passenger addPassenger(String name, String email, String phone, String address) {
        validate(name, email, phone);
        if (store.findPassengerByEmail(email) != null)
            throw new AirlineException("A passenger with this email already exists: " + email);
        Passenger p = new Passenger(AppUtil.newPassengerId(), name.trim(), email.trim(), phone.trim(), address.trim());
        store.addPassenger(p);
        return p;
    }


    public void updatePassenger(String passengerId, String name, String email,
                                String phone, String address) {
        Passenger p = getOrThrow(passengerId);
        validate(name, email, phone);
        Passenger existing = store.findPassengerByEmail(email);
        if (existing != null && !existing.getPassengerId().equals(passengerId))
            throw new AirlineException("Another passenger already uses this email.");
        p.setName(name.trim());
        p.setEmail(email.trim());
        p.setPhone(phone.trim());
        p.setAddress(address.trim());
        store.updatePassenger(p);
    }

    public void deletePassenger(String passengerId) {
        Passenger p = getOrThrow(passengerId);
        if (!p.getReservationIds().isEmpty())
            throw new AirlineException("Cannot delete passenger with active reservations. Cancel them first.");
        store.deletePassenger(passengerId);
    }

    public Passenger getPassengerOrThrow(String id) { return getOrThrow(id); }
    public Passenger findByEmail(String email)       { return store.findPassengerByEmail(email); }

    public List<Passenger> getAllPassengers() { return new ArrayList<>(store.getAllPassengers()); }

    public List<Passenger> searchByName(String query) {
        String q = query.toLowerCase();
        return store.getAllPassengers().stream()
                .filter(p -> p.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public void linkReservation(String passengerId, String reservationId) {
        Passenger p = getOrThrow(passengerId);
        p.addReservationId(reservationId);
        store.updatePassenger(p);
    }

    public void unlinkReservation(String passengerId, String reservationId) {
        Passenger p = store.getPassenger(passengerId);
        if (p != null) { p.removeReservationId(reservationId); store.updatePassenger(p); }
    }

    private Passenger getOrThrow(String id) {
        Passenger p = store.getPassenger(id);
        if (p == null) throw new PassengerNotFoundException(id);
        return p;
    }

    private void validate(String name, String email, String phone) {
        if (name == null || name.isBlank())
            throw new AirlineException("Name cannot be blank.");
        if (!AppUtil.isValidEmail(email))
            throw new AirlineException("Invalid email address: " + email);
        if (!AppUtil.isValidPhone(phone))
            throw new AirlineException("Invalid phone number: " + phone);
    }
}