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

    /**
     * helps by the selection for the Recorder
     * 1 --> FOOD_SEARCH,
     * 2---> EXPLORATION,
     * 3---> IMMEDIATE_RETURN,
     * 4---> NO_FOOD_RETURN,
     * 5---> RETURN_FOOD,
     * 6---> RETURN_IN_SEQUENCE,
     */
    private  int selectionReason ;
    private TrailEntry trailEntry;    // to handle the entering to this Trail in a concurrent way.

    private Trail(final Trail reverse) {
        super(reverse);
        this.selectionReason = 0;
        this.anthill = Pheromone.NOT_A_PHEROMONE;
        this.food = Pheromone.NOT_A_PHEROMONE;
        this.ants = 0;
        this.trailEntry = new TrailEntry(this);

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
     * getter
     *
     * @return selectionReason.(int)
     */
    public int getSelectionReason() {
        return selectionReason;
    }

    /**
     * setter
     *
     * @param selectionReason selectionReason in (int)
     */
    public void setSelectionReason(int selectionReason) {
        this.selectionReason = selectionReason;
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


    /**
     * this methode will be used to enter this Trail in way that ensure concurrency.
     * @param currentClearing  the Current Clearing which the Ant should left,
     * @param ant       the Ant
     * @param entryReason   the reason you have to enter this Trail.
     * @return      true if the entry was completed successfully.
     * @throws InterruptedException
     */
    public boolean enterTrail(Clearing currentClearing,Ant ant,EntryReason entryReason) throws InterruptedException {
        return switch (entryReason) {
            case FOOD_SEARCH -> trailEntry.enterTrailFoodSearch(currentClearing,ant);
            case IMMEDIATE_RETURN -> trailEntry.immediateReturnToTrail(currentClearing,ant);
            case NO_FOOD_RETURN -> trailEntry.noFoodReturnToTrail(currentClearing,ant);
            /* TODO complete this */ // case HEADING_BACK_HOME -> trailEntry.
            default -> false;
        };
    }


}
