package com.musicstream.model;

import com.musicstream.exception.InstanceLimitException;
import com.musicstream.exception.NotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The central Platform registry.
 * Manages all users, artists, albums, songs, and playlists.
 * Acts as the top-level service layer the UI talks to.
 */
public class Platform {

    private static final String PLATFORM_NAME = "StreamFlow";

    private List<User> users;
    private List<Artist> artists;
    private List<Album> albums;
    private List<Song> songs;
    private List<Playlist> publicPlaylists;

    // ID counters
    private int userCounter = 1;
    private int artistCounter = 1;
    private int albumCounter = 1;
    private int songCounter = 1;
    private int playlistCounter = 1;

    public Platform() {
        users = new ArrayList<>();
        artists = new ArrayList<>();
        albums = new ArrayList<>();
        songs = new ArrayList<>();
        publicPlaylists = new ArrayList<>();
    }

    // ── User management ───────────────────────────────────────────────────────

    public FreeUser registerFreeUser(String username, String email, String password)
            throws InstanceLimitException {
        validateUniqueEmail(email);
        String id = "U" + String.format("%03d", userCounter++);
        FreeUser u = new FreeUser(id, username, email, password);
        users.add(u);
        System.out.println("✔ Registered Free user: " + username + " (" + id + ")");
        return u;
    }

    public PremiumUser registerPremiumUser(String username, String email, String password, double fee)
            throws InstanceLimitException {
        validateUniqueEmail(email);
        String id = "U" + String.format("%03d", userCounter++);
        PremiumUser u = new PremiumUser(id, username, email, password, fee);
        users.add(u);
        System.out.println("✔ Registered Premium user: " + username + " (" + id + ")");
        return u;
    }

    public boolean removeUser(String userId) throws NotFoundException {
        User u = findUserById(userId);
        users.remove(u);
        User.decrementInstanceCount();
        System.out.println("User " + userId + " removed.");
        return true;
    }

