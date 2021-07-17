package com.pseuco.np21;

/**
 * Representation of a clearing.
 * <p>
 * Manages ant and food levels on it.
 * <p>
 * You may change the code however you see fit.
 */
public class Clearing extends com.pseuco.np21.shared.Clearing<Clearing, Trail> {
    private int ants;
    private int food;

    /**
     * Constructs a new clearing.
     *
     * @param name     of the clearing
     * @param food     the clearing starts with
     * @param capacity the clearing has access to
     */
    public Clearing(final String name, final int food, final Capacity capacity) {
        super(name, food, capacity);
        this.food = initialFood;
    }

    /**
     * Check whether there is still space left on this clearing.
     *
     * @return {@code true} iff there is space left
     */
    public boolean isSpaceLeft() {
        return capacity.isInfinite() || ants < capacity.value();
    }

    /**
     * Call this when an ant enters this clearing.
     */
    public void enter() {
        ants++;
    }

    /**
     * Call this when an ant leaves this clearing.
     */
    public void leave() {
        ants--;
    }

    /**
     * Check whether this clearing has food left.
     *
     * @return {@code true} iff there is food left
     */
    public boolean hasFood() {
        return food > 0;
    }

    /**
     * Call this when an ant picks up food at this clearing.
     */
    public void pickupFood() {
        food--;
    }

    /**
     * Call this when an ant places food at this clearing.
     */
    public void placeFood() {
        food++;
    }
}
