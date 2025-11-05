package com.example.payroll.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.Properties;

public class CredentialsStore {
    private final File file;
    private final Properties users = new Properties();

    public CredentialsStore(File file) {
        this.file = Objects.requireNonNull(file);
        load();
    }

    public synchronized boolean authenticate(String username, String password) {
        if (username == null || password == null) return false;
        String stored = users.getProperty(username.toLowerCase());
        if (stored == null) return false;
        return stored.equals(hash(password));
    }

    public synchronized boolean userExists(String username) {
        return users.containsKey(username.toLowerCase());
    }

    public synchronized void addUser(String username, String password) throws Exception {
        String key = username.toLowerCase();
        if (users.containsKey(key)) throw new IllegalArgumentException("User already exists");
        users.setProperty(key, hash(password));
        save();
    }

    private void load() {
        if (!file.exists()) return;
        try (FileInputStream fis = new FileInputStream(file)) {
            users.load(fis);
        } catch (Exception ignored) {}
    }

    private void save() throws Exception {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            users.store(fos, "User credentials (username -> sha256(password))");
        }
    }

    private static String hash(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


