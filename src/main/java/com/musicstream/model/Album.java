package com.musicstream.model;

import com.musicstream.exception.InstanceLimitException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a music album that groups multiple songs by an artist.
 */
public class Album {

    private static int instanceCount = 0;

    private String albumId;
    private String title;
    private String artistId;
    private int releaseYear;
    private String genre;
    private List<Song> songs;
    private boolean isPublished;

    public Album(String albumId, String title, String artistId, int releaseYear, String genre)
            throws InstanceLimitException {
        if (instanceCount >= ModelLimits.MAXIMUM_INSTANCES) {
            throw new InstanceLimitException("Cannot add more albums. Maximum limit of "
                    + ModelLimits.MAXIMUM_INSTANCES + " reached.");
        }
        this.albumId = albumId;
        this.title = title;
        this.artistId = artistId;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.songs = new ArrayList<>();
        this.isPublished = false;
        instanceCount++;
    }

    // ── Business methods ──────────────────────────────────────────────────────

    /**
     * Adds a song to this album and assigns the album ID back to the song.
     */
    public void addSong(Song song) {
        if (song == null) throw new IllegalArgumentException("Song cannot be null.");
        if (songs.contains(song)) {
            System.out.println("\"" + song.getTitle() + "\" is already in album \"" + title + "\".");
            return;
        }
        songs.add(song);
        song.assignToAlbum(albumId);
        System.out.println("Added \"" + song.getTitle() + "\" to album \"" + title + "\".");
    }

    /**
     * Removes a song from this album.
     */
    public boolean removeSong(Song song) {
        boolean removed = songs.remove(song);
        if (removed) System.out.println("Removed \"" + song.getTitle() + "\" from album \"" + title + "\".");
        return removed;
    }

    /**
     * Publishes the album, making it publicly available.
     */
    public void publish() {
        if (songs.isEmpty()) {
            System.out.println("⚠ Cannot publish an empty album.");
            return;
        }
        this.isPublished = true;
        System.out.println("✔ Album \"" + title + "\" is now published.");
    }

    /**
     * Returns total duration of all songs in the album (in seconds).
     */
    public double getTotalDuration() {
        return songs.stream().mapToDouble(Song::getDuration).sum();
    }

    /**
     * Returns the formatted total duration as h:mm:ss.
     */
    public String getFormattedTotalDuration() {
        int total = (int) getTotalDuration();
        int hours = total / 3600;
        int mins = (total % 3600) / 60;
        int secs = total % 60;
        return String.format("%d:%02d:%02d", hours, mins, secs);
    }

    /**
     * Returns a list of all songs in this album.
     */
    public List<Song> listSongs() {
        return new ArrayList<>(songs);
    }

    // ── Static ────────────────────────────────────────────────────────────────

    public static int getInstanceCount() { return instanceCount; }

    public static void decrementInstanceCount() { instanceCount--; }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getAlbumId() { return albumId; }

    public String getTitle() { return title; }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Album title cannot be empty.");
        this.title = title.trim();
    }

    public String getArtistId() { return artistId; }

    public int getReleaseYear() { return releaseYear; }

    public void setReleaseYear(int year) {
        if (year < 1900 || year > 2100) throw new IllegalArgumentException("Invalid release year.");
        this.releaseYear = year;
    }

    public String getGenre() { return genre; }

    public void setGenre(String genre) { this.genre = genre; }

    public boolean isPublished() { return isPublished; }

    public int getSongCount() { return songs.size(); }

    @Override
    public String toString() {
        return String.format("Album[id=%s, title=\"%s\", artist=%s, year=%d, genre=%s, songs=%d, published=%s]",
                albumId, title, artistId, releaseYear, genre, songs.size(), isPublished);
    }
}
