package com.musicstream.model;

import com.musicstream.exception.InstanceLimitException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user-created playlist containing an ordered list of songs.
 */
public class Playlist {

    private static int instanceCount = 0;

    private String playlistId;
    private String name;
    private String ownerId;
    private String description;
    private boolean isPublic;
    private LocalDate createdDate;
    private List<Song> songs;

    public Playlist(String playlistId, String name, String ownerId, String description, boolean isPublic)
            throws InstanceLimitException {
        if (instanceCount >= ModelLimits.MAXIMUM_INSTANCES) {
            throw new InstanceLimitException("Cannot create more playlists. Maximum limit of "
                    + ModelLimits.MAXIMUM_INSTANCES + " reached.");
        }
        this.playlistId = playlistId;
        this.name = name;
        this.ownerId = ownerId;
        this.description = description;
        this.isPublic = isPublic;
        this.createdDate = LocalDate.now();
        this.songs = new ArrayList<>();
        instanceCount++;
    }

    // ── Business methods ──────────────────────────────────────────────────────

    /**
     * Adds a song to the playlist. Prevents duplicates.
     */
    public void addSong(Song song) {
        if (song == null) throw new IllegalArgumentException("Song cannot be null.");
        for (Song s : songs) {
            if (s.getSongId().equals(song.getSongId())) {
                System.out.println("\"" + song.getTitle() + "\" is already in playlist \"" + name + "\".");
                return;
            }
        }
        songs.add(song);
        System.out.println("Added \"" + song.getTitle() + "\" to playlist \"" + name + "\".");
    }

    /**
     * Removes a song from the playlist by song object.
     */
    public boolean removeSong(Song song) {
        boolean removed = songs.removeIf(s -> s.getSongId().equals(song.getSongId()));
        if (removed) System.out.println("Removed \"" + song.getTitle() + "\" from playlist \"" + name + "\".");
        else System.out.println("\"" + song.getTitle() + "\" was not found in playlist \"" + name + "\".");
        return removed;
    }

    /**
     * Moves a song to a different position in the playlist (1-indexed).
     */
    public void reorderSong(Song song, int newPosition) {
        if (newPosition < 1 || newPosition > songs.size()) {
            throw new IllegalArgumentException("Position " + newPosition + " is out of range (1-" + songs.size() + ").");
        }
        int idx = -1;
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getSongId().equals(song.getSongId())) {
                idx = i;
                break;
            }
        }
        if (idx == -1) {
            System.out.println("Song not found in playlist.");
            return;
        }
        Song removed = songs.remove(idx);
        songs.add(newPosition - 1, removed);
        System.out.println("Moved \"" + song.getTitle() + "\" to position " + newPosition + ".");
    }

    /**
     * Returns total duration of all songs in the playlist.
     */
    public double getTotalDuration() {
        return songs.stream().mapToDouble(Song::getDuration).sum();
    }

    /**
     * Displays all songs with their position, title, and duration.
     */
    public void displaySongs() {
        if (songs.isEmpty()) {
            System.out.println("Playlist \"" + name + "\" is empty.");
            return;
        }
        System.out.println("\n── Playlist: " + name + " ──────────────────────");
        for (int i = 0; i < songs.size(); i++) {
            Song s = songs.get(i);
            System.out.printf("  %2d. %-35s %s%n", i + 1, s.getTitle(), s.getFormattedDuration());
        }
        int totalSecs = (int) getTotalDuration();
        System.out.printf("  Total: %d songs, %d:%02d%n", songs.size(), totalSecs / 60, totalSecs % 60);
    }

    /**
     * Shuffles the playlist order randomly.
     */
    public void shuffle() {
        java.util.Collections.shuffle(songs);
        System.out.println("🔀 Playlist \"" + name + "\" has been shuffled.");
    }

    // ── Static ────────────────────────────────────────────────────────────────

    public static int getInstanceCount() { return instanceCount; }

    public static void decrementInstanceCount() { instanceCount--; }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getPlaylistId() { return playlistId; }

    public String getName() { return name; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Playlist name cannot be empty.");
        this.name = name.trim();
    }

    public String getOwnerId() { return ownerId; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public boolean isPublic() { return isPublic; }

    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public LocalDate getCreatedDate() { return createdDate; }

    public List<Song> getSongs() { return new ArrayList<>(songs); }

    public int getSongCount() { return songs.size(); }

    @Override
    public String toString() {
        return String.format("Playlist[id=%s, name=\"%s\", owner=%s, songs=%d, public=%s]",
                playlistId, name, ownerId, songs.size(), isPublic);
    }
}
