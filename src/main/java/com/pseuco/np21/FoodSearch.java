package com.pseuco.np21;


import java.util.List;

public class FoodSearch {

    private final Ant ant;
    private final SearchFoodTrailHandler searchTrailHandler;
    /**
     * constructor for the Class
     * @param ant the Ant
     */
    public FoodSearch(Ant ant) {
        this.ant = ant;
        this.searchTrailHandler = new SearchFoodTrailHandler();
    }

    /**
     * this methode is used to check whether the chosen Trail still the right one .
     *
     * @param  currentClearing the current Clearing
     * @param  targetTrail  the TargetTrail
     * @return true if the targetTrail still valid.
     */
     public boolean checkTrail(Clearing currentClearing , Trail targetTrail){
         return searchTrailHandler.checkTrail(currentClearing,targetTrail,ant);
     }



     /**
     * this methode is used to choose the right Trail according to the project description.
     *
     * @param  currentClearing the currentClearing
     * @return the targetTrail.
     */
     public Trail getTargetTrail(Clearing currentClearing){
         List<Trail> trailList = currentClearing.connectsTo();
         return searchTrailHandler.getTargetTrail(trailList,ant);
    }

    /**
     * this methode is used to get the valid Trail after stepping back because of the two special {d,e} cases.
     *
     * @param currentClearing  the current Clearing where the ant now stays.
     * @param lastWrongDeletedClearing  the last wrong visited Clearing which was deleted from the sequence.
     * @return  the Valid Trail to take.
     */
    public Trail GetTargetTrailAfterBackTracks(Clearing currentClearing, Clearing lastWrongDeletedClearing){
         List<Trail> trailList = currentClearing.connectsTo(); // list of all connected Trails
         for (int i = 0 ; i< trailList.size(); i++){ // delete the Trail which can take us to the deleted Clearing
             Trail t =  trailList.get(i);
             Clearing c = t.to();
            if (c.id() == lastWrongDeletedClearing.id()){
                trailList.remove(t);
            }
         }   // now do the search in this particular list.
         return searchTrailHandler.getTargetTrail(trailList,ant);
    }

