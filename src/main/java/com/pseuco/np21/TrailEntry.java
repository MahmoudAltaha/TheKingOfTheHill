package com.pseuco.np21;


import com.pseuco.np21.shared.Recorder.DespawnReason;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * this class can be seen as a gate for the Trail. each Clearing has one gate which is necessary
 * to ensure the concurrency
 */

public class TrailEntry {


    private final Trail trail ;
    private Lock TrailLock = new ReentrantLock();
    private Condition isSpaceLeft= TrailLock.newCondition();

    /**
     * construct a TrailEntry.
     * @param trail trail.
     */
    public TrailEntry(Trail trail) {
        this.trail = trail;
    }

    public Lock getTrailLook(){
        return TrailLock;
    }
    public Condition getIsSpaceLeft() { return isSpaceLeft; }


    /**
     * this methode will be used to handle the entering to a Trail according to the behavior of an Ant.
     * if this methode returns false that means that this trail is not the best Trail anymore, so go get again the
     * new one
     *
     * @param c    The Clearing from which the Ant comes.
     * @param ant   the ant who want to enter this trail.
     * @return     true when the Ant has entered the Trail successfully.
     */
    public boolean enterTrailFoodSearch(Clearing c,Ant ant) {
        assert trail  != null ;
        TrailLock.lock();
        try{
            while (!trail.isSpaceLeft())// if the Trail is not available , the Ant should wait.
                isSpaceLeft.await();
                c.getClearingEntry().clearingLock.lock(); // take the lock of the Clearing to send Signal
            try {
                if ( !ant.getWorld().isFoodLeft() || Thread.currentThread().isInterrupted()) {
                    c.leave();
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                   return false;
                } else{
                    trail.enter();
                    c.leave();
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                    ant.TrailSequence.add(trail); // the TrailsPath with FoodSearch Entering
                    ant.alreadyEnteredTrails.put(trail.id(),trail);
                }
            } finally {
                c.getClearingEntry().clearingLock.unlock();
            }
            com.pseuco.np21.shared.Trail.Pheromone p = trail.getOrUpdateFoodPheromone(false,null,false);
        }
        catch (InterruptedException e){ // Thread interrupted while he is in wait mode.
            c.leave();
            if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                c.getClearingEntry().isSpaceLeft.signalAll();
            } return false;
        }
        finally {
            TrailLock.unlock();
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
     */
    public boolean immediateReturnToTrail(Clearing c,Ant ant) {
        assert trail  != null ;
        TrailLock.lock();
        try{
            while (!trail.isSpaceLeft())// if the Trail is not available , the Ant should wait.
                isSpaceLeft.await();

            c.getClearingEntry().clearingLock.lock(); // take the lock of the Clearing to send Signal
            try {
                if ( !ant.getWorld().isFoodLeft()  || Thread.currentThread().isInterrupted()) {
                    c.leave();
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                    return false;
                } else{
                    trail.enter();
                    c.leave();
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                }
            } finally {
                c.getClearingEntry().clearingLock.unlock();
            }
            // remove this wrong Clearing from the sequence.
            int sizeClearingSequence = ant.getClearingSequence().size();
            Clearing removedClearing = ant.getClearingSequence().remove(sizeClearingSequence-1);
            assert ant.getClearingSequence().size()< sizeClearingSequence;

            int sizeTrailSequence = ant.TrailSequence.size();
            Trail removedTrail = ant.TrailSequence.remove(sizeTrailSequence-1);
            assert ant.TrailSequence.size()<sizeTrailSequence;

            ant.alreadyEnteredTrails.put(trail.id(),trail);
        } catch (InterruptedException e){ // Thread interrupted while he is in wait mode.
            c.leave();
            if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                c.getClearingEntry().isSpaceLeft.signalAll();
            }
            return  false;
        }
        finally {
            TrailLock.unlock();
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
     */
    public boolean noFoodReturnToTrail(Clearing c,Ant ant){
        assert trail  != null ;
        TrailLock.lock();
        try{
            while (!trail.isSpaceLeft())// if the Trail is not available , the Ant should wait.
                isSpaceLeft.await();

            c.getClearingEntry().clearingLock.lock(); // take the lock of the Clearing to send Signal
            try {
                if ( !ant.getWorld().isFoodLeft()  || Thread.currentThread().isInterrupted() ) {
                    c.leave();
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                    return false;
                } else{
                    trail.enter();;
                    c.leave();
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                    int sizeClearingSequence = ant.getClearingSequence().size();
                    Clearing removedClearing = ant.getClearingSequence().remove(sizeClearingSequence-1);
                    assert ant.getClearingSequence().size()< sizeClearingSequence;

                    int sizeTrailSequence = ant.TrailSequence.size();
                    Trail removedTrail = ant.TrailSequence.remove(sizeTrailSequence-1);
                    assert ant.TrailSequence.size()<sizeTrailSequence;

                    ant.alreadyEnteredTrails.put(trail.id(),trail);
                }
            } finally {
                c.getClearingEntry().clearingLock.unlock();
            }
         } catch (InterruptedException e){ // Thread interrupted while he is in wait mode.
            c.leave();
            if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                c.getClearingEntry().isSpaceLeft.signalAll();
            } return false;
        }
        finally {
            TrailLock.unlock();
        }
        return true;
    }



    /**
     * handling how to enter a Trail t when the Ant want to go back hone.
     *
     * @param c The current Clearing
     * @param ant the ant who want to enter this Trail to go back Home.
     * @return true by successfully entering the trail.
     */

    public  boolean homewardEnterTrail(Clearing c, Ant ant) {
        assert trail  != null ;
        TrailLock.lock();
        try{
            while (!trail.isSpaceLeft())// if the Trail is not available , the Ant should wait.
                isSpaceLeft.await();

            c.getClearingEntry().clearingLock.lock(); // take the lock of the Clearing to send Signal
            try {
                if ( !ant.getWorld().isFoodLeft()  || Thread.currentThread().isInterrupted()) {
                    c.leave();
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                    return false;
                } else{
                   trail.enter();
                    c.leave();
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                }
            } finally {
                c.getClearingEntry().clearingLock.unlock();
            }
        } catch (InterruptedException e){ // Thread interrupted while he is in wait mode.
            c.leave();
            if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                c.getClearingEntry().isSpaceLeft.signalAll();
            } return false;
        }  finally {
            TrailLock.unlock();
        }
        return true;

    }


    /**
     * this methode will be used to enter this Trail in way that ensure concurrency.
     *
     *
     * @param currentClearing  the Current Clearing which the Ant should left,
     * @param ant       the Ant
     * @param entryReason   the reason you have to enter this Trail.
     * @return      true if the entry was completed successfully.
     */
    public boolean enter (Clearing currentClearing,Ant ant,EntryReason entryReason) {
        return switch (entryReason) {
            case FOOD_SEARCH -> this.enterTrailFoodSearch(currentClearing,ant);
            case IMMEDIATE_RETURN -> this.immediateReturnToTrail(currentClearing,ant);
            case NO_FOOD_RETURN -> this.noFoodReturnToTrail(currentClearing,ant);
            case HEADING_BACK_HOME_WITH_UPDATING, HEADING_BACK_HOME_WITHOUT_UPDATING -> this.homewardEnterTrail(currentClearing , ant);
        };
    }



}
