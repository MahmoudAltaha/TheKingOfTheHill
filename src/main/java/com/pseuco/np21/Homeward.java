package com.pseuco.np21;

import java.util.List;

public class Homeward {

    private final Ant ant;

    /**
     * constructor
     *
     * @param ant Ant
     */

    public Homeward(Ant ant) {
        this.ant = ant;
    }

    /**
     * this methode is used to check whether the chosen Trail still the right one .
     *
     * @param currentClearing The current Clearing
     * @param targetTrail     The target trail
     * @return true if the targetTrail still valid.
     */
    public boolean checkTrail(Clearing currentClearing, Trail targetTrail) {
        //TODO


        return false;
    }

    /**
     * this methode is used to choose the right Trail according to the project description.
     *
     * @param currentClearing       The current Clearing
     * @return the targetTrail.
     */
    public synchronized Trail getTargetTrail(Clearing currentClearing) {
        Clearing lastClearing;  //last clearing that have been visited
        List<Trail> connectedTrails = currentClearing.connectsTo(); //List of connected trials with currentClearing
        Trail targetTrail;
        if (ant.isAdventurer()) {  //when ant is an adventurer, take exact the same sequence back to anthill
            int sequenceSize = ant.getClearingSequence().size();
            lastClearing = ant.getClearingSequence().get(sequenceSize - 1);
            targetTrail = connectedTrails.get(sequenceSize-1);
            for (Trail trail : connectedTrails) { // searching the trail connected to last visited clearing
                if (trail.to().equals(lastClearing)) {
                    targetTrail = trail;
                }
            }
        } else {
            // when ant is NOT an adventurer, take the trail with minAnthillPheromone
            int trailsNumber = connectedTrails.size() - 1;
            int minAnthill = connectedTrails.get(trailsNumber).anthill().value();
            targetTrail = connectedTrails.get(trailsNumber);
            for (int i = 0; i < trailsNumber; i++) {
                if (connectedTrails.get(i).anthill().value()  < minAnthill) {
                    minAnthill = connectedTrails.get(i).anthill().value();
                    targetTrail = connectedTrails.get(i);
                }
            }

        }
        return targetTrail;
    }

    /**
     * this methode is used to check whether the Clearing has a Connected Trail.
     *
     * @param c Current Clearing.
     * @return return true if you found a Trail.
     */
    public boolean checkTrail(Clearing c) {
        //TODO complete this
        return !c.connectsTo().isEmpty();
    }


    /**
     * handling how to enter a Trail t.
     *
     * @param c The current Clearing
     * @param t The target trail
     * @return true by successfully entering the trail.
     * @throws InterruptedException
     */

    public synchronized boolean enterTrail(Clearing c, Trail t) throws InterruptedException {

        while (!t.isSpaceLeft()) {
            wait();
        }
        c.leave();
        ant.getRecorder().leave(ant, c);
        t.enter();
        ant.getRecorder().enter(ant, t);
        if (!c.equals(ant.getWorld().anthill())){
            notifyAll();
        }
        //TODO Food-Pheromone update Handling


        return true;
    }

    /**
     * handling how to enter a Clearing c.
     *
     * @param t The current trail.
     * @param c The target Clearing.
     * @return true by successfully entering the Clearing.
     * @throws InterruptedException
     */
    public synchronized boolean enterClearing(Trail t, Clearing c) throws InterruptedException {
        //TODO implement this
        return true;
    }

    /**
     * drop the food item into the anthill
     *
     * @param c  The Anthill
     * @return true by successfully dropping food
     */
    public synchronized boolean dropFood(Clearing c) {
        //TODO implement this
        return true;
    }

}
