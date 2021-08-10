package com.pseuco.np21;

import java.lang.annotation.Target;
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


    /**
     * this methode is used to choose the right Trail according to the project description.
     *
     * @param currentClearing       The current Clearing
     * @return the targetTrail.
     */
    public Trail getTargetTrail(Clearing currentClearing) {
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

    /* the methode above is false for many reasons: (but i didn't touch your implementation !!!!)
       the reasons:
      1) in the line 32 you are assuming that the currentClearing is always the last one in the sequence , which
      is not true (the sequence should be cleared once the ant is in the Hill and not before according to the description.
      then in line 33 you are using connectedTrails.get(sequence -1)!!!!(sequence!!!!).
      the in the for loop you are retuning the Trail which leads to the last Clearing in the Sequence !! why??
      even when you are assuming that the current Clearing is the last one in the sequence then you want to have a Trail
      which is leading to the same currentClearing!! sorry but i didn't get it ..maybe you can prove to me when we meet online
      that this code actually works, which i doubt.

      2) now for the second part of the methode( not Adventurer) :
        i see you are using here some hashMaps cool . but you can't give a hashMap the same key twice . the second
        using of an already used key will override the first object and not adding the second one .
        how you are using the same key? i will tell you ..you are taking the pheromone value as a key and more than
        one Trail could have the same value of Pheromone. so you may actually override more than one object in this
        way
        the second problem is that even if we assumed that every thing was fine after that the Collection.min will
        give always the last min object you compared (not randomly) i have built a programme like yours and can show
        it to you tomorrow.
        i hope you understand that my note here is not downgrading you every one of us has written some wrong code
        and maybe you can prove to me tomorrow that i am wrong totally okay. just wanted to make sure that every thing works
        have a good night.
      * */
    // this is the same methode i wrote i think till now that it is correct. if you are ok with it just edit the name.
    // if not please don't delete it and discuss it with me later.
    synchronized public Trail correctOne(Clearing currentClearing){
        List<Clearing> sequence =ant.getClearingSequence();  // the sequence
        List<Trail> connectedTrails = currentClearing.connectsTo(); // the out Trails from the Current Clearing
        if (ant.isAdventurer()){ // check if the Ant is an Adventurer.
            int currentClearingNumberFromTheSequence = 0; // get the index of the currentClearing from sequence.
            for (int i = 0 ; i <sequence.size();i++){     // by looping the sequence
                if (sequence.get(i).id() != currentClearing.id()){
                    currentClearingNumberFromTheSequence ++;
                }else {
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
        List<Trail> nonNapTrails = new ArrayList<>();
        addNapTrails(connectedTrails,nonNapTrails);
        List<Trail> minTrails = new ArrayList<>(); // all Trails which has the same min antHill-pheromone
        for (int i = 0; i < (nonNapTrails.size() - 1) ; i++) {  // compare the Pheromones and add the min-ones to the list.
            Trail t1 = nonNapTrails.get(i);
            Trail t2 = nonNapTrails.get(i + 1);
            compareTrails(t1, t2, minTrails);
        }
        Random random = new Random();
        int randomIndex = random.nextInt(minTrails.size());//get random number
         Trail targetTrail =  minTrails.get(randomIndex);
         targetTrail.setSelectionReason(5); // set the selection reason.
        return  targetTrail  ;// now return a random Trails which has min-antHill-Pheromone.
    }

    /**
     * this method used intern to compare the pheromone of two Trails and to add the min one to the list.
     * and remove the non min from it.(you need to read this methode with the loop to understand what she do)
     *
     * @param t1 first Trail
     * @param t2 second Trail
     * @param minTrails list of min Trails.
     */
    private void compareTrails(Trail t1, Trail t2, List<Trail> minTrails) {
        com.pseuco.np21.shared.Trail.Pheromone p1 = t1.getOrUpdateHill(false,null);
        com.pseuco.np21.shared.Trail.Pheromone p2 = t2.getOrUpdateHill(false,null);
        if (p1.value() > p2.value()) {
            minTrails.add(t2);
            // remove t1 if it is present and all the Trails with the same Hill-Value , if not nothing is happening(is written in javaDoc)
            for (int i = 0; i < minTrails.size(); i++) {
                if (minTrails.get(i).getOrUpdateHill(false, null).value() == t1.getOrUpdateHill(false, null).value()) {
                    minTrails.remove(minTrails.get(i));
                }
            }
        } else if (p1.value() < p2.value()) {
            if (! minTrails.contains(t1)) {
                minTrails.add(t1);
            }
        } else {
                if (!minTrails.contains(t1)) {
                    minTrails.add(t1);
                }
                minTrails.add(t2);
            }
        }


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
