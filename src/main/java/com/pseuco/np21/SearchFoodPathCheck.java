package com.pseuco.np21;

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
    synchronized  private boolean checkIfTheTrailStillValidNormalCase(Clearing currentClearing , Trail targetTrail){
        return searchFoodTrailHandler.checkTrail(currentClearing,targetTrail,ant);
    }



    /**
     * this methode is used to choose the right Trail according to the project description.
     *
     * @param  currentClearing the currentClearing
     * @return the targetTrail.
     */
    synchronized public Trail getTargetTrail(Clearing currentClearing){
        List<Trail> trailList = currentClearing.connectsTo();
        return searchFoodTrailHandler.getTargetTrail(trailList,ant);
    }


    /**
     * this methode is used to check whether the Clearing has valid Connected Trail.
     *
     * @param c  Current Clearing.
     * @return   return true if you found a Trail.
     */
    synchronized public boolean checkTrail(Clearing c){
        List<Trail> connectedTrails = c.connectsTo();
        return   searchFoodTrailHandler.checkTrail(c,connectedTrails,ant);
    }


    /**
     * this methode is used to get the right reversedTrail wHen we Want to step back;
     * @param currentTwiceVisitedClearing   the currentTwiceVisitedClearing which we want to leave.
     * @param trailWeComeFrom           the Trail from which we come from the last time.
     * @return  the reversedTrail which we need to take
     */
    synchronized  public Trail getTrailToStepBack(Clearing currentTwiceVisitedClearing,Trail trailWeComeFrom){
        assert (trailWeComeFrom.to().equals(currentTwiceVisitedClearing));
        return trailWeComeFrom.reverse();
    }


}
