package com.airline.service;

import com.airline.exception.AirlineException;
import com.airline.model.User;
import com.airline.storage.DataStore;
import com.airline.util.AppUtil;

public class AuthService {

    private final DataStore store;
    private User currentUser;

    public AuthService(DataStore store) { this.store = store; }

    public User login(String username, String password) {
        User user = store.getUser(username);
        if (user == null) throw new AirlineException("Invalid username or password.");
        if (!user.getPasswordHash().equals(AppUtil.hashPassword(password)))
            throw new AirlineException("Invalid username or password.");
        this.currentUser = user;
        return user;
    }

    public void logout() { this.currentUser = null; }

    public User getCurrentUser() { return currentUser; }

    public boolean isLoggedIn()  { return currentUser != null; }
    public boolean isAdmin()     { return isLoggedIn() && currentUser.isAdmin(); }
    public boolean isCustomer()  { return isLoggedIn() && currentUser.isCustomer(); }


    public User registerCustomer(String username, String password, String linkedPassengerId) {
        if (store.getUser(username) != null)
            throw new AirlineException("Username already taken: " + username);
        if (password.length() < 6)
            throw new AirlineException("Password must be at least 6 characters.");
        User u = new User(username, AppUtil.hashPassword(password), User.Role.CUSTOMER);
        u.setLinkedPassengerId(linkedPassengerId);
        store.addUser(u);
        return u;
    }


    public void changePassword(String username, String newPassword) {
        User u = store.getUser(username);
        if (u == null) throw new AirlineException("User not found: " + username);
        if (newPassword.length() < 6) throw new AirlineException("Password too short (min 6 chars).");
        u.setPasswordHash(AppUtil.hashPassword(newPassword));
        store.updateUser(u);
    }


    public void changeOwnPassword(String currentPwd, String newPwd) {
        if (!currentUser.getPasswordHash().equals(AppUtil.hashPassword(currentPwd)))
            throw new AirlineException("Current password is incorrect.");
        changePassword(currentUser.getUsername(), newPwd);
    }
}