package com.musicstream;

import com.musicstream.exception.InstanceLimitException;
import com.musicstream.model.ModelLimits;
import com.musicstream.model.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies the 100-instance cap per class (spec explicitly suggests this via unit tests).
 */
public class InstanceLimitTest {

    @BeforeEach
    void resetCounters() {
        ModelTestSupport.resetAllInstanceCounters();
    }

    @Test
    @DisplayName("Creating the 101st song throws InstanceLimitException")
    void songLimit101() throws Exception {
        Platform p = new Platform();
        var artist = p.addArtist("Cap Artist", "Pop", "bio");
        for (int i = 0; i < ModelLimits.MAXIMUM_INSTANCES; i++) {
            p.addSong("T" + i, artist.getArtistId(), "Pop", 60, false, 2020);
        }
        assertThrows(InstanceLimitException.class,
                () -> p.addSong("Overflow", artist.getArtistId(), "Pop", 60, false, 2020));
    }

    @Test
    @DisplayName("Creating the 101st artist throws InstanceLimitException")
    void artistLimit101() throws Exception {
        Platform p = new Platform();
        for (int i = 0; i < ModelLimits.MAXIMUM_INSTANCES; i++) {
            p.addArtist("Artist " + i, "Pop", "bio");
        }
        assertThrows(InstanceLimitException.class, () -> p.addArtist("Overflow", "Pop", "bio"));
    }
}
