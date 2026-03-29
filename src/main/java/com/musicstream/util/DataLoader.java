package com.musicstream.util;

import com.musicstream.exception.InstanceLimitException;
import com.musicstream.exception.NotFoundException;
import com.musicstream.model.*;

/**
 * Seeds the platform with sample artists, albums, songs, and users
 * so the system is immediately usable when first launched.
 */
public class DataLoader {

    public static void load(Platform platform) {
        System.out.println("Loading sample data...");
        try {
            // ── Artists ───────────────────────────────────────────────────────
            Artist taylorSwift = platform.addArtist("Taylor Swift", "Pop",
                    "10x Grammy-winning singer-songwriter.");
            Artist kendrickLamar = platform.addArtist("Kendrick Lamar", "Hip-Hop",
                    "Pulitzer Prize-winning rapper from Compton.");
            Artist billiEilish = platform.addArtist("Billie Eilish", "Alternative Pop",
                    "Multi-Grammy winner known for whispery vocals.");
            Artist theWeeknd = platform.addArtist("The Weeknd", "R&B",
                    "Canadian R&B superstar Abel Tesfaye.");

            taylorSwift.verify();
            kendrickLamar.verify();
            billiEilish.verify();
            theWeeknd.verify();

            taylorSwift.updateMonthlyListeners(85_000_000);
            kendrickLamar.updateMonthlyListeners(42_000_000);
            billiEilish.updateMonthlyListeners(60_000_000);
            theWeeknd.updateMonthlyListeners(70_000_000);

            // ── Songs ─────────────────────────────────────────────────────────
            Song antiHero = platform.addSong("Anti-Hero", taylorSwift.getArtistId(),
                    "Pop", 200, false, 2022);
            Song shakeitOff = platform.addSong("Shake It Off", taylorSwift.getArtistId(),
                    "Pop", 219, false, 2014);
            Song lover = platform.addSong("Lover", taylorSwift.getArtistId(),
                    "Pop", 221, false, 2019);

            Song humble = platform.addSong("HUMBLE.", kendrickLamar.getArtistId(),
                    "Hip-Hop", 177, true, 2017);
            Song dna = platform.addSong("DNA.", kendrickLamar.getArtistId(),
                    "Hip-Hop", 185, true, 2017);
            Song notLikeUs = platform.addSong("Not Like Us", kendrickLamar.getArtistId(),
                    "Hip-Hop", 274, true, 2024);

            Song badGuy = platform.addSong("bad guy", billiEilish.getArtistId(),
                    "Alternative Pop", 194, false, 2019);
            Song happierThanEver = platform.addSong("Happier Than Ever", billiEilish.getArtistId(),
                    "Alternative Pop", 295, false, 2021);

            Song blinding = platform.addSong("Blinding Lights", theWeeknd.getArtistId(),
                    "R&B", 200, false, 2019);
            Song saveYourTears = platform.addSong("Save Your Tears", theWeeknd.getArtistId(),
                    "R&B", 215, false, 2020);

            // bump play counts to make top-songs interesting
            for (int i = 0; i < 50; i++) antiHero.incrementPlayCount();
            for (int i = 0; i < 80; i++) blinding.incrementPlayCount();
            for (int i = 0; i < 70; i++) humble.incrementPlayCount();
            for (int i = 0; i < 40; i++) badGuy.incrementPlayCount();
            for (int i = 0; i < 60; i++) notLikeUs.incrementPlayCount();

            // ── Albums ────────────────────────────────────────────────────────
            Album midnights = platform.addAlbum("Midnights", taylorSwift.getArtistId(), 2022, "Pop");
            midnights.addSong(antiHero);

            Album lover_album = platform.addAlbum("Lover", taylorSwift.getArtistId(), 2019, "Pop");
            lover_album.addSong(lover);
            lover_album.addSong(shakeitOff);
            lover_album.publish();

            Album damn = platform.addAlbum("DAMN.", kendrickLamar.getArtistId(), 2017, "Hip-Hop");
            damn.addSong(humble);
            damn.addSong(dna);
            damn.publish();

            Album whenWeAllFallAsleep = platform.addAlbum("When We All Fall Asleep, Where Do We Go?",
                    billiEilish.getArtistId(), 2019, "Alternative Pop");
            whenWeAllFallAsleep.addSong(badGuy);
            whenWeAllFallAsleep.publish();

            Album afterHours = platform.addAlbum("After Hours", theWeeknd.getArtistId(), 2020, "R&B");
            afterHours.addSong(blinding);
            afterHours.addSong(saveYourTears);
            afterHours.publish();

            // ── Users ─────────────────────────────────────────────────────────
            FreeUser alice = platform.registerFreeUser("alice", "alice@email.com", "pass123");
            PremiumUser bob = platform.registerPremiumUser("bob", "bob@email.com", "pass456", 9.99);

            // Alice creates a playlist
            Playlist aliceFaves = alice.createPlaylist(
                    platform.generatePlaylistId(), "Alice's Faves", "My top tracks", true);
            aliceFaves.addSong(antiHero);
            aliceFaves.addSong(badGuy);
            aliceFaves.addSong(blinding);
            platform.registerPublicPlaylist(aliceFaves);

            // Bob creates a playlist
            Playlist bobHipHop = bob.createPlaylist(
                    platform.generatePlaylistId(), "Hip-Hop Heat", "Best rap tracks", true);
            bobHipHop.addSong(humble);
            bobHipHop.addSong(dna);
            bobHipHop.addSong(notLikeUs);
            platform.registerPublicPlaylist(bobHipHop);

            // Bob downloads a song
            bob.downloadSong(blinding);

            System.out.println("\n✔ Sample data loaded successfully.\n");

        } catch (InstanceLimitException | NotFoundException e) {
            System.out.println("Data load error: " + e.getMessage());
        }
    }
}
