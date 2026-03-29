package com.musicstream.model;

/**
 * Central cap for how many instances of each model type the platform may hold.
 * The project specification asks for this limit to be defined in one place.
 */
public final class ModelLimits {

    public static final int MAXIMUM_INSTANCES = 100;

    private ModelLimits() {}
}
