package com.musicstream;

import com.musicstream.model.Album;
import com.musicstream.model.Artist;
import com.musicstream.model.Playlist;
import com.musicstream.model.Song;
import com.musicstream.model.User;

import java.lang.reflect.Field;

/**
 * Resets static instance counters between tests so limits and IDs stay deterministic.
 */
public final class ModelTestSupport {

    private ModelTestSupport() {}

    public static void resetAllInstanceCounters() {
        try {
            setStaticInt(User.class, "instanceCount", 0);
            setStaticInt(Song.class, "instanceCount", 0);
            setStaticInt(Artist.class, "instanceCount", 0);
            setStaticInt(Album.class, "instanceCount", 0);
            setStaticInt(Playlist.class, "instanceCount", 0);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setStaticInt(Class<?> clazz, String fieldName, int value)
            throws ReflectiveOperationException {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.setInt(null, value);
    }
}
