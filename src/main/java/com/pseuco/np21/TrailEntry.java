package com.pseuco.np21;


import java.util.NoSuchElementException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * this class can be seen as a gate for the Trail. each Clearing has one gate which is necessary
 * to ensure the concurrency
 */

public class TrailEntry {


    private final Trail trail ;



    public Lock lock = new ReentrantLock();



    public Condition isSpaceLeft= lock.newCondition();

    /**
     * construct a TrailEntry.
     * @param trail trail.
     */
    public TrailEntry(Trail trail) {
        this.trail = trail;
    }



    /**
     *  getter
     * @return the condition.
     */
    public Condition getIsSpaceLeft() {
        return isSpaceLeft;
    }

    public Lock getLook(){
        return lock;
    }

    /**
     * this methode will be used to handle the entering to a Trail according to the behavior of an Ant.
     * if this methode returns false that means that this trail is not the best Trail anymore, so go get again the
     * new one
     *
     * @param c    The Clearing from which the Ant comes.
     * @param ant   the ant who want to enter this trail.
     * @return     true when the Ant has entered the Trail successfully.
     * @throws InterruptedException InterruptedException
     */
    public boolean enterTrailFoodSearch(Clearing c,Ant ant)throws InterruptedException {
        assert trail  != null ;
        lock.lock();
        try{
            while (!trail.isSpaceLeft())// if the Trail is not available , the Ant should wait.
                isSpaceLeft.await();

            trail.enter();  // enter the Trail
            ant.getRecorder().enter(ant,trail);  // recorder stuff.
            c.leave(); // leave the Clearing
            ant.getRecorder().leave(ant,c); // recorder stuff
            if( c.id() != ant.getWorld().anthill().id()  ){ // if the left Clearing was not the hill->notifyAll.
                c.getClearingEntry().getIsSpaceLeft().signalAll();
            }
            com.pseuco.np21.shared.Trail.Pheromone p = trail.getOrUpdateFood(false,null,false);
            if ( ! p.isAPheromone()){  // if the Trail has Nap-Food-Pheromone then the ant is an Adventurer.
                ant.setAntTOAdventurer();
            }

            if ( ant.isInSequence(trail.to())){
                ant.TrailsToVisitedClearing.put(trail.id(),trail);
                 }
            }
        finally {
            lock.unlock();
        }

        return true;
    }
    /**
     * this methode will be used to step back from an already visited clearing.
     * to the last Clearing from the sequence case d).
     *
     * @param c     The Clearing from which the Ant comes.
     * @param ant   the ant who want to enter this trail.
     * @return      true if the Ant has entered the Trail successfully
     * @throws InterruptedException InterruptedException
     */
    public boolean immediateReturnToTrail(Clearing c,Ant ant)throws InterruptedException {
        assert trail  != null ;
        lock.lock();
        try{
            while (!trail.isSpaceLeft())// wait for a free space.
                isSpaceLeft.await();
            trail.enter();
            ant.getRecorder().enter(ant,trail);
            c.leave();  // leave the wrong Clearing
            ant.getRecorder().leave(ant,c);
            c.getClearingEntry().getIsSpaceLeft().signalAll();  // signal all Ants that the Clearing has now a free space.
            // remove this wrong Clearing from the sequence.
            ant.removeClearingFromSequence(c);
        }finally {
            lock.unlock();
        }
        return true;
    }
    /**
     * this methode will be used to step back
     * from a clearing for which it has no options to reach food other than turning back.
     *
     * @param c     The Clearing from which the Ant comes.
     * @param ant   the ant who want to enter this trail.
     * @return      true if the Ant has entered the Trail successfully
     * @throws InterruptedException InterruptedException
     */
     public boolean noFoodReturnToTrail(Clearing c,Ant ant)throws InterruptedException {
        assert trail  != null ;
        lock.lock();
        try {
            while (!trail.isSpaceLeft())
            isSpaceLeft.await();
            trail.enter();
            ant.getRecorder().enter(ant,trail);
            c.leave();  // leave the wrong Clearing
            ant.getRecorder().leave(ant,c);
            c.getClearingEntry().getIsSpaceLeft().signalAll();  // notify all Ants that the Clearing has now a free space.
            // remove this Clearing from the sequence.there are no Food to find in this way.
            ant.removeClearingFromSequence(c);
        }finally {
            lock.unlock();
        }


        //ToDO make this void.
        return true;
    }



    /**
     * handling how to enter a Trail t when the Ant want to go back hone.
     *
     * @param c The current Clearing
     * @param ant the ant who want to enter this Trail to go back Home.
     * @return true by successfully entering the trail.
     * @throws InterruptedException InterruptedException
     */

    public  boolean homewardEnterTrail(Clearing c, Ant ant) throws InterruptedException {

        lock.lock();
        try{
            while (!trail.isSpaceLeft())
                isSpaceLeft.await();
            c.leave();
            ant.getRecorder().leave(ant, c);
            trail.enter();
            ant.getRecorder().enter(ant, trail);
            if (c.id()!= ant.getWorld().anthill().id()){
                c.getClearingEntry().getLock().lock();
                try{
                    c.getClearingEntry().getIsSpaceLeft().signalAll();
                }finally {
                    c.getClearingEntry().getLock().unlock();
                }
            }


        }finally {
            lock.unlock();
        }


        return true;
    }


    /*   (ignore this comment for now)!!!!!!
    if you are using this method to enter the Trail for FoodSearch you may get false as return.
     * that means that the Trail which the Ant is trying to enter is no more the best Trail. so by return false
     * you should get new Trail and try to enter again.
    * */
    /**
     * this methode will be used to enter this Trail in way that ensure concurrency.
     *
     *
     * @param currentClearing  the Current Clearing which the Ant should left,
     * @param ant       the Ant
     * @param entryReason   the reason you have to enter this Trail.
     * @return      true if the entry was completed successfully.
     * @throws InterruptedException InterruptedException
     */
     public boolean enter (Clearing currentClearing,Ant ant,EntryReason entryReason) throws InterruptedException {
        return switch (entryReason) {
            case FOOD_SEARCH -> this.enterTrailFoodSearch(currentClearing,ant);
            case IMMEDIATE_RETURN -> this.immediateReturnToTrail(currentClearing,ant);
            case NO_FOOD_RETURN -> this.noFoodReturnToTrail(currentClearing,ant);
            case HEADING_BACK_HOME -> this.homewardEnterTrail(currentClearing , ant);
        };
    }



}
