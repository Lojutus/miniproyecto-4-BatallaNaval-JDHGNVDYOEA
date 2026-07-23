package com.example.batallanaval.model.Classes.Utils;

/**
 * Outcome of performing a shot against a target.
 */
public enum ShotResult {
    /** Shot hit water (miss). */
    WATER,
    /** Shot caused a vessel to be sunk. */
    SUNK,
    /** Shot hit a vessel but did not sink it. */
    HIT
}
