package com.pseuco.np21;

/**
 * Representation of a trail.
 * <p>
 * Manages ant and pheromone levels on it.
 * <p>
 * You may change the code however you see fit.
 */
public class Trail extends com.pseuco.np21.shared.Trail<Clearing, Trail> {
    private Pheromone anthill;
    private Pheromone food;
    private int ants;

    private Trail(final Trail reverse) {
        super(reverse);

        this.anthill = Pheromone.NOT_A_PHEROMONE;
        this.food = Pheromone.NOT_A_PHEROMONE;
        this.ants = 0;
    }

    /**
     * Constructs a new trail given the connected clearings.
     *
     * @param from clearing the trail leads away from
     * @param to   clearing the trail leads to
     */
    public Trail(final Clearing from, final Clearing to) {
        super(from, to, (reverse) -> new Trail((Trail) reverse));

        this.anthill = Pheromone.NOT_A_PHEROMONE;
        this.food = Pheromone.NOT_A_PHEROMONE;
        this.ants = 0;
    }

    /**
     * Get the anthill pheromone level.
     *
     * @return anthill pheromone level
     */
    public Pheromone anthill() {
        return anthill;
    }

    /**
     * Get the food pheromone level.
     *
     * @return food pheromone level
     */
    public Pheromone food() {
        return food;
    }

    /**
     * Update the anthill pheromone level.
     *
     * @param p the new pheromone level
     */
    public void updateAnthill(final Pheromone p) {
        if (!p.isInfinite() && anthill.isAPheromone() && PHEROMONE_COMPARATOR.compare(p, anthill) > 0)
            return;
        anthill = p;
    }

    /**
     * Update the food pheromone level.
     *
     * @param p        the new pheromone level
     * @param explorer {@code true} iff the ant is in exploration mode
     */
    public void updateFood(final Pheromone p, final boolean explorer) {
        if (explorer && !p.isInfinite() && food.isAPheromone() && PHEROMONE_COMPARATOR.compare(p, food) > 0)
            return;
        food = p;
    }

    /**
     * Check whether there is still space left on this trail.
     *
     * @return {@code true} iff there is space left
     */
    public boolean isSpaceLeft() {
        return capacity.isInfinite() || ants < capacity.value();
    }

    /**
     * Call this when an ant enters this trail.
     */
    public void enter() {
        ants++;
    }

    /**
     * Call this when an ant leaves this trail.
     */
    public void leave() {
        ants--;
    }
}
