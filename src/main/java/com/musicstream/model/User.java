package com.musicstream.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.musicstream.interface_.Streamable;

/**
 * Abstract base class for all users of the music streaming platform.
 * Provides shared identity and account fields for FreeUser and PremiumUser.
 */
public abstract class User {

    private static int instanceCount = 0;

    private String userId;
    private String username;
    private String email;
    private String passwordHash;
    private LocalDate registrationDate;
    private boolean isActive;
    private List<User> friends = new ArrayList<>();

    public User(String userId, String username, String email, String passwordHash) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.registrationDate = LocalDate.now();
        this.isActive = true;
    }

    // ── Abstract methods ──────────────────────────────────────────────────────

    /** Returns the subscription tier label, e.g. "Free" or "Premium". */
    public abstract String getSubscriptionType();

    /** Returns the monthly listening‑hour cap for this user tier (-1 = unlimited). */
    public abstract int getMonthlyHourLimit();

    /** Attempts to stream the given song; tier‑specific validation lives here. */
    public abstract void streamContent(Streamable content);

    // ── Static instance tracking ──────────────────────────────────────────────

    public static int getInstanceCount() { return instanceCount; }

    public static void incrementInstanceCount() { instanceCount++; }

    public static void decrementInstanceCount() { instanceCount--; }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getUserId() { return userId; }

    public String getUsername() { return username; }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        this.username = username.trim();
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        this.email = email.trim();
    }

    public String getPasswordHash() { return passwordHash; }

    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /**
     * Updates the account password with basic validation.
     */
    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }
        this.passwordHash = newPassword;
    }

    public LocalDate getRegistrationDate() { return registrationDate; }

    public boolean isActive() { return isActive; }

    public void setActive(boolean active) { this.isActive = active; }

    public List<User> getFriends() { return friends; }

    public void addFriend(User friend) {
        if (friend == null || friend == this || friends.contains(friend)) {
            throw new IllegalArgumentException("Invalid friend.");
        }
        friends.add(friend);
    }

    public void removeFriend(User friend) {
        if (friend == null || !friends.contains(friend)) {
            throw new IllegalArgumentException("Friend not found.");
        }
        friends.remove(friend);
    }

    public boolean isFriend(User user) {
        return friends.contains(user);
    }

    public void displayFriends() {
        if (friends.isEmpty()) {
            System.out.println(username + " has no friends.");
        } else {
            System.out.println(username + "'s friends:");
            for (User friend : friends) {
                System.out.println("- " + friend.getUsername());
            }
        }
    }

    public void getFriendCount() {
        System.out.println(username + " has " + friends.size() + " friend(s).");
    }

    public void getFriendCurrentSong() {
        if (friends.isEmpty()) {
            System.out.println(username + " has no friends to check.");
        } else {
            System.out.println(username + "'s friends' current songs:");
            for (User friend : friends) {
                if (friend instanceof Streamable) {
                    Streamable streamableFriend = (Streamable) friend;
                    System.out.println("- " + friend.getUsername() + ": " + streamableFriend.getCurrentSong());
                } else {
                    System.out.println("- " + friend.getUsername() + ": Not streaming");
                }
            }
        }
    }

    

    @Override
    public String toString() {
        return String.format("User[id=%s, username=%s, email=%s, type=%s, active=%s]",
                userId, username, email, getSubscriptionType(), isActive);
    }
}
