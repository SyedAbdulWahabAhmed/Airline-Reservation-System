package com.airline;

import com.airline.model.*;
import com.airline.service.*;
import com.airline.storage.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static DataStore store;
    private static AuthService authService;
    private static FlightService flightService;
    private static PassengerService passengerService;
    private static ReservationService reservationService;

    public static void main(String[] args) {

        store = DataStore.getInstance();
        authService = new AuthService(store);
        flightService = new FlightService(store);
        passengerService = new PassengerService(store);
        reservationService = new ReservationService(store, flightService, passengerService);

        System.out.println("====================================================");
        System.out.println("      WELCOME TO AIRLINE MANAGEMENT SYSTEM          ");
        System.out.println("====================================================");

        boolean running = true;
        while (running) {
            try {
                if (!authService.isLoggedIn()) {
                    running = showWelcomeMenu();
                } else if (authService.isAdmin()) {
                    showAdminMenu();
                } else {
                    showCustomerMenu();
                }
            } catch (Exception e) {
                System.out.println("\n[ERROR] " + e.getMessage());
            }
        }
        System.out.println("\nThank you for using Airline Management System. Goodbye!");
    }


    private static boolean showWelcomeMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Login");
        System.out.println("2. Register New Customer");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                handleLogin();
                break;
            case "2":
                handleRegistration();
                break;
            case "3":
                return false;
            default:
                System.out.println("Invalid choice. Try again.");
        }
        return true;
    }

    private static void handleLogin() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Username: ");
        String user = scanner.nextLine().trim();
        System.out.print("Password: ");
        String pass = scanner.nextLine().trim();

        User loggedInUser = authService.login(user, pass);
        System.out.println("\n[SUCCESS] Welcome back, " + loggedInUser.getUsername() + "! (" + loggedInUser.getRole() + ")");
    }

    private static void handleRegistration() {
        System.out.println("\n--- PASSENGER PROFILE REGISTRATION ---");
        System.out.print("Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Address: ");
        String address = scanner.nextLine().trim();


        Passenger p = passengerService.addPassenger(name, email, phone, address);
        System.out.println("Passenger profile created with ID: " + p.getPassengerId());


        System.out.println("\n--- CREATE ACCOUNT CREDENTIALS ---");
        System.out.print("Choose Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Choose Password (min 6 chars): ");
        String password = scanner.nextLine().trim();

        authService.registerCustomer(username, password, p.getPassengerId());
        System.out.println("[SUCCESS] Account registered successfully! You can login now.");
    }


    private static void showAdminMenu() {
        System.out.println("\n================ ADMIN DASHBOARD ================");
        System.out.println("1. Schedule/Add New Flight");
        System.out.println("2. View All Flights");
        System.out.println("3. Cancel a Flight");
        System.out.println("4. View All Passenger Profiles");
        System.out.println("5. View All System Reservations");
        System.out.println("6. View Financial Revenue Statistics");
        System.out.println("7. Logout");
        System.out.print("Select Admin Action: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                adminAddFlight();
                break;
            case "2":
                adminViewAllFlights();
                break;
            case "3":
                adminCancelFlight();
                break;
            case "4":
                adminViewPassengers();
                break;
            case "5":
                adminViewReservations();
                break;
            case "6":
                adminViewRevenue();
                break;
            case "7":
                authService.logout();
                System.out.println("[SUCCESS] Logged out successfully.");
                break;
            default:
                System.out.println("Invalid Action.");
        }
    }

    private static void adminAddFlight() {
        System.out.println("\n--- SCHEDULE NEW FLIGHT ---");
        System.out.print("Flight Number (e.g., PK-301): ");
        String fn = scanner.nextLine().trim();
        System.out.print("Source City: ");
        String src = scanner.nextLine().trim();
        System.out.print("Destination City: ");
        String dest = scanner.nextLine().trim();
        System.out.print("Departure Time (yyyy-MM-dd HH:mm): ");
        LocalDateTime dep = LocalDateTime.parse(scanner.nextLine().trim(), formatter);
        System.out.print("Arrival Time (yyyy-MM-dd HH:mm): ");
        LocalDateTime arr = LocalDateTime.parse(scanner.nextLine().trim(), formatter);
        System.out.print("Total Capacity Seats: ");
        int seats = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Ticket Price (PKR): ");
        double price = Double.parseDouble(scanner.nextLine().trim());

        Flight f = flightService.addFlight(fn, src, dest, dep, arr, seats, price);
        System.out.println("[SUCCESS] Flight scheduled successfully!\n" + f);
    }

    private static void adminViewAllFlights() {
        System.out.println("\n--- ALL SCHEDULED FLIGHTS ---");
        List<Flight> flights = flightService.getAllFlights();
        if (flights.isEmpty()) {
            System.out.println("No flights available in the database.");
            return;
        }
        flights.forEach(System.out::println);
    }

    private static void adminCancelFlight() {
        System.out.print("\nEnter Flight Number to cancel: ");
        String fn = scanner.nextLine().trim();
        flightService.cancelFlight(fn);
        System.out.println("[SUCCESS] Flight status updated to CANCELLED.");
    }

    private static void adminViewPassengers() {
        System.out.println("\n--- REGISTERED PASSENGERS ---");
        List<Passenger> passengers = passengerService.getAllPassengers();
        if (passengers.isEmpty()) {
            System.out.println("No passenger profiles registered yet.");
            return;
        }
        passengers.forEach(System.out::println);
    }

    private static void adminViewReservations() {
        System.out.println("\n--- GLOBAL RESERVATIONS LIST ---");
        List<Reservation> reservations = reservationService.getAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("No bookings recorded in the system.");
            return;
        }
        reservations.forEach(System.out::println);
    }

    private static void adminViewRevenue() {
        System.out.println("\n--- SYSTEM REVENUE REPORT ---");
        System.out.printf("Total Active Gross Revenue: PKR %,10.2f\n", reservationService.getTotalRevenue());
        System.out.println("\nRevenue Distribution by Flight:");
        Map<String, Double> breakDown = reservationService.getRevenueByFlight();
        if (breakDown.isEmpty()) {
            System.out.println("No revenue generated yet.");
        } else {
            breakDown.forEach((fn, rev) -> System.out.printf("Flight %-10s : PKR %,10.2f\n", fn, rev));
        }
    }


    private static void showCustomerMenu() {
        User user = authService.getCurrentUser();
        System.out.println("\n================ CUSTOMER PORTAL ================");
        System.out.println("Welcome, " + user.getUsername() + " | Profile ID: " + user.getLinkedPassengerId());
        System.out.println("1. Search Active Flights");
        System.out.println("2. Book a Flight Ticket");
        System.out.println("3. View My Bookings / Flight Tickets");
        System.out.println("4. Online Flight Check-In");
        System.out.println("5. Cancel Ticket Reservation");
        System.out.println("6. Logout");
        System.out.print("Select Action: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                customerSearchFlights();
                break;
            case "2":
                customerBookFlight();
                break;
            case "3":
                customerViewMyBookings();
                break;
            case "4":
                customerCheckIn();
                break;
            case "5":
                customerCancelBooking();
                break;
            case "6":
                authService.logout();
                System.out.println("[SUCCESS] Logged out successfully.");
                break;
            default:
                System.out.println("Invalid Action.");
        }
    }

    private static void customerSearchFlights() {
        System.out.println("\n--- SEARCH FLIGHTS ---");
        System.out.print("Enter Source City (Or leave blank for any): ");
        String src = scanner.nextLine().trim();
        src = src.isEmpty() ? null : src;

        System.out.print("Enter Destination City (Or leave blank for any): ");
        String dest = scanner.nextLine().trim();
        dest = dest.isEmpty() ? null : dest;

        System.out.print("Enter Departure Date (yyyy-MM-dd) (Or leave blank for any): ");
        String dateStr = scanner.nextLine().trim();
        LocalDateTime searchDate = null;
        if (!dateStr.isEmpty()) {
            searchDate = LocalDateTime.parse(dateStr + " 00:00", formatter);
        }

        List<Flight> results = flightService.searchFlights(src, dest, searchDate);
        if (results.isEmpty()) {
            System.out.println("No matching scheduled flights found.");
            return;
        }
        System.out.println("\nMatching Flights Available:");
        results.forEach(System.out::println);
    }

    private static void customerBookFlight() {
        String passengerId = authService.getCurrentUser().getLinkedPassengerId();
        System.out.print("\nEnter Flight Number you want to book (e.g. PK-301): ");
        String fn = scanner.nextLine().trim().toUpperCase();

        Reservation res = reservationService.bookFlight(passengerId, fn);
        System.out.println("\n[SUCCESS] Ticket Confirmed Successfully!");
        System.out.println(res);
    }

    private static void customerViewMyBookings() {
        System.out.println("\n--- MY RESERVATION TICKETS ---");
        String passengerId = authService.getCurrentUser().getLinkedPassengerId();
        List<Reservation> myRes = reservationService.getReservationsForPassenger(passengerId);
        if (myRes.isEmpty()) {
            System.out.println("You have no booking records.");
            return;
        }
        myRes.forEach(System.out::println);
    }

    private static void customerCheckIn() {
        System.out.print("\nEnter your Reservation ID for Web Check-In: ");
        String resId = scanner.nextLine().trim();

        // Security verification: Ensure this booking belongs to the current logged-in passenger
        Reservation res = reservationService.getReservationOrThrow(resId);
        String currentPassengerId = authService.getCurrentUser().getLinkedPassengerId();

        if (!res.getPassengerId().equals(currentPassengerId)) {
            System.out.println("[ERROR] Unauthorized action. This reservation does not belong to your account.");
            return;
        }

        reservationService.checkIn(resId);
        System.out.println("[SUCCESS] Checked-In successfully! Enjoy your flight.");
    }

    private static void customerCancelBooking() {
        System.out.print("\nEnter Reservation ID you wish to CANCEL: ");
        String resId = scanner.nextLine().trim();


        Reservation res = reservationService.getReservationOrThrow(resId);
        String currentPassengerId = authService.getCurrentUser().getLinkedPassengerId();

        if (!res.getPassengerId().equals(currentPassengerId)) {
            System.out.println("[ERROR] Unauthorized action.");
            return;
        }

        reservationService.cancelReservation(resId);
        System.out.println("[SUCCESS] Your ticket reservation has been cancelled. Seat released.");
    }
}