package com.musicstream.model;

import com.musicstream.exception.InstanceLimitException;
import com.musicstream.interface_.Streamable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A premium-tier user with unlimited streaming, offline downloads,
 * and access to explicit content.
 */
public class PremiumUser extends User {

    private LocalDate subscriptionStart;
    private LocalDate subscriptionEnd;
    private double monthlyFee;
    private List<Playlist> playlists;
    private List<Song> downloadedSongs;
    private List<String> listeningHistory; // song titles

    public PremiumUser(String userId, String username, String email, String passwordHash,
                       double monthlyFee) throws InstanceLimitException {
        super(userId, username, email, passwordHash);
        if (User.getInstanceCount() >= ModelLimits.MAXIMUM_INSTANCES) {
            throw new InstanceLimitException("Cannot register more users. Maximum of "
                    + ModelLimits.MAXIMUM_INSTANCES + " reached.");
        }
        this.monthlyFee = monthlyFee;
        this.subscriptionStart = LocalDate.now();
        this.subscriptionEnd = LocalDate.now().plusMonths(1);
        this.playlists = new ArrayList<>();
        this.downloadedSongs = new ArrayList<>();
        this.listeningHistory = new ArrayList<>();
        User.incrementInstanceCount();
    }

    // ── Abstract overrides ────────────────────────────────────────────────────

    @Override
    public String getSubscriptionType() { return "Premium"; }

    @Override
    public int getMonthlyHourLimit() { return -1; } // unlimited

    @Override
    public void streamContent(Streamable content) {
        if (!isSubscriptionActive()) {
            System.out.println("❌ Your Premium subscription has expired. Please renew.");
            return;
        }
        content.play();
        listeningHistory.add(content.getTitle());
    }

    // ── Business methods ──────────────────────────────────────────────────────

    /**
     * Downloads a song for offline listening.
     */
    public void downloadSong(Song song) {
        if (!isSubscriptionActive()) {
            System.out.println("❌ Subscription expired. Cannot download.");
            return;
        }
        for (Song s : downloadedSongs) {
            if (s.getSongId().equals(song.getSongId())) {
                System.out.println("\"" + song.getTitle() + "\" is already downloaded.");
                return;
            }
        }
        downloadedSongs.add(song);
        System.out.println("⬇ Downloaded \"" + song.getTitle() + "\" for offline listening.");
    }

    /**
     * Removes a downloaded song from offline storage.
     */
    public boolean removeDownload(Song song) {
        boolean removed = downloadedSongs.removeIf(s -> s.getSongId().equals(song.getSongId()));
        if (removed) System.out.println("Removed \"" + song.getTitle() + "\" from downloads.");
        return removed;
    }

    /**
     * Creates a new playlist for this user.
     */
    public Playlist createPlaylist(String playlistId, String playlistName, String description, boolean isPublic)
            throws InstanceLimitException {
        Playlist p = new Playlist(playlistId, playlistName, getUserId(), description, isPublic);
        playlists.add(p);
        System.out.println("✔ Playlist \"" + playlistName + "\" created.");
        return p;
    }

    /**
     * Renews the subscription for one additional month.
     */
    public void renewSubscription() {
        subscriptionEnd = subscriptionEnd.plusMonths(1);
        System.out.println("✔ Subscription renewed. New end date: " + subscriptionEnd);
    }

    /**
     * Checks whether the subscription is currently active.
     */
    public boolean isSubscriptionActive() {
        return LocalDate.now().isBefore(subscriptionEnd) || LocalDate.now().isEqual(subscriptionEnd);
    }

    /**
     * Shows the last N songs in listening history.
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

    /**
     * Displays downloaded songs.
     */
    public void viewDownloads() {
        if (downloadedSongs.isEmpty()) {
            System.out.println(getUsername() + " has no downloaded songs.");
            return;
        }
        System.out.println("\n── Downloads for " + getUsername() + " ──");
        downloadedSongs.forEach(s -> System.out.println("  " + s.getTitle() + " – " + s.getFormattedDuration()));
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

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public LocalDate getSubscriptionStart() { return subscriptionStart; }

    public LocalDate getSubscriptionEnd() { return subscriptionEnd; }

    public double getMonthlyFee() { return monthlyFee; }

    public void setMonthlyFee(double fee) {
        if (fee < 0) throw new IllegalArgumentException("Monthly fee cannot be negative.");
        this.monthlyFee = fee;
    }

    public List<Playlist> getPlaylists() { return new ArrayList<>(playlists); }

    public List<Song> getDownloadedSongs() { return new ArrayList<>(downloadedSongs); }

    @Override
    public String toString() {
        return String.format("PremiumUser[id=%s, username=%s, fee=$%.2f, active=%s, downloads=%d, playlists=%d]",
                getUserId(), getUsername(), monthlyFee, isSubscriptionActive(),
                downloadedSongs.size(), playlists.size());
    }
}
