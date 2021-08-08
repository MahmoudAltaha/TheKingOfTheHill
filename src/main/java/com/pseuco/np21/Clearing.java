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
    private final ClearingEntry clearingEntry;

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
        this.clearingEntry = new ClearingEntry(this);
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


    /**
     * this methode will be used to enter this Clearing in way that ensure concurrency.
     * @param currentTrail  the Current Trail which the Ant should left,
     * @param ant       the Ant
     * @param entryReason   the reason you have to enter this Clearing.
     * @return      true if the entry was completed successfully.
     * @throws InterruptedException
     */
    public boolean enterClearing(Trail currentTrail,Ant ant,EntryReason entryReason) throws InterruptedException {
        return switch (entryReason) {
            case FOOD_SEARCH -> clearingEntry.enterClearingFoodSearch(currentTrail, ant);
            case IMMEDIATE_RETURN -> clearingEntry.immediateReturnTOClearing(currentTrail, ant);
            case NO_FOOD_RETURN -> clearingEntry.noFoodReturnTOClearing(currentTrail, ant);
           /* TODO complete this */ //   case HEADING_BACK_HOME -> clearingEntry.
            default -> false;
        };
    }

    /**
     * this methode used to pickUp one piece of Food from this Clearing.
     * @param ant  The Ant
     * @return true if the Ant has pickedUp the food.
     */
    public boolean TakeOnPieceOfFood(Ant ant){
        return clearingEntry.pickUPFood(ant);
    }

    /**
     * drop the food in the Hill
     *
     * @param c the Hill
     * @return true by successfully dropping food
     */
    public synchronized boolean dropFood(Clearing c) {
        //TODO implement this
        return true;
    }


}
