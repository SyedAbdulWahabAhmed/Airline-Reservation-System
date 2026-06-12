package com.airline.storage;

import com.airline.model.*;
import com.airline.util.AppUtil;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Handles all file I/O. Loads data on startup and saves after every mutation.
 * Files live in a "data/" directory relative to the working directory.
 */
public class DataStore {

    private static final String DATA_DIR      = "data";
    private static final String FLIGHTS_FILE  = DATA_DIR + "/flights.csv";
    private static final String PASSENGERS_FILE = DATA_DIR + "/passengers.csv";
    private static final String RESERVATIONS_FILE = DATA_DIR + "/reservations.csv";
    private static final String USERS_FILE    = DATA_DIR + "/users.csv";

    // ── In-memory maps ─────────────────────────────────────────────────────
    private final Map<String, Flight>      flights      = new LinkedHashMap<>();
    private final Map<String, Passenger>   passengers   = new LinkedHashMap<>();
    private final Map<String, Reservation> reservations = new LinkedHashMap<>();
    private final Map<String, User>        users        = new LinkedHashMap<>();

    // ── Singleton ──────────────────────────────────────────────────────────
    private static DataStore instance;
    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    private DataStore() {
        ensureDataDir();
        loadAll();
        seedDefaultAdmin();
    }

    // ── Init helpers ───────────────────────────────────────────────────────
    private void ensureDataDir() {
        try { Files.createDirectories(Paths.get(DATA_DIR)); }
        catch (IOException e) { throw new RuntimeException("Cannot create data dir", e); }
    }

    private void seedDefaultAdmin() {
        if (!users.containsKey("admin")) {
            User admin = new User("admin", AppUtil.hashPassword("admin123"), User.Role.ADMIN);
            users.put("admin", admin);
            saveUsers();
        }
    }

    // ── Load ───────────────────────────────────────────────────────────────
    private void loadAll() {
        loadFlights();
        loadPassengers();
        loadReservations();
        loadUsers();
    }

    private void loadFlights() {
        readLines(FLIGHTS_FILE).forEach(line -> {
            try {
                Flight f = Flight.fromCsv(line);
                flights.put(f.getFlightNumber(), f);
            } catch (Exception e) {
                System.err.println("WARN: skipping bad flight line: " + e.getMessage());
            }
        });
    }

    private void loadPassengers() {
        int maxSeq = 0;
        for (String line : readLines(PASSENGERS_FILE)) {
            try {
                Passenger p = Passenger.fromCsv(line);
                passengers.put(p.getPassengerId(), p);
                String digits = p.getPassengerId().replaceAll("[^0-9]", "");
                if (!digits.isEmpty()) maxSeq = Math.max(maxSeq, Integer.parseInt(digits));
            } catch (Exception e) {
                System.err.println("WARN: skipping bad passenger line: " + e.getMessage());
            }
        }
        AppUtil.seedSequence(maxSeq);
    }

    private void loadReservations() {
        readLines(RESERVATIONS_FILE).forEach(line -> {
            try {
                Reservation r = Reservation.fromCsv(line);
                reservations.put(r.getReservationId(), r);
            } catch (Exception e) {
                System.err.println("WARN: skipping bad reservation line: " + e.getMessage());
            }
        });
    }

    private void loadUsers() {
        readLines(USERS_FILE).forEach(line -> {
            try {
                User u = User.fromCsv(line);
                users.put(u.getUsername(), u);
            } catch (Exception e) {
                System.err.println("WARN: skipping bad user line: " + e.getMessage());
            }
        });
    }

    private List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("WARN: cannot read " + path + ": " + e.getMessage());
        }
        return lines;
    }


    public void saveFlights() {
        writeLines(FLIGHTS_FILE, flights.values().stream()
                .map(Flight::toCsv).toArray(String[]::new));
    }

    public void savePassengers() {
        writeLines(PASSENGERS_FILE, passengers.values().stream()
                .map(Passenger::toCsv).toArray(String[]::new));
    }

    public void saveReservations() {
        writeLines(RESERVATIONS_FILE, reservations.values().stream()
                .map(Reservation::toCsv).toArray(String[]::new));
    }

    public void saveUsers() {
        writeLines(USERS_FILE, users.values().stream()
                .map(User::toCsv).toArray(String[]::new));
    }

    private void writeLines(String path, String[] lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String line : lines) { bw.write(line); bw.newLine(); }
        } catch (IOException e) {
            System.err.println("ERROR saving " + path + ": " + e.getMessage());
        }
    }


    public void addFlight(Flight f)        { flights.put(f.getFlightNumber(), f); saveFlights(); }
    public void updateFlight(Flight f)     { flights.put(f.getFlightNumber(), f); saveFlights(); }
    public void deleteFlight(String fn)    { flights.remove(fn); saveFlights(); }
    public Flight getFlight(String fn)     { return flights.get(fn.toUpperCase()); }
    public Collection<Flight> getAllFlights() { return Collections.unmodifiableCollection(flights.values()); }

    public void addPassenger(Passenger p)  { passengers.put(p.getPassengerId(), p); savePassengers(); }
    public void updatePassenger(Passenger p){ passengers.put(p.getPassengerId(), p); savePassengers(); }
    public void deletePassenger(String id) { passengers.remove(id); savePassengers(); }
    public Passenger getPassenger(String id){ return passengers.get(id); }
    public Collection<Passenger> getAllPassengers() { return Collections.unmodifiableCollection(passengers.values()); }

    public Passenger findPassengerByEmail(String email) {
        return passengers.values().stream()
                .filter(p -> p.getEmail().equalsIgnoreCase(email))
                .findFirst().orElse(null);
    }

    public void addReservation(Reservation r)    { reservations.put(r.getReservationId(), r); saveReservations(); }
    public void updateReservation(Reservation r) { reservations.put(r.getReservationId(), r); saveReservations(); }
    public Reservation getReservation(String id) { return reservations.get(id); }
    public Collection<Reservation> getAllReservations() {
        return Collections.unmodifiableCollection(reservations.values());
    }

    public List<Reservation> getReservationsByPassenger(String passengerId) {
        List<Reservation> list = new ArrayList<>();
        for (Reservation r : reservations.values())
            if (r.getPassengerId().equals(passengerId)) list.add(r);
        return list;
    }

    public List<Reservation> getReservationsByFlight(String flightNumber) {
        List<Reservation> list = new ArrayList<>();
        for (Reservation r : reservations.values())
            if (r.getFlightNumber().equalsIgnoreCase(flightNumber)) list.add(r);
        return list;
    }


    public void addUser(User u)       { users.put(u.getUsername(), u); saveUsers(); }
    public void updateUser(User u)    { users.put(u.getUsername(), u); saveUsers(); }
    public User getUser(String uname) { return users.get(uname); }
    public Collection<User> getAllUsers() { return Collections.unmodifiableCollection(users.values()); }
}