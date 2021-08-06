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
    public boolean specialCheckTrail(Clearing currentClearing,Clearing lastWrongDeletedClearing,Ant ant){
       return searchTrailHandler.specialCheckTrail(currentClearing,lastWrongDeletedClearing,ant);
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
        while (!t.isSpaceLeft()){
            wait();
        }
        t.enter();
        ant.getRecorder().enter(ant,t);
        c.leave();
        ant.getRecorder().leave(ant,c);
        if(! c.equals(ant.getWorld().anthill() ) ){ // if the left Clearing was not the hill->notifyAll.
            notifyAll();
        }
        if (! ant.isInSequence(t.to())){ // if the next Clearing was not in the sequence then update Hill-Pheromone.
            int value = Math.min(t.anthill().value(),ant.getClearingSequence().size());
                //TODO update Hill Pheromone.
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
     * this methode will be userd
     * @param c     The current Clearing from which the Ant will pick up the Food.
     * @return      true if the food was collected successfully.
     */
    synchronized public boolean pickUPFood(Clearing c){
        //TODO handle the process of picking up the food.
        return  false;
    }








}
