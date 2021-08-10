package com.pseuco.np21;

import com.pseuco.np21.shared.Recorder.DespawnReason;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * this class can be seen as a gate for the clearing. each Clearing has one gate which is necessary
 * to ensure the concurrency
 */
public class ClearingEntry {
    private final Clearing clearing;

    public final Lock lock = new ReentrantLock();


    public Condition isSpaceLeft= lock.newCondition();


    /**
     * construct a clearingEntry
     * @param clearing clearing
     */
    public ClearingEntry(Clearing clearing) {
        this.clearing = clearing;
    }


    /**
     *  getter
     * @return  the condition
     */
    public Condition getIsSpaceLeft() {
        return isSpaceLeft;
    }

    /**
     * this methode will be used to handle the entering to a Clearing according to the behavior of an Ant.
     *
     * @param t   The Trail from which the Ant comes.
     * @param ant   The ant which want to enter this clearing .
     * @return    true if the Ant has entered the Clearing successfully, false when the ant died you
     * @throws InterruptedException InterruptedException
     */
    public boolean enterClearingFoodSearch(Trail t,Ant ant)throws InterruptedException {
        lock.lock();
        try{
            while (!clearing.isSpaceLeft())  // wait for space,,if the Ant has waited more than its disguise she can pass.
                if (!isSpaceLeft.await(ant.disguise(), TimeUnit.MILLISECONDS)) {
                    ant.getRecorder().attractAttention(ant); // added new
                    ant.getRecorder().despawn(ant, DespawnReason.DISCOVERED_AND_EATEN);
                    throw new InterruptedException();
                }

                clearing.enter(); // enter the Clearing
                ant.getRecorder().enter(ant,clearing); // recorder stuff.
                ant.addClearingToSequence(clearing); // add the Clearing to the Sequence.
                t.leave(); // leave the Trail.
                ant.getRecorder().leave(ant,t); // recorder stuff
                t.getTrailEntry().getIsSpaceLeft().signalAll();
               /* signal all the Ants to make sure that tha ant which is waiting to enter the Trail
                    //has been also notified */
            }finally {
                lock.unlock();
            }
        return  true;

    }

    /**
     * this methode will be used to step back after being in a Trail which is connected
     *  to an already visited clearing. case d). it does the same entering but we need to separate them to know
     *  which reason we will give to the Recorder.
     *
     * @param t    The Trail from which the Ant comes.
     * @param ant    The Ant which want to enter to this clearing .
     * @return     true if the Ant has entered the Clearing successfully.
     * @throws InterruptedException InterruptedException
     */
     public boolean immediateReturnTOClearing(Trail t,Ant ant)throws InterruptedException {
        return enterClearingFoodSearch(t,ant);
    }

    /**
     * this methode will be used to step back
     * from a Trail which was connected to a clearing for which it has no options
     * to reach food other than turning back. it does the same entering but we need to separate them to know
     * which reason we will give to the Recorder.
     *
     * @param t    The Trail from which the Ant comes.
     * @param ant    The Ant which want to enter to this clearing .
     * @return     true if the Ant has entered the Clearing successfully.
     * @throws InterruptedException InterruptedException
     */
     public boolean noFoodReturnTOClearing(Trail t,Ant ant)throws InterruptedException {
        return enterClearingFoodSearch(t,ant);
    }


    /**
     * this methode will be used to pick up one piece of food.
     * @param ant    The Ant which want to enter to this clearing .
     * @return      true if the food was collected successfully. if so you can start the homeward.
     */
     public boolean pickUPFood(Ant ant){
         lock.lock();
        try {
            if (!clearing.getOrSetFood(FoodInClearing.HAS_FOOD)) {  // check i there are no food exit with false.
                return false;
            }
            ant.setHoldFood(true);  // the Ant now has food
            clearing.getOrSetFood(FoodInClearing.PICKUP_FOOD); // remove the picked up food from this Clearing.
        } finally {
            lock.unlock();
        }
        return  true;
    }

    /**
     * this methode will be used to enter the Clearing when the Ant is willing to reach the Hill
     * @param t the current Trail which the Ant should leave.
     * @param ant  the Ant
     * @return true if the Ant has entered the Clearing successfully.
     */
     public boolean homewardEnterClearing(Trail t, Ant ant) throws InterruptedException{
       enterClearingFoodSearch(t,ant);
         return  true;
    }

    /**
     * drop the food in the Hill
     *
     * @param c the Hill
     * @return true by successfully dropping food
     */
    public boolean dropFood(Clearing c, Ant ant) {
        lock.unlock();
        try {
            if (c.id() == ant.getWorld().anthill().id()) {
                ant.getWorld().foodCollected();
                c.getOrSetFood(FoodInClearing.DROP_FOOD);
                ant.setHoldFood(false);
                return true;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    /**
     * this methode will be used to enter this Clearing in way that ensure concurrency.
     *
     * @param currentTrail  the Current Trail which the Ant should left,
     * @param ant       the Ant
     * @param entryReason   the reason you have to enter this Clearing.
     * @return      true if the entry was completed successfully.
     * @throws InterruptedException InterruptedException
     */
     public boolean enter(Trail currentTrail,Ant ant,EntryReason entryReason) throws InterruptedException {
        return switch (entryReason) {
            case FOOD_SEARCH -> this.enterClearingFoodSearch(currentTrail, ant);
            case IMMEDIATE_RETURN -> this.immediateReturnTOClearing(currentTrail, ant);
            case NO_FOOD_RETURN -> this.noFoodReturnTOClearing(currentTrail, ant);
            case HEADING_BACK_HOME -> this.homewardEnterClearing(currentTrail, ant);
        };
    }

}
