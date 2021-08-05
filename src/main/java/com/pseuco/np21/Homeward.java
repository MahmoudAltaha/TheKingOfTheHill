package com.pseuco.np21;

public class Homeward {

    private final Ant ant;

    /**
     * constructor
     * @param ant
     */

    public Homeward(Ant ant) {
        this.ant = ant;
    }

    /**
     * handling how to enter a Trail t.
     *
     * @param c     The current Clearing
     * @param t     The target trail
     * @return      true by successfully entering the trail.
     * @throws      InterruptedException
     */

    public synchronized boolean enterTrail (Clearing c, Trail t) throws InterruptedException{
        assert t != null;
        while (!t.isSpaceLeft()){
            wait();
        }
        t.enter();
        return true;
    }

    /**
     * handling how to enter a Clearing c.
     *
     * @param t      The current trail.
     * @param c      The target Clearing.
     * @return       true by successfully entering the Clearing.
     * @throws       InterruptedException
     */
    public synchronized boolean enterClearing(Trail t, Clearing c) throws InterruptedException{
        //TODO implement this
        return true;
    }

    /**
     * drop the food item into the anthill
     * @param c
     * @return      true by successfully dropping food
     */
    public synchronized boolean dropFood (Clearing c) {
        //TODO implement this
        return true;
    }

}