    public User findUserById(String userId) throws NotFoundException {
        return users.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    public void listUsers() {
        if (users.isEmpty()) { System.out.println("No users registered."); return; }
        System.out.println("\n── Users (" + users.size() + ") ──────────────────────────");
        users.forEach(u -> System.out.println("  " + u));
    }

    // ── Artist management ─────────────────────────────────────────────────────

    public Artist addArtist(String name, String genre, String bio) throws InstanceLimitException {
        String id = "A" + String.format("%03d", artistCounter++);
        Artist a = new Artist(id, name, genre, bio);
        artists.add(a);
        System.out.println("✔ Added artist: " + name + " (" + id + ")");
        return a;
    }

    public boolean removeArtist(String artistId) throws NotFoundException {
        Artist a = findArtistById(artistId);
        artists.remove(a);
        Artist.decrementInstanceCount();
        System.out.println("Artist " + artistId + " removed.");
        return true;
    }

    public Artist findArtistById(String artistId) throws NotFoundException {
        return artists.stream()
                .filter(a -> a.getArtistId().equals(artistId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Artist not found: " + artistId));
    }

    public void listArtists() {
        if (artists.isEmpty()) { System.out.println("No artists found."); return; }
        System.out.println("\n── Artists (" + artists.size() + ") ──────────────────────────");
        artists.forEach(a -> System.out.println("  " + a));
    }

    // ── Song management ───────────────────────────────────────────────────────

    public Song addSong(String title, String artistId, String genre, double duration,
                        boolean explicit, int releaseYear) throws InstanceLimitException, NotFoundException {
        findArtistById(artistId); // validates artist exists
        String id = "S" + String.format("%03d", songCounter++);
        Song s = new Song(id, title, artistId, genre, duration, explicit, releaseYear);
        songs.add(s);
        // auto-link song to artist
        Artist artist = findArtistById(artistId);
        artist.addSong(s);
        System.out.println("✔ Added song: \"" + title + "\" (" + id + ")");
        return s;
    }

    public boolean removeSong(String songId) throws NotFoundException {
        Song s = findSongById(songId);
        Artist artist = findArtistById(s.getArtistId());
        artist.removeSong(s);
        for (Album al : albums) {
            al.removeSong(s);
        }
        songs.remove(s);
        Song.decrementInstanceCount();
        System.out.println("Song " + songId + " removed.");
        return true;
    }

    public Song findSongById(String songId) throws NotFoundException {
        return songs.stream()
                .filter(s -> s.getSongId().equals(songId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Song not found: " + songId));
    }

    public void listSongs() {
        if (songs.isEmpty()) { System.out.println("No songs found."); return; }
        System.out.println("\n── Songs (" + songs.size() + ") ──────────────────────────");
        songs.forEach(s -> System.out.println("  " + s));
    }

    /**
     * Returns songs filtered by genre.
     */
    public List<Song> searchByGenre(String genre) {
        List<Song> result = songs.stream()
                .filter(s -> s.isGenre(genre))
                .collect(Collectors.toList());
        System.out.println("Found " + result.size() + " song(s) in genre \"" + genre + "\".");
        return result;
    }

    /**
     * Returns the top N most-played songs across the platform.
     */
    public List<Song> getTopSongs(int n) {
        return songs.stream()
                .sorted(Comparator.comparingInt(Song::getPlayCount).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    // ── Album management ──────────────────────────────────────────────────────

    public Album addAlbum(String title, String artistId, int releaseYear, String genre)
            throws InstanceLimitException, NotFoundException {
        findArtistById(artistId);
        String id = "AL" + String.format("%03d", albumCounter++);
        Album al = new Album(id, title, artistId, releaseYear, genre);
        albums.add(al);
        Artist artist = findArtistById(artistId);
        artist.addAlbum(al);
        System.out.println("✔ Added album: \"" + title + "\" (" + id + ")");
        return al;
    }

    public Album findAlbumById(String albumId) throws NotFoundException {
        return albums.stream()
                .filter(a -> a.getAlbumId().equals(albumId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Album not found: " + albumId));
    }

    public void listAlbums() {
        if (albums.isEmpty()) { System.out.println("No albums found."); return; }
        System.out.println("\n── Albums (" + albums.size() + ") ──────────────────────────");
        albums.forEach(a -> System.out.println("  " + a));
    }

    // ── Public Playlist management ────────────────────────────────────────────

    public void registerPublicPlaylist(Playlist p) {
        if (p.isPublic() && !publicPlaylists.contains(p)) {
            publicPlaylists.add(p);
        }
    }

    /** Removes a playlist from the public directory if it was listed. */
    public void removePublicPlaylistById(String playlistId) {
        publicPlaylists.removeIf(p -> p.getPlaylistId().equals(playlistId));
    }

    public void listPublicPlaylists() {
        if (publicPlaylists.isEmpty()) { System.out.println("No public playlists."); return; }
        System.out.println("\n── Public Playlists (" + publicPlaylists.size() + ") ──");
        publicPlaylists.forEach(p -> System.out.println("  " + p));
    }

    // ── Platform stats ────────────────────────────────────────────────────────

    public void printStats() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  " + PLATFORM_NAME + " – Platform Stats");
        System.out.println("═══════════════════════════════════════");
        System.out.println("  Users    : " + users.size());
        System.out.println("  Artists  : " + artists.size());
        System.out.println("  Albums   : " + albums.size());
        System.out.println("  Songs    : " + songs.size());
        System.out.println("  Playlists: " + publicPlaylists.size() + " (public)");
        long premiumCount = users.stream().filter(u -> u instanceof PremiumUser).count();
        long freeCount = users.stream().filter(u -> u instanceof FreeUser).count();
        System.out.println("  Premium  : " + premiumCount + "  |  Free: " + freeCount);
        System.out.println("═══════════════════════════════════════");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void validateUniqueEmail(String email) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                throw new IllegalArgumentException("Email already registered: " + email);
            }
        }
    }

    public List<User> getUsers() { return new ArrayList<>(users); }
    public List<Artist> getArtists() { return new ArrayList<>(artists); }
    public List<Song> getSongs() { return new ArrayList<>(songs); }
    public List<Album> getAlbums() { return new ArrayList<>(albums); }

    public String generatePlaylistId() {
        return "P" + String.format("%03d", playlistCounter++);
    }
}
