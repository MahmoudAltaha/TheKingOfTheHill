package com.pseuco.np21;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HomeWardPathCheck {

    private final Ant ant;

    /**
     * constructor
     *
     * @param ant Ant
     */

    public HomeWardPathCheck(Ant ant) {
        this.ant = ant;
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
            HashMap<Trail, Integer> findingMin = new HashMap<Trail, Integer>();
            HashMap<Integer, Trail> reverseMap = new HashMap<Integer, Trail>();
            for (Trail t : connectedTrails) {
                findingMin.put(t, t.getOrUpdateHill(false, null).value());
                reverseMap.put(t.getOrUpdateHill(false, null).value(), t);
            }
            int min = Collections.min(findingMin.values());
            targetTrail= reverseMap.get(min);

        }
        return targetTrail;
    }
}
