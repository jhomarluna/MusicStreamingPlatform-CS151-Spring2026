package com.musicstream.model;

import com.musicstream.exception.InstanceLimitException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a musical artist on the platform.
 * An artist can have multiple songs and albums.
 */
public class Artist {

    private static int instanceCount = 0;

    private String artistId;
    private String name;
    private String genre;
    private String bio;
    private int monthlyListeners;
    private boolean isVerified;
    private List<String> songIds;
    private List<String> albumIds;

    public Artist(String artistId, String name, String genre, String bio) throws InstanceLimitException {
        if (instanceCount >= ModelLimits.MAXIMUM_INSTANCES) {
            throw new InstanceLimitException("Cannot add more artists. Maximum limit of "
                    + ModelLimits.MAXIMUM_INSTANCES + " reached.");
        }
        this.artistId = artistId;
        this.name = name;
        this.genre = genre;
        this.bio = bio;
        this.monthlyListeners = 0;
        this.isVerified = false;
        this.songIds = new ArrayList<>();
        this.albumIds = new ArrayList<>();
        instanceCount++;
    }

    // ── Business methods ──────────────────────────────────────────────────────

    /**
     * Adds a song to this artist's discography.
     */
    public void addSong(Song song) {
        if (song == null) {
            throw new IllegalArgumentException("Song cannot be null.");
        }
        if (!songIds.contains(song.getSongId())) {
            songIds.add(song.getSongId());
            System.out.println("Added \"" + song.getTitle() + "\" to " + name + "'s discography.");
        } else {
            System.out.println("\"" + song.getTitle() + "\" is already in " + name + "'s discography.");
        }
    }

    /**
     * Removes a song from this artist's discography.
     */
    public boolean removeSong(Song song) {
        if (song == null) return false;
        boolean removed = songIds.remove(song.getSongId());
        if (removed) System.out.println("Removed \"" + song.getTitle() + "\" from " + name + "'s discography.");
        return removed;
    }

    /**
     * Adds an album to this artist's discography.
     */
    public void addAlbum(Album album) {
        if (album == null) {
            throw new IllegalArgumentException("Album cannot be null.");
        }
        if (!albumIds.contains(album.getAlbumId())) {
            albumIds.add(album.getAlbumId());
            System.out.println("Added album \"" + album.getTitle() + "\" to " + name + "'s profile.");
        }
    }

    /**
     * Marks the artist as verified (e.g. identity confirmed by platform).
     */
    public void verify() {
        this.isVerified = true;
        System.out.println("✔ Artist \"" + name + "\" is now verified.");
    }

    /**
     * Updates the monthly listener count.
     */
    public void updateMonthlyListeners(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Monthly listener count cannot be negative.");
        }
        this.monthlyListeners = count;
    }

    /**
     * Returns total number of songs in discography.
     */
    public int getSongCount() { return songIds.size(); }

    // ── Static ────────────────────────────────────────────────────────────────

    public static int getInstanceCount() { return instanceCount; }

    public static void decrementInstanceCount() { instanceCount--; }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getArtistId() { return artistId; }

    public String getName() { return name; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Artist name cannot be empty.");
        this.name = name.trim();
    }

    public String getGenre() { return genre; }

    public void setGenre(String genre) { this.genre = genre; }

    public String getBio() { return bio; }

    public void setBio(String bio) { this.bio = bio; }

    public int getMonthlyListeners() { return monthlyListeners; }

    public boolean isVerified() { return isVerified; }

    public List<String> getSongIds() { return new ArrayList<>(songIds); }

    public List<String> getAlbumIds() { return new ArrayList<>(albumIds); }

    @Override
    public String toString() {
        return String.format("Artist[id=%s, name=\"%s\", genre=%s, verified=%s, listeners=%d, songs=%d]",
                artistId, name, genre, isVerified, monthlyListeners, songIds.size());
    }
}
