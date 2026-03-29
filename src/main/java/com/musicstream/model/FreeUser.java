package com.musicstream.model;

import com.musicstream.exception.InstanceLimitException;
import com.musicstream.exception.StreamingPermissionException;
import com.musicstream.interface_.Streamable;
import java.util.ArrayList;
import java.util.List;

/**
 * A free-tier user of the platform.
 * Limited to 30 hours/month of listening, no offline downloads,
 * and cannot stream explicit content.
 */
public class FreeUser extends User {

    public static final int MONTHLY_HOUR_LIMIT = 30;
    private double hoursListenedThisMonth;
    private List<Playlist> playlists;
    private List<String> listeningHistory; // song IDs

    public FreeUser(String userId, String username, String email, String passwordHash)
            throws InstanceLimitException {
        super(userId, username, email, passwordHash);
        if (User.getInstanceCount() >= ModelLimits.MAXIMUM_INSTANCES) {
            throw new InstanceLimitException("Cannot register more users. Maximum of "
                    + ModelLimits.MAXIMUM_INSTANCES + " reached.");
        }
        this.hoursListenedThisMonth = 0;
        this.playlists = new ArrayList<>();
        this.listeningHistory = new ArrayList<>();
        User.incrementInstanceCount();
    }

    // ── Abstract overrides ────────────────────────────────────────────────────

    @Override
    public String getSubscriptionType() { return "Free"; }

    @Override
    public int getMonthlyHourLimit() { return MONTHLY_HOUR_LIMIT; }

    @Override
    public void streamContent(Streamable content) {
        try {
            if (content.isExplicit()) {
                throw new StreamingPermissionException(
                        "Free users cannot stream explicit content. Upgrade to Premium.");
            }
            double hoursToAdd = content.getDuration() / 3600.0;
            if (hoursListenedThisMonth + hoursToAdd > MONTHLY_HOUR_LIMIT) {
                throw new StreamingPermissionException(
                        "Monthly listening limit reached (" + MONTHLY_HOUR_LIMIT + " hrs). Upgrade to Premium.");
            }
            content.play();
            hoursListenedThisMonth += hoursToAdd;
            listeningHistory.add(content.getTitle());
        } catch (StreamingPermissionException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ── Business methods ──────────────────────────────────────────────────────

    /**
     * Creates a new playlist for this user and registers it.
     */
    public Playlist createPlaylist(String playlistId, String playlistName, String description, boolean isPublic)
            throws InstanceLimitException {
        Playlist p = new Playlist(playlistId, playlistName, getUserId(), description, isPublic);
        playlists.add(p);
        System.out.println("✔ Playlist \"" + playlistName + "\" created.");
        return p;
    }

    /**
     * Deletes one of the user's playlists.
     */
    public boolean deletePlaylist(String playlistId) {
        boolean removed = playlists.removeIf(p -> p.getPlaylistId().equals(playlistId));
        if (removed) {
            Playlist.decrementInstanceCount();
            System.out.println("Playlist " + playlistId + " deleted.");
        } else {
            System.out.println("Playlist not found.");
        }
        return removed;
    }

    /**
     * Displays all playlists owned by this user (with track listings).
     */
    public void viewPlaylists() {
        if (playlists.isEmpty()) {
            System.out.println(getUsername() + " has no playlists.");
            return;
        }
        System.out.println("\n── Playlists for " + getUsername() + " ──");
        for (Playlist p : playlists) {
            System.out.println("  " + p);
            p.displaySongs();
        }
    }

    /**
     * Resets monthly listening hours (called at the start of a new month).
     */
    public void resetMonthlyHours() {
        hoursListenedThisMonth = 0;
        System.out.println("Monthly hours reset for " + getUsername() + ".");
    }

    /**
     * Shows the last N songs listened to.
     */
    public void viewRecentHistory(int count) {
        if (listeningHistory.isEmpty()) {
            System.out.println("No listening history yet.");
            return;
        }
        int start = Math.max(0, listeningHistory.size() - count);
        System.out.println("\n── Recent History ──");
        for (int i = listeningHistory.size() - 1; i >= start; i--) {
            System.out.println("  " + listeningHistory.get(i));
        }
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public double getHoursListenedThisMonth() { return hoursListenedThisMonth; }

    public List<Playlist> getPlaylists() { return new ArrayList<>(playlists); }

    public List<String> getListeningHistory() { return new ArrayList<>(listeningHistory); }

    @Override
    public String toString() {
        return String.format("FreeUser[id=%s, username=%s, hoursUsed=%.1f/%d, playlists=%d]",
                getUserId(), getUsername(), hoursListenedThisMonth, MONTHLY_HOUR_LIMIT, playlists.size());
    }
}
