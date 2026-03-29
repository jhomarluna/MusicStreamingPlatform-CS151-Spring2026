package com.musicstream.model;

import com.musicstream.exception.InstanceLimitException;
import com.musicstream.interface_.Streamable;

/**
 * Represents a single song on the platform.
 * Implements Streamable so it can be played, paused, and stopped.
 */
public class Song implements Streamable {

    private static int instanceCount = 0;

    private String songId;
    private String title;
    private String artistId;
    private String albumId;
    private String genre;
    private double duration; // seconds
    private int playCount;
    private boolean explicit;
    private int releaseYear;
    private boolean isPlaying;
    private boolean isPaused;

    public Song(String songId, String title, String artistId, String genre,
                double duration, boolean explicit, int releaseYear) throws InstanceLimitException {
        if (instanceCount >= ModelLimits.MAXIMUM_INSTANCES) {
            throw new InstanceLimitException("Cannot add more songs. Maximum limit of "
                    + ModelLimits.MAXIMUM_INSTANCES + " reached.");
        }
        this.songId = songId;
        this.title = title;
        this.artistId = artistId;
        this.genre = genre;
        this.duration = duration;
        this.explicit = explicit;
        this.releaseYear = releaseYear;
        this.playCount = 0;
        this.isPlaying = false;
        this.isPaused = false;
        instanceCount++;
    }

    // ── Streamable ────────────────────────────────────────────────────────────

    @Override
    public void play() {
        isPlaying = true;
        isPaused = false;
        playCount++;
        System.out.println("▶ Now playing: \"" + title + "\"");
    }

    @Override
    public void pause() {
        if (!isPlaying) {
            System.out.println("⚠ \"" + title + "\" is not currently playing.");
            return;
        }
        isPaused = true;
        isPlaying = false;
        System.out.println("⏸ Paused: \"" + title + "\"");
    }

    @Override
    public void stop() {
        isPlaying = false;
        isPaused = false;
        System.out.println("⏹ Stopped: \"" + title + "\"");
    }

    @Override
    public double getDuration() { return duration; }

    @Override
    public String getTitle() { return title; }

    @Override
    public boolean isExplicit() { return explicit; }

    // ── Business methods ──────────────────────────────────────────────────────

    /**
     * Increments play count manually (e.g. when added to a listening history).
     */
    public void incrementPlayCount() {
        this.playCount++;
    }

    /**
     * Returns a formatted duration string mm:ss.
     */
    public String getFormattedDuration() {
        int mins = (int) (duration / 60);
        int secs = (int) (duration % 60);
        return String.format("%d:%02d", mins, secs);
    }

    /**
     * Checks whether this song belongs to a given genre (case-insensitive).
     */
    public boolean isGenre(String genreQuery) {
        return this.genre.equalsIgnoreCase(genreQuery);
    }

    /**
     * Updates the album this song belongs to.
     */
    public void assignToAlbum(String albumId) {
        if (albumId == null || albumId.trim().isEmpty()) {
            throw new IllegalArgumentException("Album ID cannot be empty.");
        }
        this.albumId = albumId;
        System.out.println("\"" + title + "\" assigned to album " + albumId);
    }

    // ── Static ────────────────────────────────────────────────────────────────

    public static int getInstanceCount() { return instanceCount; }

    public static void decrementInstanceCount() { instanceCount--; }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getSongId() { return songId; }

    public String getArtistId() { return artistId; }

    public void setArtistId(String artistId) { this.artistId = artistId; }

    public String getAlbumId() { return albumId; }

    public String getGenre() { return genre; }

    public void setGenre(String genre) { this.genre = genre; }

    public int getPlayCount() { return playCount; }

    public int getReleaseYear() { return releaseYear; }

    public void setReleaseYear(int releaseYear) {
        if (releaseYear < 1900 || releaseYear > 2100) {
            throw new IllegalArgumentException("Invalid release year: " + releaseYear);
        }
        this.releaseYear = releaseYear;
    }

    public boolean isPlaying() { return isPlaying; }

    public boolean isPaused() { return isPaused; }

    @Override
    public String toString() {
        return String.format("Song[id=%s, title=\"%s\", artist=%s, genre=%s, duration=%s, plays=%d, explicit=%s]",
                songId, title, artistId, genre, getFormattedDuration(), playCount, explicit);
    }
}
