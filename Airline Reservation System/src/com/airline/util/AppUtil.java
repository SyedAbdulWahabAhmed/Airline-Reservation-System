package com.airline.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public final class AppUtil {

    private AppUtil() {}

    private static final AtomicInteger SEQ = new AtomicInteger(1);
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public static String newPassengerId() {
        return String.format("PAX%06d", SEQ.getAndIncrement());
    }
    public static String newReservationId() {
        return String.format("RES%08d", System.currentTimeMillis() % 100_000_000);
    }


    public static void seedSequence(int max) {
        if (max >= SEQ.get()) SEQ.set(max + 1);
    }


    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }


    public static String readLine(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    public static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) {
                System.out.println("  ✗ Please enter a valid integer.");
            }
        }
    }

    public static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try { return Double.parseDouble(s); }
            catch (NumberFormatException e) {
                System.out.println("  ✗ Please enter a valid number.");
            }
        }
    }

    public static LocalDateTime readDateTime(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt + " (yyyy-MM-dd HH:mm): ");
            String s = sc.nextLine().trim();
            try { return LocalDateTime.parse(s, DT_FMT); }
            catch (DateTimeParseException e) {
                System.out.println("  ✗ Invalid format. Use yyyy-MM-dd HH:mm");
            }
        }
    }

    public static boolean confirm(Scanner sc, String prompt) {
        System.out.print(prompt + " (y/n): ");
        return sc.nextLine().trim().equalsIgnoreCase("y");
    }


    private static final Pattern EMAIL_RE = Pattern.compile(
            "^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$");
    private static final Pattern PHONE_RE = Pattern.compile(
            "^[+0-9][0-9\\-. ]{6,14}$");

    public static boolean isValidEmail(String e) { return EMAIL_RE.matcher(e).matches(); }
    public static boolean isValidPhone(String p) { return PHONE_RE.matcher(p).matches(); }


    public static void printDivider() {
        System.out.println("─".repeat(110));
    }

    public static void printHeader(String title) {
        printDivider();
        int pad = (108 - title.length()) / 2;
        System.out.println(" ".repeat(Math.max(0, pad)) + title);
        printDivider();
    }

    public static void success(String msg) { System.out.println("  ✔  " + msg); }
    public static void error(String msg)   { System.out.println("  ✗  " + msg); }
    public static void info(String msg)    { System.out.println("  ℹ  " + msg); }
}