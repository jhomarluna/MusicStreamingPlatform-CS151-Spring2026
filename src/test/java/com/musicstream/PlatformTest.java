package com.musicstream;

import com.musicstream.exception.InstanceLimitException;
import com.musicstream.exception.NotFoundException;
import com.musicstream.model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the StreamFlow Music Streaming Platform.
 * Covers core business logic, instance limits, and exception handling.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlatformTest {

    private Platform platform;

    @BeforeEach
    void setUp() {
        ModelTestSupport.resetAllInstanceCounters();
        platform = new Platform();
    }

    // ── User registration ─────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Register a Free user successfully")
    void testRegisterFreeUser() throws InstanceLimitException {
        FreeUser u = platform.registerFreeUser("alice", "alice@test.com", "pass");
        assertNotNull(u);
        assertEquals("alice", u.getUsername());
        assertEquals("Free", u.getSubscriptionType());
    }

    @Test
    @Order(2)
    @DisplayName("Register a Premium user successfully")
    void testRegisterPremiumUser() throws InstanceLimitException {
        PremiumUser u = platform.registerPremiumUser("bob", "bob@test.com", "pass", 9.99);
        assertNotNull(u);
        assertEquals("Premium", u.getSubscriptionType());
        assertEquals(9.99, u.getMonthlyFee(), 0.001);
    }

    @Test
    @Order(3)
    @DisplayName("Duplicate email should throw IllegalArgumentException")
    void testDuplicateEmail() throws InstanceLimitException {
        platform.registerFreeUser("user1", "dupe@test.com", "pass");
        assertThrows(IllegalArgumentException.class,
                () -> platform.registerFreeUser("user2", "dupe@test.com", "pass2"));
    }

    // ── Artist management ─────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("Add artist and find by ID")
    void testAddAndFindArtist() throws InstanceLimitException, NotFoundException {
        Artist a = platform.addArtist("Test Artist", "Jazz", "A jazz musician.");
        Artist found = platform.findArtistById(a.getArtistId());
        assertEquals("Test Artist", found.getName());
    }

    @Test
    @Order(5)
    @DisplayName("Finding non-existent artist throws NotFoundException")
    void testArtistNotFound() {
        assertThrows(NotFoundException.class, () -> platform.findArtistById("NONEXISTENT"));
    }

    @Test
    @Order(6)
    @DisplayName("Verify artist sets verified flag")
    void testVerifyArtist() throws InstanceLimitException {
        Artist a = platform.addArtist("Verified Artist", "Rock", "Bio.");
        assertFalse(a.isVerified());
        a.verify();
        assertTrue(a.isVerified());
    }

    // ── Song management ───────────────────────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("Add song and retrieve it")
    void testAddSong() throws InstanceLimitException, NotFoundException {
        Artist a = platform.addArtist("Song Artist", "Pop", "Bio.");
        Song s = platform.addSong("Test Song", a.getArtistId(), "Pop", 210, false, 2023);
        assertEquals("Test Song", s.getTitle());
        assertEquals("Pop", s.getGenre());
        assertFalse(s.isExplicit());
    }

    @Test
    @Order(8)
    @DisplayName("Song play count increments correctly")
    void testPlayCount() throws InstanceLimitException, NotFoundException {
        Artist a = platform.addArtist("Play Artist", "Pop", "Bio.");
        Song s = platform.addSong("Count Song", a.getArtistId(), "Pop", 180, false, 2020);
        int before = s.getPlayCount();
        s.incrementPlayCount();
        s.incrementPlayCount();
        assertEquals(before + 2, s.getPlayCount());
    }

    @Test
    @Order(9)
    @DisplayName("Song formatted duration is correct")
    void testFormattedDuration() throws InstanceLimitException, NotFoundException {
        Artist a = platform.addArtist("Duration Artist", "Pop", "Bio.");
        Song s = platform.addSong("3:30 Song", a.getArtistId(), "Pop", 210, false, 2021);
        assertEquals("3:30", s.getFormattedDuration());
    }

    // ── Album management ──────────────────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("Add album and link songs")
    void testAlbum() throws InstanceLimitException, NotFoundException {
        Artist a = platform.addArtist("Album Artist", "Rock", "Bio.");
        Album al = platform.addAlbum("Test Album", a.getArtistId(), 2022, "Rock");
        Song s1 = platform.addSong("Track 1", a.getArtistId(), "Rock", 200, false, 2022);
        Song s2 = platform.addSong("Track 2", a.getArtistId(), "Rock", 180, false, 2022);
        al.addSong(s1);
        al.addSong(s2);
        assertEquals(2, al.getSongCount());
    }

    @Test
    @Order(11)
    @DisplayName("Empty album cannot be published")
    void testPublishEmptyAlbum() throws InstanceLimitException, NotFoundException {
        Artist a = platform.addArtist("Empty Album Artist", "Jazz", "Bio.");
        Album al = platform.addAlbum("Empty Album", a.getArtistId(), 2023, "Jazz");
        al.publish(); // should print warning, not throw
        assertFalse(al.isPublished());
    }

    @Test
    @Order(12)
    @DisplayName("Album total duration sums correctly")
    void testAlbumTotalDuration() throws InstanceLimitException, NotFoundException {
        Artist a = platform.addArtist("Dur Artist", "Pop", "Bio.");
        Album al = platform.addAlbum("Dur Album", a.getArtistId(), 2021, "Pop");
        Song s1 = platform.addSong("Dur Track 1", a.getArtistId(), "Pop", 60, false, 2021);
        Song s2 = platform.addSong("Dur Track 2", a.getArtistId(), "Pop", 90, false, 2021);
        al.addSong(s1);
        al.addSong(s2);
        assertEquals(150.0, al.getTotalDuration(), 0.001);
    }

    // ── Playlist management ───────────────────────────────────────────────────

    @Test
    @Order(13)
    @DisplayName("Create playlist and add songs")
    void testPlaylist() throws InstanceLimitException, NotFoundException {
        Artist a = platform.addArtist("PL Artist", "Pop", "Bio.");
        Song s = platform.addSong("PL Song", a.getArtistId(), "Pop", 200, false, 2022);
        FreeUser u = platform.registerFreeUser("pluser", "pl@test.com", "pass");
        Playlist pl = u.createPlaylist("PLT001", "My List", "desc", false);
        pl.addSong(s);
        assertEquals(1, pl.getSongCount());
    }

    @Test
    @Order(14)
    @DisplayName("Duplicate song in playlist is rejected")
    void testNoDuplicateSongsInPlaylist() throws InstanceLimitException, NotFoundException {
        Artist a = platform.addArtist("Dup PL Artist", "Pop", "Bio.");
        Song s = platform.addSong("Dup PL Song", a.getArtistId(), "Pop", 200, false, 2022);
        FreeUser u = platform.registerFreeUser("dupuser", "dup@test.com", "pass");
        Playlist pl = u.createPlaylist("DUPPL", "Dup List", "desc", false);
        pl.addSong(s);
        pl.addSong(s); // duplicate
        assertEquals(1, pl.getSongCount());
    }

    // ── Streaming permissions ─────────────────────────────────────────────────

    @Test
    @Order(15)
    @DisplayName("Free user cannot stream explicit content")
    void testFreeUserExplicitBlocked() throws InstanceLimitException, NotFoundException {
        Artist a = platform.addArtist("Exp Artist", "Hip-Hop", "Bio.");
        Song explicit = platform.addSong("Explicit Song", a.getArtistId(), "Hip-Hop", 180, true, 2020);
        FreeUser u = platform.registerFreeUser("freeexp", "freeexp@test.com", "pass");
        // streamContent catches the exception internally and prints error — just verify no crash
        assertDoesNotThrow(() -> u.streamContent(explicit));
        // play count should NOT have incremented since stream was blocked
        assertEquals(0, explicit.getPlayCount());
    }

    @Test
    @Order(16)
    @DisplayName("Premium user can stream explicit content")
    void testPremiumUserExplicit() throws InstanceLimitException, NotFoundException {
        Artist a = platform.addArtist("Prem Exp Artist", "Hip-Hop", "Bio.");
        Song explicit = platform.addSong("Prem Explicit Song", a.getArtistId(), "Hip-Hop", 180, true, 2020);
        PremiumUser u = platform.registerPremiumUser("premexp", "premexp@test.com", "pass", 9.99);
        assertDoesNotThrow(() -> u.streamContent(explicit));
    }

    // ── Genre search ──────────────────────────────────────────────────────────

    @Test
    @Order(17)
    @DisplayName("Search by genre returns correct results")
    void testSearchByGenre() throws InstanceLimitException, NotFoundException {
        Artist a = platform.addArtist("Genre Artist", "Jazz", "Bio.");
        platform.addSong("Jazz Track 1", a.getArtistId(), "Jazz", 200, false, 2020);
        platform.addSong("Jazz Track 2", a.getArtistId(), "Jazz", 180, false, 2021);
        platform.addSong("Pop Track", a.getArtistId(), "Pop", 210, false, 2022);
        assertEquals(2, platform.searchByGenre("Jazz").size());
        assertEquals(1, platform.searchByGenre("Pop").size());
    }

    // ── Top songs ─────────────────────────────────────────────────────────────

    @Test
    @Order(18)
    @DisplayName("Top songs are ordered by play count")
    void testTopSongs() throws InstanceLimitException, NotFoundException {
        Artist a = platform.addArtist("Top Artist", "Pop", "Bio.");
        Song s1 = platform.addSong("High Play", a.getArtistId(), "Pop", 180, false, 2020);
        Song s2 = platform.addSong("Low Play", a.getArtistId(), "Pop", 180, false, 2020);
        for (int i = 0; i < 10; i++) s1.incrementPlayCount();
        for (int i = 0; i < 2; i++) s2.incrementPlayCount();
        var top = platform.getTopSongs(2);
        assertEquals(s1.getSongId(), top.get(0).getSongId());
    }

    // ── Input validation ──────────────────────────────────────────────────────

    @Test
    @Order(19)
    @DisplayName("Invalid email format throws exception")
    void testInvalidEmail() throws InstanceLimitException {
        FreeUser u = platform.registerFreeUser("validuser", "valid@test.com", "pass");
        assertThrows(IllegalArgumentException.class, () -> u.setEmail("notanemail"));
    }

    @Test
    @Order(20)
    @DisplayName("Negative monthly listener count throws exception")
    void testNegativeListeners() throws InstanceLimitException {
        Artist a = platform.addArtist("Neg Listener Artist", "Rock", "Bio.");
        assertThrows(IllegalArgumentException.class, () -> a.updateMonthlyListeners(-1));
    }

    @Test
    @Order(21)
    @DisplayName("User.changePassword updates stored password with validation")
    void testChangePassword() throws InstanceLimitException {
        FreeUser u = platform.registerFreeUser("pwuser", "pw@test.com", "secret");
        u.changePassword("newsecret");
        assertEquals("newsecret", u.getPasswordHash());
        assertThrows(IllegalArgumentException.class, () -> u.changePassword("ab"));
    }
}
