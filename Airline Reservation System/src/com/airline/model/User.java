package com.airline.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role { ADMIN, CUSTOMER }

    private String username;
    private String passwordHash;
    private Role role;
    private String linkedPassengerId;

    public User(String username, String passwordHash, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getUsername()            { return username; }
    public String getPasswordHash()        { return passwordHash; }
    public void   setPasswordHash(String h){ this.passwordHash = h; }
    public Role   getRole()                { return role; }
    public String getLinkedPassengerId()   { return linkedPassengerId; }
    public void   setLinkedPassengerId(String id) { this.linkedPassengerId = id; }

    public boolean isAdmin()    { return role == Role.ADMIN; }
    public boolean isCustomer() { return role == Role.CUSTOMER; }


    public String toCsv() {
        return String.join(",",
                username,
                passwordHash,
                role.name(),
                linkedPassengerId == null ? "" : linkedPassengerId);
    }

    public static User fromCsv(String line) {
        String[] p = line.split(",", -1);
        if (p.length < 3) throw new IllegalArgumentException("Invalid user CSV: " + line);
        User u = new User(p[0], p[1], Role.valueOf(p[2]));
        if (p.length >= 4 && !p[3].isEmpty()) u.setLinkedPassengerId(p[3]);
        return u;
    }

    @Override public String toString() {
        return String.format("User[username=%s, role=%s]", username, role);
    }
}
