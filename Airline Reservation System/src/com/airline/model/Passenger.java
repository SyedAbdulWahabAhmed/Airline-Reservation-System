package com.airline.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Passenger implements Serializable {
    private static final long serialVersionUID = 1L;

    private String passengerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private List<String> reservationIds;

    public Passenger(String passengerId, String name, String email,
                     String phone, String address) {
        this.passengerId    = passengerId;
        this.name           = name;
        this.email          = email;
        this.phone          = phone;
        this.address        = address;
        this.reservationIds = new ArrayList<>();
    }


    public String       getPassengerId()     { return passengerId; }
    public String       getName()            { return name; }
    public String       getEmail()           { return email; }
    public String       getPhone()           { return phone; }
    public String       getAddress()         { return address; }
    public List<String> getReservationIds()  { return reservationIds; }


    public void setName(String n)           { this.name = n; }
    public void setEmail(String e)          { this.email = e; }
    public void setPhone(String p)          { this.phone = p; }
    public void setAddress(String a)        { this.address = a; }

    public void addReservationId(String id)    { reservationIds.add(id); }
    public void removeReservationId(String id) { reservationIds.remove(id); }


    public String toCsv() {
        String resIds = String.join(";", reservationIds);
        return String.join("|", passengerId, name, email, phone, address, resIds);
    }

    public static Passenger fromCsv(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 5) throw new IllegalArgumentException("Invalid passenger CSV: " + line);
        Passenger ps = new Passenger(p[0], p[1], p[2], p[3], p[4]);
        if (p.length >= 6 && !p[5].isEmpty()) {
            for (String id : p[5].split(";")) {
                if (!id.isEmpty()) ps.addReservationId(id);
            }
        }
        return ps;
    }

    @Override public String toString() {
        return String.format(
                "ID: %-12s  Name: %-25s  Email: %-30s  Phone: %-15s",
                passengerId, name, email, phone);
    }
}