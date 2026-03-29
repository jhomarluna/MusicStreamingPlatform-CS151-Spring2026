package com.musicstream.interface_;

/**
 * Interface for any content that can be streamed by a user.
 * Implemented by Song and Podcast.
 */
public interface Streamable {
    void play();
    void pause();
    void stop();
    double getDuration();
    String getTitle();
    boolean isExplicit();
}
