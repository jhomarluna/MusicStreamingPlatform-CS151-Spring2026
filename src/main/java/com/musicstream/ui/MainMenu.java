package com.musicstream.ui;

import com.musicstream.exception.InstanceLimitException;
import com.musicstream.exception.NotFoundException;
import com.musicstream.model.*;
import com.musicstream.util.DataLoader;
import com.musicstream.util.InputHandler;

import java.util.List;

/**
 * Entry point and top-level menu for the StreamFlow Music Platform.
 * All user interaction flows through this class.
 * Type EXIT at any prompt to quit the application immediately.
 */
public class MainMenu {

    private static Platform platform = new Platform();

    public static void main(String[] args) {
        DataLoader.load(platform);
        printBanner();
        runMainLoop();
    }

    // ── Banner ────────────────────────────────────────────────────────────────

    private static void printBanner() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║        🎵  StreamFlow  🎵            ║");
        System.out.println("║   Music Streaming Platform v1.0      ║");
        System.out.println("║  (Type EXIT at any time to quit)     ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    // ── Main loop ─────────────────────────────────────────────────────────────

    private static void runMainLoop() {
        while (true) {
            printMainMenu();
            int choice = InputHandler.readInt("Enter choice: ");
            switch (choice) {
                case 1 -> userMenu();
                case 2 -> artistMenu();
                case 3 -> songMenu();
                case 4 -> albumMenu();
                case 5 -> playlistMenu();
                case 6 -> platform.printStats();
                case 7 -> showTopSongs();
                case 8 -> searchByGenrePrompt();
                case 0 -> {
                    System.out.println("Goodbye! Thanks for using StreamFlow.");
                    return;
                }
                default -> System.out.println("⚠ Invalid option. Please choose 0-8.");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n══════════════ MAIN MENU ══════════════");
        System.out.println("  1. User Management");
        System.out.println("  2. Artist Management");
        System.out.println("  3. Song Management");
        System.out.println("  4. Album Management");
        System.out.println("  5. Playlist Management");
        System.out.println("  6. Platform Stats");
        System.out.println("  7. Top Songs");
        System.out.println("  8. Search Songs by Genre");
        System.out.println("  0. Exit");
        System.out.println("═══════════════════════════════════════");
    }

    // ── User Menu ─────────────────────────────────────────────────────────────

    private static void userMenu() {
        while (true) {
            System.out.println("\n─── USER MENU ───────────────────────────");
            System.out.println("  1. List all users");
            System.out.println("  2. Register Free user");
            System.out.println("  3. Register Premium user");
            System.out.println("  4. View user details");
            System.out.println("  5. Remove user");
            System.out.println("  6. Stream a song (as user)");
            System.out.println("  7. Create playlist (as user)");
            System.out.println("  8. View user playlists");
            System.out.println("  9. Download song (Premium only)");
            System.out.println(" 10. View listening history");
            System.out.println(" 11. Change password");
            System.out.println(" 12. Delete playlist");
            System.out.println(" 13. Reset monthly listening hours (Free)");
            System.out.println(" 14. Renew Premium subscription");
            System.out.println(" 15. View offline downloads (Premium)");
            System.out.println(" 16. Remove song from downloads (Premium)");
            System.out.println("  0. Back to main menu");

            int choice = InputHandler.readInt("Choice: ");
            switch (choice) {
                case 1 -> platform.listUsers();
                case 2 -> registerFreeUser();
                case 3 -> registerPremiumUser();
                case 4 -> viewUserDetails();
                case 5 -> removeUser();
                case 6 -> streamSongAsUser();
                case 7 -> createPlaylistAsUser();
                case 8 -> viewUserPlaylists();
                case 9 -> downloadSongAsPremium();
                case 10 -> viewUserHistory();
                case 11 -> changeUserPassword();
                case 12 -> deletePlaylistAsUser();
                case 13 -> resetMonthlyHoursAsFree();
                case 14 -> renewPremiumSubscription();
                case 15 -> viewOfflineDownloads();
                case 16 -> removeDownloadAsPremium();
                case 0 -> { return; }
                default -> System.out.println("⚠ Invalid option.");
            }
        }
    }

    private static void registerFreeUser() {
        String name = InputHandler.readLine("Username: ");
        String email = InputHandler.readLine("Email: ");
        String pass = InputHandler.readLine("Password: ");
        try {
            platform.registerFreeUser(name, email, pass);
        } catch (InstanceLimitException | IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void registerPremiumUser() {
        String name = InputHandler.readLine("Username: ");
        String email = InputHandler.readLine("Email: ");
        String pass = InputHandler.readLine("Password: ");
        double fee = InputHandler.readDouble("Monthly fee ($): ");
        try {
            platform.registerPremiumUser(name, email, pass, fee);
        } catch (InstanceLimitException | IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void viewUserDetails() {
        String id = InputHandler.readLine("User ID: ");
        try {
            User u = platform.findUserById(id);
            System.out.println("\n" + u);
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void removeUser() {
        String id = InputHandler.readLine("User ID to remove: ");
        try {
            platform.removeUser(id);
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void streamSongAsUser() {
        String userId = InputHandler.readLine("User ID: ");
        String songId = InputHandler.readLine("Song ID: ");
        try {
            User u = platform.findUserById(userId);
            Song s = platform.findSongById(songId);
            u.streamContent(s);
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void createPlaylistAsUser() {
        String userId = InputHandler.readLine("User ID: ");
        try {
            User u = platform.findUserById(userId);
            String pName = InputHandler.readLine("Playlist name: ");
            String desc = InputHandler.readLine("Description: ");
            boolean isPublic = InputHandler.readBoolean("Make public?");
            String pid = platform.generatePlaylistId();
            Playlist pl = null;
            if (u instanceof FreeUser fu) {
                pl = fu.createPlaylist(pid, pName, desc, isPublic);
            } else if (u instanceof PremiumUser pu) {
                pl = pu.createPlaylist(pid, pName, desc, isPublic);
            }
            if (pl != null && isPublic) {
                platform.registerPublicPlaylist(pl);
            }
        } catch (NotFoundException | InstanceLimitException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void viewUserPlaylists() {
        String userId = InputHandler.readLine("User ID: ");
        try {
            User u = platform.findUserById(userId);
            if (u instanceof FreeUser fu) fu.viewPlaylists();
            else if (u instanceof PremiumUser pu) pu.viewPlaylists();
            else System.out.println("Unknown user type.");
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void changeUserPassword() {
        String userId = InputHandler.readLine("User ID: ");
        String newPass = InputHandler.readLine("New password (min 4 characters): ");
        try {
            platform.findUserById(userId).changePassword(newPass);
            System.out.println("✔ Password updated.");
        } catch (NotFoundException | IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void deletePlaylistAsUser() {
        String userId = InputHandler.readLine("User ID: ");
        String playlistId = InputHandler.readLine("Playlist ID: ");
        try {
            User u = platform.findUserById(userId);
            boolean removed = false;
            if (u instanceof FreeUser fu) removed = fu.deletePlaylist(playlistId);
            else if (u instanceof PremiumUser pu) removed = pu.deletePlaylist(playlistId);
            if (removed) platform.removePublicPlaylistById(playlistId);
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void resetMonthlyHoursAsFree() {
        String userId = InputHandler.readLine("User ID (Free tier): ");
        try {
            User u = platform.findUserById(userId);
            if (u instanceof FreeUser fu) fu.resetMonthlyHours();
            else System.out.println("❌ Only Free accounts have a monthly hour cap.");
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void renewPremiumSubscription() {
        String userId = InputHandler.readLine("User ID (Premium): ");
        try {
            User u = platform.findUserById(userId);
            if (u instanceof PremiumUser pu) pu.renewSubscription();
            else System.out.println("❌ Only Premium accounts can renew.");
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void viewOfflineDownloads() {
        String userId = InputHandler.readLine("User ID (Premium): ");
        try {
            User u = platform.findUserById(userId);
            if (u instanceof PremiumUser pu) pu.viewDownloads();
            else System.out.println("❌ Only Premium users have offline downloads.");
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void removeDownloadAsPremium() {
        String userId = InputHandler.readLine("User ID (Premium): ");
        String songId = InputHandler.readLine("Song ID: ");
        try {
            User u = platform.findUserById(userId);
            Song s = platform.findSongById(songId);
            if (u instanceof PremiumUser pu) pu.removeDownload(s);
            else System.out.println("❌ Only Premium users have downloads to remove.");
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void downloadSongAsPremium() {
        String userId = InputHandler.readLine("Premium User ID: ");
        String songId = InputHandler.readLine("Song ID: ");
        try {
            User u = platform.findUserById(userId);
            Song s = platform.findSongById(songId);
            if (u instanceof PremiumUser pu) {
                pu.downloadSong(s);
            } else {
                System.out.println("❌ Only Premium users can download songs.");
            }
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void viewUserHistory() {
        String userId = InputHandler.readLine("User ID: ");
        int count = InputHandler.readInt("How many recent tracks? ");
        try {
            User u = platform.findUserById(userId);
            if (u instanceof FreeUser fu) fu.viewRecentHistory(count);
            else if (u instanceof PremiumUser pu) pu.viewRecentHistory(count);
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ── Artist Menu ───────────────────────────────────────────────────────────

    private static void artistMenu() {
        while (true) {
            System.out.println("\n─── ARTIST MENU ─────────────────────────");
            System.out.println("  1. List all artists");
            System.out.println("  2. Add artist");
            System.out.println("  3. View artist details");
            System.out.println("  4. Verify artist");
            System.out.println("  5. Update monthly listeners");
            System.out.println("  6. Remove artist");
            System.out.println("  0. Back");

            int choice = InputHandler.readInt("Choice: ");
            switch (choice) {
                case 1 -> platform.listArtists();
                case 2 -> addArtist();
                case 3 -> viewArtistDetails();
                case 4 -> verifyArtist();
                case 5 -> updateListeners();
                case 6 -> removeArtist();
                case 0 -> { return; }
                default -> System.out.println("⚠ Invalid option.");
            }
        }
    }

    private static void addArtist() {
        String name = InputHandler.readLine("Artist name: ");
        String genre = InputHandler.readLine("Genre: ");
        String bio = InputHandler.readLine("Bio: ");
        try {
            platform.addArtist(name, genre, bio);
        } catch (InstanceLimitException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void viewArtistDetails() {
        String id = InputHandler.readLine("Artist ID: ");
        try {
            Artist a = platform.findArtistById(id);
            System.out.println("\n" + a);
            System.out.println("  Songs: " + a.getSongIds());
            System.out.println("  Albums: " + a.getAlbumIds());
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void verifyArtist() {
        String id = InputHandler.readLine("Artist ID: ");
        try {
            platform.findArtistById(id).verify();
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void updateListeners() {
        String id = InputHandler.readLine("Artist ID: ");
        int count = InputHandler.readInt("Monthly listener count: ");
        try {
            platform.findArtistById(id).updateMonthlyListeners(count);
            System.out.println("✔ Updated.");
        } catch (NotFoundException | IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void removeArtist() {
        String id = InputHandler.readLine("Artist ID to remove: ");
        try {
            platform.removeArtist(id);
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ── Song Menu ─────────────────────────────────────────────────────────────

    private static void songMenu() {
        while (true) {
            System.out.println("\n─── SONG MENU ───────────────────────────");
            System.out.println("  1. List all songs");
            System.out.println("  2. Add song");
            System.out.println("  3. View song details");
            System.out.println("  4. Play song (standalone)");
            System.out.println("  5. Pause song");
            System.out.println("  6. Stop song");
            System.out.println("  7. Remove song");
            System.out.println("  0. Back");

            int choice = InputHandler.readInt("Choice: ");
            switch (choice) {
                case 1 -> platform.listSongs();
                case 2 -> addSong();
                case 3 -> viewSongDetails();
                case 4 -> playSong();
                case 5 -> pauseSong();
                case 6 -> stopSong();
                case 7 -> removeSong();
                case 0 -> { return; }
                default -> System.out.println("⚠ Invalid option.");
            }
        }
    }

    private static void addSong() {
        String title = InputHandler.readLine("Title: ");
        String artistId = InputHandler.readLine("Artist ID: ");
        String genre = InputHandler.readLine("Genre: ");
        double duration = InputHandler.readDouble("Duration (seconds): ");
        boolean explicit = InputHandler.readBoolean("Explicit content?");
        int year = InputHandler.readInt("Release year: ");
        try {
            platform.addSong(title, artistId, genre, duration, explicit, year);
        } catch (InstanceLimitException | NotFoundException | IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void viewSongDetails() {
        String id = InputHandler.readLine("Song ID: ");
        try {
            System.out.println("\n" + platform.findSongById(id));
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void playSong() {
        String id = InputHandler.readLine("Song ID: ");
        try {
            platform.findSongById(id).play();
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void pauseSong() {
        String id = InputHandler.readLine("Song ID: ");
        try {
            platform.findSongById(id).pause();
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void stopSong() {
        String id = InputHandler.readLine("Song ID: ");
        try {
            platform.findSongById(id).stop();
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void removeSong() {
        String id = InputHandler.readLine("Song ID to remove: ");
        try {
            platform.removeSong(id);
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ── Album Menu ────────────────────────────────────────────────────────────

    private static void albumMenu() {
        while (true) {
            System.out.println("\n─── ALBUM MENU ──────────────────────────");
            System.out.println("  1. List all albums");
            System.out.println("  2. Add album");
            System.out.println("  3. Add song to album");
            System.out.println("  4. View album details");
            System.out.println("  5. Publish album");
            System.out.println("  6. Remove song from album");
            System.out.println("  0. Back");

            int choice = InputHandler.readInt("Choice: ");
            switch (choice) {
                case 1 -> platform.listAlbums();
                case 2 -> addAlbum();
                case 3 -> addSongToAlbum();
                case 4 -> viewAlbumDetails();
                case 5 -> publishAlbum();
                case 6 -> removeSongFromAlbum();
                case 0 -> { return; }
                default -> System.out.println("⚠ Invalid option.");
            }
        }
    }

    private static void addAlbum() {
        String title = InputHandler.readLine("Album title: ");
        String artistId = InputHandler.readLine("Artist ID: ");
        int year = InputHandler.readInt("Release year: ");
        String genre = InputHandler.readLine("Genre: ");
        try {
            platform.addAlbum(title, artistId, year, genre);
        } catch (InstanceLimitException | NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void addSongToAlbum() {
        String albumId = InputHandler.readLine("Album ID: ");
        String songId = InputHandler.readLine("Song ID: ");
        try {
            Album al = platform.findAlbumById(albumId);
            Song s = platform.findSongById(songId);
            al.addSong(s);
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void viewAlbumDetails() {
        String id = InputHandler.readLine("Album ID: ");
        try {
            Album al = platform.findAlbumById(id);
            System.out.println("\n" + al);
            System.out.println("  Songs:");
            al.listSongs().forEach(s -> System.out.println("    " + s.getTitle()
                    + " [" + s.getFormattedDuration() + "]"));
            System.out.println("  Total duration: " + al.getFormattedTotalDuration());
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void publishAlbum() {
        String id = InputHandler.readLine("Album ID: ");
        try {
            platform.findAlbumById(id).publish();
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void removeSongFromAlbum() {
        String albumId = InputHandler.readLine("Album ID: ");
        String songId = InputHandler.readLine("Song ID: ");
        try {
            Album al = platform.findAlbumById(albumId);
            Song s = platform.findSongById(songId);
            al.removeSong(s);
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ── Playlist Menu ─────────────────────────────────────────────────────────

    private static void playlistMenu() {
        while (true) {
            System.out.println("\n─── PLAYLIST MENU ───────────────────────");
            System.out.println("  1. List public playlists");
            System.out.println("  2. Add song to playlist (by owner)");
            System.out.println("  3. Remove song from playlist");
            System.out.println("  4. View playlist songs");
            System.out.println("  5. Shuffle playlist");
            System.out.println("  6. Reorder song in playlist");
            System.out.println("  0. Back");

            int choice = InputHandler.readInt("Choice: ");
            switch (choice) {
                case 1 -> platform.listPublicPlaylists();
                case 2 -> addSongToPlaylist();
                case 3 -> removeSongFromPlaylist();
                case 4 -> viewPlaylistSongs();
                case 5 -> shufflePlaylist();
                case 6 -> reorderSongInPlaylist();
                case 0 -> { return; }
                default -> System.out.println("⚠ Invalid option.");
            }
        }
    }

    private static Playlist findPlaylistFromUser(String userId, String playlistId) throws NotFoundException {
        User u = platform.findUserById(userId);
        List<Playlist> playlists = null;
        if (u instanceof FreeUser fu) playlists = fu.getPlaylists();
        else if (u instanceof PremiumUser pu) playlists = pu.getPlaylists();
        if (playlists == null) throw new NotFoundException("No playlists for user " + userId);
        for (Playlist p : playlists) {
            if (p.getPlaylistId().equals(playlistId)) return p;
        }
        throw new NotFoundException("Playlist " + playlistId + " not found for user " + userId);
    }

    private static void addSongToPlaylist() {
        String userId = InputHandler.readLine("User ID (playlist owner): ");
        String playlistId = InputHandler.readLine("Playlist ID: ");
        String songId = InputHandler.readLine("Song ID: ");
        try {
            Playlist pl = findPlaylistFromUser(userId, playlistId);
            Song s = platform.findSongById(songId);
            pl.addSong(s);
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void removeSongFromPlaylist() {
        String userId = InputHandler.readLine("User ID (playlist owner): ");
        String playlistId = InputHandler.readLine("Playlist ID: ");
        String songId = InputHandler.readLine("Song ID: ");
        try {
            Playlist pl = findPlaylistFromUser(userId, playlistId);
            Song s = platform.findSongById(songId);
            pl.removeSong(s);
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void viewPlaylistSongs() {
        String userId = InputHandler.readLine("User ID (playlist owner): ");
        String playlistId = InputHandler.readLine("Playlist ID: ");
        try {
            findPlaylistFromUser(userId, playlistId).displaySongs();
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void shufflePlaylist() {
        String userId = InputHandler.readLine("User ID (playlist owner): ");
        String playlistId = InputHandler.readLine("Playlist ID: ");
        try {
            findPlaylistFromUser(userId, playlistId).shuffle();
        } catch (NotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void reorderSongInPlaylist() {
        String userId = InputHandler.readLine("User ID (playlist owner): ");
        String playlistId = InputHandler.readLine("Playlist ID: ");
        String songId = InputHandler.readLine("Song ID to move: ");
        int pos = InputHandler.readInt("New position (1-indexed): ");
        try {
            Playlist pl = findPlaylistFromUser(userId, playlistId);
            Song s = platform.findSongById(songId);
            pl.reorderSong(s, pos);
        } catch (NotFoundException | IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ── Global helpers ────────────────────────────────────────────────────────

    private static void showTopSongs() {
        int n = InputHandler.readInt("How many top songs to display? ");
        List<Song> top = platform.getTopSongs(n);
        System.out.println("\n── Top " + top.size() + " Songs ──────────────────────");
        for (int i = 0; i < top.size(); i++) {
            Song s = top.get(i);
            System.out.printf("  %2d. %-35s plays: %d%n", i + 1, s.getTitle(), s.getPlayCount());
        }
    }

    private static void searchByGenrePrompt() {
        String genre = InputHandler.readLine("Genre to search: ");
        List<Song> results = platform.searchByGenre(genre);
        if (results.isEmpty()) {
            System.out.println("No songs found for genre: " + genre);
        } else {
            results.forEach(s -> System.out.println("  " + s));
        }
    }
}
