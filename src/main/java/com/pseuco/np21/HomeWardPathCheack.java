package com.pseuco.np21;

import java.util.List;

public class HomeWardPathCheack {

    private final Ant ant;

    /**
     * constructor
     *
     * @param ant Ant
     */

    public HomeWardPathCheack(Ant ant) {
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
        //TODO, DO I NEED THIS ONE?

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
            int minAnthill = connectedTrails.get(trailsNumber).getOrUpdateHill(false,null).value();
            targetTrail = connectedTrails.get(trailsNumber);
            for (int i = 0; i < trailsNumber; i++) {
                if (connectedTrails.get(i).getOrUpdateHill(false,null).value()  < minAnthill) {
                    minAnthill = connectedTrails.get(i).getOrUpdateHill(false,null).value();
                    targetTrail = connectedTrails.get(i);
                }
            }

        }
        return targetTrail;
    }
}
