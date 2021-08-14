package com.pseuco.np21;

import com.pseuco.np21.shared.Recorder;

import java.lang.annotation.Target;
import java.util.List;

/**
 * this class will be used to find the correct Trail which the ant should take when she search food.
 */
public class SearchFoodPathCheck {

    private final SearchFoodTrailHandler searchFoodTrailHandler;
    private final Ant ant;

    public SearchFoodPathCheck(Ant ant) {
        this.searchFoodTrailHandler = new SearchFoodTrailHandler();
        this.ant =ant;
    }

    /** (ignore this methode for now)
     * this methode is used to check whether the chosen Trail still the right one .
     *
     * @param  currentClearing the current Clearing
     * @param  targetTrail  the TargetTrail
     * @return true if the targetTrail still valid.
     */
    private boolean checkIfTheTrailStillValidNormalCase(Clearing currentClearing , Trail targetTrail){
        return searchFoodTrailHandler.checkTrail(currentClearing,targetTrail,ant);
    }



    /**
     * this methode is used to choose the right Trail according to the project description.
     *
     * @param  currentClearing the currentClearing
     * @return the targetTrail.
     */
     public Trail getTargetTrail(Clearing currentClearing){
        List<Trail> trailList = currentClearing.connectsTo();
        return searchFoodTrailHandler.getTargetTrail(trailList,ant);
    }


    /**
     * this methode is used to check whether the Clearing has valid Connected Trail.
     *
     * @param c  Current Clearing.
     * @return   return true if you found a Trail.
     */
     public boolean checkTrail(Clearing c) throws InterruptedException {
        List<Trail> connectedTrails = c.connectsTo();
        return searchFoodTrailHandler.checkTrail(c,connectedTrails,ant);
    }


    /**
     * this methode is used to get the right reversedTrail wHen we Want to step back;
     * @param currentTwiceVisitedClearing   the currentTwiceVisitedClearing which we want to leave.
     * @param trailWeComeFrom           the Trail from which we come from the last time.
     * @return  the reversedTrail which we need to take
     */
     public Trail getTrailToStepBack(Clearing currentTwiceVisitedClearing,Trail trailWeComeFrom){
         //this assert make sure that we get the right Clearing and the right Trail from The Ant methodes(run for example)
        assert (trailWeComeFrom.to().id() == (currentTwiceVisitedClearing.id()));
         return trailWeComeFrom.reverse();
    }



    public Trail getTrailByNofoodReturn(Clearing currentClearing, Ant ant) {
        List<Clearing> sequence = ant.getClearingSequence();  // the sequence
        List<Trail> connectedTrails = currentClearing.connectsTo(); // the out Trails from the Current Clearing
        Trail targetTrail = connectedTrails.get(0); // this ist just to initialize the Trail with some object, it will be changed later in for loop.
        int currentClearingNumberFromTheSequence = 0; // get the index of the currentClearing from sequence.
        for (Clearing clearing : sequence) {     // by looping the sequence
            if (clearing.id() != currentClearing.id()) {
                currentClearingNumberFromTheSequence++;
            } else {
                break;
            }
        }
        //{AntHill, currentClearing, .. ...}
        //now return the Trail which leads to the Clearing which is Ordered in the sequence -->
        for (Trail target : connectedTrails) {    //--> exactly one index behind the CurrentClearing
            if (target.to().id() == sequence.get(currentClearingNumberFromTheSequence - 1).id()) {
               targetTrail =target;
               break;
            }
        }
        return targetTrail;
    }



}
