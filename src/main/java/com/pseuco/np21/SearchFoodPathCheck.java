package com.pseuco.np21;

import java.util.List;

public class SearchFoodPathCheck {

    private final SearchFoodTrailHandler searchFoodTrailHandler;
    private Ant ant;

    public SearchFoodPathCheck(Ant ant) {
        this.searchFoodTrailHandler = new SearchFoodTrailHandler();
        this.ant =ant;
    }

    /**
     * this methode is used to check whether the chosen Trail still the right one .
     *
     * @param  currentClearing the current Clearing
     * @param  targetTrail  the TargetTrail
     * @return true if the targetTrail still valid.
     */
    synchronized  public boolean checkIfTheTrailStillValidNormalCase(Clearing currentClearing , Trail targetTrail){
        return searchFoodTrailHandler.checkTrail(currentClearing,targetTrail,ant);
    }


    /**
     *  this methode is used to check whether the chosen Trail still the right one.(case backtracking).
     * @param currentClearing  current Clearing
     * @param lastWrongDeletedClearing  last Wrong deleted Clearing
     * @param t  the trail that need to be checked
     * @return true if the trail still valid.
     */
    synchronized  public boolean CheckIfTheTrailStillValidAfterBackTracks(Clearing currentClearing,Clearing lastWrongDeletedClearing,Trail t){
        return getTargetTrailAfterBackTracks(currentClearing,lastWrongDeletedClearing).id() == t.id();
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
     * this methode is used to get the valid Trail after stepping back because of the two special {d,e} cases.
     *
     * @param currentClearing  the current Clearing where the ant now stays.
     * @param lastWrongDeletedClearing  the last wrong visited Clearing which was deleted from the sequence.
     * @return  the Valid Trail to take.
     */
    synchronized public Trail getTargetTrailAfterBackTracks(Clearing currentClearing, Clearing lastWrongDeletedClearing){
        List<Trail> trailList = currentClearing.connectsTo(); // list of all connected Trails
        for (int i = 0 ; i< trailList.size(); i++){ // delete the Trail which can take us to the deleted Clearing
            Trail t =  trailList.get(i);
            Clearing c = t.to();
            if (c.id() == lastWrongDeletedClearing.id()){
                trailList.remove(t);
            }
        }   // now do the search in this particular list.
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
     * this methode is used when we need to check whether there are valid Trails after we went throw
     * the special case when we enter a Clearing which is already in the sequence. so after one step back
     * we do this check. be careful!!! when this methode returns false , that doesn't mean we have to start the
     * homeward. it does mean that we have to once again back and mark the Trail we took to MaP!!
     * @param currentClearing   the current Clearing where the ant is staying now.
     * @param lastWrongDeletedClearing  the last deleted Clearing after going throw the special case d)
     * @param ant   the Ant
     * @return true if we found a valid Trail,in this case we get the Trail and enter it normally.
     */
    synchronized public boolean CheckTrailAfterBackTracks(Clearing currentClearing,Clearing lastWrongDeletedClearing,Ant ant){
        return searchFoodTrailHandler.CheckTrailAfterBackTracks(currentClearing,lastWrongDeletedClearing,ant);
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
