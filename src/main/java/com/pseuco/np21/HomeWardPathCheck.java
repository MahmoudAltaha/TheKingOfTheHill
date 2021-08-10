package com.pseuco.np21;

import java.util.*;

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

    public Trail getTargetTrail(Clearing currentClearing){
        List<Clearing> sequence =ant.getClearingSequence();  // the sequence
        List<Trail> connectedTrails = currentClearing.connectsTo(); // the out Trails from the Current Clearing
        if (ant.isAdventurer()){ // check if the Ant is an Adventurer.
            int currentClearingNumberFromTheSequence = 0; // get the index of the currentClearing from sequence.
            for (Clearing clearing : sequence) {     // by looping the sequence
                if (clearing.id() != currentClearing.id()) {
                    currentClearingNumberFromTheSequence++;
                } else {
                    break;
                }
            }
            //now return the Trail which leads to the Clearing which is Ordered in the sequence -->
            for (Trail target : connectedTrails) {    //--> exactly one index behind the CurrentClearing
                if (target.to().id() == sequence.get(currentClearingNumberFromTheSequence - 1).id()) {
                    target.setSelectionReason(6);   // set the selectionReason
                    return target;
                }
            }
        }
         /*
        List<Trail> nonNapTrails = new ArrayList<>();
        addNapTrails(connectedTrails,nonNapTrails);*/
        List<Trail> minTrails = new ArrayList<>(); // all Trails which has the same min antHill-pheromone
        makeListWithJustMin(minTrails,connectedTrails);
        Random random = new Random();
        int randomIndex = random.nextInt(minTrails.size());//get random number
        Trail targetTrail =  minTrails.get(randomIndex);
        targetTrail.setSelectionReason(5); // set the selection reason.
        return  targetTrail  ;// now return a random Trails which has min-antHill-Pheromone.
    }

    /**
     * this methode used to add the Trails with min-Hill Value to a min List.
     * @param minTrailsList  List with min-Hill Trails.
     * @param connectedTrails   the Trails which has all Trails .
     */
    private void makeListWithJustMin(List<Trail> minTrailsList ,List<Trail> connectedTrails){
        for (int i = 0; i < (connectedTrails.size() - 1) ; i++) {  // compare the Pheromones and add the min-ones to the list.
            Trail t1 = connectedTrails.get(i);
            Trail t2 = connectedTrails.get(i + 1);
            com.pseuco.np21.shared.Trail.Pheromone p1 = t1.getOrUpdateHill(false,null);
            com.pseuco.np21.shared.Trail.Pheromone p2 = t2.getOrUpdateHill(false,null);
            if (p1.value() > p2.value()) {
                minTrailsList.add(t2);
                // remove t1 if it is present and all the Trails with the same Hill-Value , if not nothing is happening(is written in javaDoc)
                for (int k = 0; k < minTrailsList.size(); k++) {
                    int hillValueOfTrailInList = minTrailsList.get(k).getOrUpdateHill(false, null).value();
                    if (hillValueOfTrailInList == t1.getOrUpdateHill(false, null).value()) {
                        minTrailsList.remove(minTrailsList.get(k));
                    }
                }
            } else if (p1.value() < p2.value()) {
                if (! minTrailsList.contains(t1)) {
                    minTrailsList.add(t1);
                }
            } else {
                if (!minTrailsList.contains(t1)) {
                    minTrailsList.add(t1);
                }
                minTrailsList.add(t2);
            }
        }

    }



    /**
     * adding the Trails with the NON-Nap-Food-Pheromones to the second list (we may not use it!!)
     * @param trailList connectedTrails
     * @param nonNapTrails list of Trails with nonNap
     */
    private void addNapTrails(List<Trail> trailList,List<Trail> nonNapTrails) {
        assert (!trailList.isEmpty());  // check if the list are not Empty
        for (Trail trail : trailList) {
            com.pseuco.np21.shared.Trail.Pheromone p = trail.getOrUpdateFood(false, null, false);
            int valueToCheck = p.value(); // pheromone of the Trail.
            if (valueToCheck == -2) {  // check if the Trail has Nap food-Ph.
                nonNapTrails.add(trail);

            }
        }
    }

}
