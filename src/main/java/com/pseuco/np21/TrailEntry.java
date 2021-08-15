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

    public Lock TrailLock = new ReentrantLock();

    public Condition isSpaceLeft= TrailLock.newCondition();

    private final TrailEntryHandler handler ;


    /**
     * construct a TrailEntry.
     * @param trail trail.
     */
    public TrailEntry(Trail trail) {
        this.trail = trail;
       this.handler= new TrailEntryHandler();
    }



    public Lock getTrailLook(){
        return TrailLock;
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
        TrailLock.lock();
        try{
            while (!trail.isSpaceLeft())// if the Trail is not available , the Ant should wait.
                isSpaceLeft.await();

            c.getClearingEntry().clearingLock.lock(); // take the lock of the Clearing to send Signal
            try {
                if ( !ant.getWorld().isFoodLeft()|| Thread.currentThread().isInterrupted()) {
                    handler.LeaveTheClearing(c,ant);
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                    ant.getRecorder().despawn(ant,DespawnReason.TERMINATED);
                    throw new InterruptedException();
                } else{
                    handler.EnterTheTrail(trail, ant);
                    handler.LeaveTheClearing(c, ant);
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                    ant.TrailSequence.add(trail);
                }
            } finally {
                c.getClearingEntry().clearingLock.unlock();
            }
            com.pseuco.np21.shared.Trail.Pheromone p = trail.getOrUpdateFoodPheromone(false,null,false);
            if ( ! p.isAPheromone()){  // if the Trail has Nap-Food-Pheromone then the ant is an Adventurer.
                ant.setAntTOAdventurer();
            }
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
     * @throws InterruptedException InterruptedException
     */
    public boolean immediateReturnToTrail(Clearing c,Ant ant)throws InterruptedException {
        assert trail  != null ;
        TrailLock.lock();
        try{
            while (!trail.isSpaceLeft())// if the Trail is not available , the Ant should wait.
                isSpaceLeft.await();

            c.getClearingEntry().clearingLock.lock(); // take the lock of the Clearing to send Signal
            try {
                if ( !ant.getWorld().isFoodLeft() || Thread.currentThread().isInterrupted()) {
                    handler.LeaveTheClearing(c,ant);
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                    ant.getRecorder().despawn(ant,DespawnReason.TERMINATED);
                    throw new InterruptedException();
                } else{
                    handler.EnterTheTrail(trail, ant);
                    handler.LeaveTheClearing(c, ant);
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                }
            } finally {
                c.getClearingEntry().clearingLock.unlock();
            }
            // remove this wrong Clearing from the sequence.
            boolean removed = ant.getClearingSequence().remove(c);
            assert removed;
            removed =ant.TrailSequence.remove(trail.reverse());
            assert removed;
            ant.TrailsToVisitedClearing.put(trail.reverse().id(),trail.reverse());
        }finally {
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
     * @throws InterruptedException InterruptedException
     */
    public boolean noFoodReturnToTrail(Clearing c,Ant ant)throws InterruptedException {
        assert trail  != null ;
        TrailLock.lock();
        try{
            while (!trail.isSpaceLeft())// if the Trail is not available , the Ant should wait.
                isSpaceLeft.await();

            c.getClearingEntry().clearingLock.lock(); // take the lock of the Clearing to send Signal
            try {
                if ( !ant.getWorld().isFoodLeft() || Thread.currentThread().isInterrupted()) {
                    handler.LeaveTheClearing(c,ant);
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                    ant.getRecorder().despawn(ant,DespawnReason.TERMINATED);
                    throw new InterruptedException();
                } else{
                    handler.EnterTheTrail(trail, ant);
                    handler.LeaveTheClearing(c, ant);
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                    boolean removed = ant.getClearingSequence().remove(c);
                    boolean removedTrail = ant.TrailSequence.remove(trail.reverse());
                }
            } finally {
                c.getClearingEntry().clearingLock.unlock();
            }

        }finally {
            TrailLock.unlock();
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
        assert trail  != null ;
        TrailLock.lock();
        try{
            while (!trail.isSpaceLeft())// if the Trail is not available , the Ant should wait.
                isSpaceLeft.await();

            c.getClearingEntry().clearingLock.lock(); // take the lock of the Clearing to send Signal
            try {
                if ( !ant.getWorld().isFoodLeft()|| Thread.currentThread().isInterrupted()) {
                    handler.LeaveTheClearing(c,ant);
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }
                    ant.getRecorder().despawn(ant,DespawnReason.TERMINATED);
                    throw new InterruptedException();
                } else{
                    handler.EnterTheTrail(trail, ant);
                    handler.LeaveTheClearing(c, ant);
                    if (c.id() != ant.getWorld().anthill().id()) { // if the left Clearing was not the hill->signalAll.
                        c.getClearingEntry().isSpaceLeft.signalAll();
                    }

                }
            } finally {
                c.getClearingEntry().clearingLock.unlock();
            }
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
