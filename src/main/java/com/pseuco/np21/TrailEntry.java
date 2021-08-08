package com.pseuco.np21;

public class TrailEntry {


    private final Trail trail ;

    /**
     * construct a TrailEntry.
     * @param trail trail.
     */
    public TrailEntry(Trail trail) {
        this.trail = trail;
    }

    /**
     *this methode will be used to handle the entering to a Trail according to the behavior of an Ant.
     *
     * @param c    The Clearing from which the Ant comes.
     * @param ant   the ant who want to enter this trail.
     * @return     true when the Ant has entered the Trail successfully.
     * @throws InterruptedException
     */
    synchronized public boolean enterTrailFoodSearch(Clearing c,Ant ant)throws InterruptedException {
        assert trail  != null ;
        while (!trail.isSpaceLeft()){  // if the Trail is not available , the Ant should wait.
            wait();
        }
        trail.enter();  // enter the Trail
        ant.getRecorder().enter(ant,trail);  // recorder stuff.
        c.leave(); // leave the Clearing
        ant.getRecorder().leave(ant,c); // recorder stuff
        if( c.id() != ant.getWorld().anthill().id()  ){ // if the left Clearing was not the hill->notifyAll.
            notifyAll();
        }
        if ( ! trail.food().isAPheromone()){  // if the Trail has Nap-Food-Pheromone then the ant is an Adventurer.
            ant.setAntTOAdventurer();
        }
        // if the next Clearing was not in the sequence then update Hill-Pheromone. (no special cases)
        if (! ant.isInSequence(trail.to())){
            // get the new Hill_Pheromone value
            int value = Math.min(trail.anthill().value(),ant.getClearingSequence().size());
            com.pseuco.np21.shared.Trail.Pheromone newPheromone = com.pseuco.np21.shared.Trail.Pheromone.get(value);
            trail.updateAnthill(newPheromone); // update the HIll-Pheromone.
            ant.getRecorder().updateAnthill(ant,trail,newPheromone); // recorder stuff.
        }
        //ToDO make this void.
        return true;
    }
    /**
     * this methode will be used to step back from an already visited clearing.
     * to the last Clearing from the sequence case d).
     *
     * @param c     The Clearing from which the Ant comes.
     * @param ant   the ant who want to enter this trail.
     * @return      true if the Ant has entered the Trail successfully
     * @throws InterruptedException
     */
    synchronized public boolean immediateReturnToTrail(Clearing c,Ant ant)throws InterruptedException {
        assert trail  != null ;
        while (!trail.isSpaceLeft()){ // wait for a free space.
            wait();
        }
        trail.enter();   // enter the trail.
        ant.getRecorder().enter(ant,trail); // recorder stuff
        c.leave();  // leave the wrong Clearing
        ant.getRecorder().leave(ant,c);  // recorder stuff
        notifyAll();  // notify all Ants that the Clearing has now a free space.
        ant.removeClearingFromSequence(c);  // remove this wrong Clearing from the sequence.
        //ToDO make this void.
        return true;
    }
    /**
     * this methode will be used to step back
     * from a clearing for which it has no options to reach food other than turning back.
     *
     * @param c     The Clearing from which the Ant comes.
     * @param ant   the ant who want to enter this trail.
     * @return      true if the Ant has entered the Trail successfully
     * @throws InterruptedException
     */
    synchronized public boolean noFoodReturnToTrail(Clearing c,Ant ant)throws InterruptedException {
        assert trail  != null ;
        while (!trail.isSpaceLeft()){
            wait();
        }
        trail.enter();   // enter the trail.
        ant.getRecorder().enter(ant,trail); // recorder stuff
        c.leave();  // leave the wrong Clearing
        ant.getRecorder().leave(ant,c);  // recorder stuff
        notifyAll();  // notify all Ants that the Clearing has now a free space.
        // remove this Clearing from the sequence.there are no Food to find in this way.
        ant.removeClearingFromSequence(c);
        //create a Map food-Pheromone .
        com.pseuco.np21.shared.Trail.Pheromone mapPheromone = com.pseuco.np21.shared.Trail.Pheromone.get(-1);
        //update the Food-Pheromone of the Trail to Map.
        trail.updateFood(mapPheromone,ant.isAdventurer());
        ant.getRecorder().updateFood(ant,trail,mapPheromone); // recorder stuff.
        //ToDO make this void.
        return true;
    }



    /**
     * handling how to enter a Trail t when the Ant want to go back hone.
     *
     * @param c The current Clearing
     * @param ant the ant who want to enter this Trail to go back Home.
     * @return true by successfully entering the trail.
     * @throws InterruptedException
     */

    public synchronized boolean homewardEnterTrail(Clearing c, Ant ant) throws InterruptedException {
        //TODO implement this
        return true;
    }




}