    /**
     * this methode is used to check whether the Clearing has valid Connected Trail.
     *
     * @param c  Current Clearing.
     * @return   return true if you found a Trail.
     */
    public boolean checkTrail(Clearing c){
            List<Trail> connectedTrails = c.connectsTo();
      return   searchTrailHandler.checkTrail(c,connectedTrails,ant);
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
    public boolean CheckTrailAfterBackTracks(Clearing currentClearing,Clearing lastWrongDeletedClearing,Ant ant){
       return searchTrailHandler.CheckTrailAfterBackTracks(currentClearing,lastWrongDeletedClearing,ant);
    }


    /**
     * this methode is used to get the right reversedTrail wHen we Want to step back;
     * @param currentTwiceVisitedClearing   the currentTwiceVisitedClearing which we want to leave.
     * @param trailWeComeFrom           the Trail from which we come from the last time.
     * @return  the reversedTrail which we need to take
     */
    public Trail getTrailToStepBack(Clearing currentTwiceVisitedClearing,Trail trailWeComeFrom){
        assert (trailWeComeFrom.to().equals(currentTwiceVisitedClearing));
        return trailWeComeFrom.reverse();
    }


    /**
     *this methode will be used to handle the entering to a Trail according to the behavior of an Ant.
     *
     * @param c    The Clearing from which the Ant comes.
     * @param t    The Trail which the Ant is heading to.
     * @return     true when the Ant has entered the Trail successfully.
     * @throws InterruptedException
     */
    synchronized public boolean enterTrail(Clearing c, Trail t)throws InterruptedException {
        assert t  != null ;
        while (!t.isSpaceLeft()){  // if the Trail is not available , the Ant should wait.
            wait();
        }
        t.enter();  // enter the Trail
        ant.getRecorder().enter(ant,t);  // recorder stuff.
        c.leave(); // leave the Clearing
        ant.getRecorder().leave(ant,c); // recorder stuff
        if( c.id() != ant.getWorld().anthill().id()  ){ // if the left Clearing was not the hill->notifyAll.
            notifyAll();
        }
        // if the next Clearing was not in the sequence then update Hill-Pheromone. (no special cases)
        if (! ant.isInSequence(t.to())){
            // get the new Hill_Pheromone value
            int value = Math.min(t.anthill().value(),ant.getClearingSequence().size());
           com.pseuco.np21.shared.Trail.Pheromone newPheromone = com.pseuco.np21.shared.Trail.Pheromone.get(value);
            t.updateAnthill(newPheromone); // update the HIll-Pheromone.
        }
            //ToDO make this void.
        return true;
    }

    /**
     * this methode will be used to handle the entering to a Clearing according to the behavior of an Ant.
     *
     * @param t   The Trail from which the Ant comes.
     * @param c   The Clearing which the Ant is heading to .
     * @return    true if the Ant has entered the Clearing successfully
     * @throws InterruptedException
     */
    synchronized public boolean enterClearing(Trail t,Clearing c)throws InterruptedException {
        while (!c.isSpaceLeft()){
            wait(ant.disguise());
        }
        /*ToDo check whether the ant has left the wait(disguise) by having killed
            if so then terminate Now!!
           */
        //TODO handle entering
        return  true;
    }

    /**
     * this methode will be used to step back from an already visited clearing.
     * to the last Clearing from the sequence case d).
     *
     * @param c     The Clearing from which the Ant comes.
     * @param t     The Trail which the Ant is heading to .
     * @return      true if the Ant has entered the Trail successfully
     * @throws InterruptedException
     */
    synchronized public boolean immediateReturnToTrail(Clearing c, Trail t)throws InterruptedException {
        assert t  != null ;
        while (!t.isSpaceLeft()){
            wait();
        }
        t.enter();
        ant.getRecorder().enter(ant,t);
        c.leave();
        ant.getRecorder().leave(ant,c);
            notifyAll();
        ant.removeClearingFromSequence(c);
        //ToDO make this void.
        return true;
    }
    /**
     * this methode will be used to step back
     * from a clearing for which it has no options to reach food other than turning back.
     *
     * @param c     The Clearing from which the Ant comes.
     * @param t     The Trail which the Ant is heading to .
     * @return      true if the Ant has entered the Trail successfully
     * @throws InterruptedException
     */
    synchronized public boolean noFoodReturnToTrail(Clearing c, Trail t)throws InterruptedException {
        assert t  != null ;
        while (!t.isSpaceLeft()){
            wait();
        }
        t.enter();
        ant.getRecorder().enter(ant,t);
        c.leave();
        ant.getRecorder().leave(ant,c);
        notifyAll();
        ant.removeClearingFromSequence(c);
        //TODO set HillPheromone to Map.
        //ToDO make this void.
        return true;
    }


    /**
     * this methode will be used to step back after being in a Trail which is connected
     *  to an already visited clearing. case d)
     *
     * @param t    The Trail from which the Ant comes.
     * @param c    The Clearing which the Ant is heading to .
     * @return     true if the Ant has entered the Clearing successfully.
     * @throws InterruptedException
     */
    synchronized public boolean ImmediateBackTOClearing(Trail t,Clearing c)throws InterruptedException {
        while (!c.isSpaceLeft()){
            wait(ant.disguise());
        }
        /*ToDo check whether the ant has left the wait(disguise) by having killed
            if so then terminate Now!!
           */
        //TODO handle entering
        return  true;
    }

    /**
     * this methode will be used to step back
     * from a Trail which was connected to a clearing for which it has no options
     * to reach food other than turning back.
     *
     * @param t    The Trail from which the Ant comes.
     * @param c    The Clearing which the Ant is heading to .
     * @return     true if the Ant has entered the Clearing successfully.
     * @throws InterruptedException
     */
    synchronized public boolean noFoodBackTOClearing(Trail t,Clearing c)throws InterruptedException {
        while (!c.isSpaceLeft()){
            wait(ant.disguise());
        }
        /*ToDo check whether the ant has left the wait(disguise) by having killed
            if so then terminate Now!!
           */
        //TODO handle entering
        return  true;
    }

    /**
     * this methode will be used to pick up one piece of food.
     * @param c     The current Clearing from which the Ant will pick up the Food.
     * @return      true if the food was collected successfully.
     */
    synchronized public boolean pickUPFood(Clearing c){
        //TODO handle the process of picking up the food.
        return  false;
    }








}
