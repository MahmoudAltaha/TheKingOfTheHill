package com.pseuco.np21;

/**
 * this class can be seen as a gate for the clearing. each Clearing has one gate which is necessary
 * to ensure the concurrency
 */
public class ClearingEntry {
    private final Clearing clearing;

    /**
     * construct a clearingEntry
     * @param clearing clearing
     */
    public ClearingEntry(Clearing clearing) {
        this.clearing = clearing;
    }

    /**
     * this methode will be used to handle the entering to a Clearing according to the behavior of an Ant.
     *
     * @param t   The Trail from which the Ant comes.
     * @param ant   The ant which want to enter this clearing .
     * @return    true if the Ant has entered the Clearing successfully, false when the ant died you
     * @throws InterruptedException
     */
    synchronized public boolean enterClearingFoodSearch(Trail t,Ant ant)throws InterruptedException {
        while (!clearing.isSpaceLeft()){  // wait for space,,if the Ant has waited more than its disguise she can pass.
            wait(ant.disguise());
        }
        // check how the Ant has left wait()
        if (!clearing.isSpaceLeft()){  // if there is
            ant.getRecorder().attractAttention(ant); // recorder stuff.
            ant.setHoldFood(false); // delete any food if the ant was holding food.
            return false;  // the ant is about to die.
        }
        clearing.enter(); // enter the Clearing
        ant.getRecorder().enter(ant,clearing); // recorder stuff.
        ant.addClearingToSequence(clearing); // add the Clearing to the Sequence.
        t.leave(); // leave the Trail.
        ant.getRecorder().leave(ant,t); // recorder stuff
        notifyAll(); /* notify all the Ants to make sure that tha ant which is waiting to enter the Trail
                    has been also notified */
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
     * @throws InterruptedException
     */
    synchronized public boolean immediateReturnTOClearing(Trail t,Ant ant)throws InterruptedException {
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
     * @throws InterruptedException
     */
    synchronized public boolean noFoodReturnTOClearing(Trail t,Ant ant)throws InterruptedException {
        return enterClearingFoodSearch(t,ant);
    }


    /**
     * this methode will be used to pick up one piece of food.
     * @param ant    The Ant which want to enter to this clearing .
     * @return      true if the food was collected successfully. if so you can start the homeward.
     */
    synchronized public boolean pickUPFood(Ant ant){
        if (! clearing.hasFood()){  // check i there are no food exit with false.
            return  false;
        }
        ant.setHoldFood(true);  // the Ant now has food
        clearing.pickupFood(); // remove the picked up food from this Clearing.
        return  true;
    }

    /**
     * this methode will be used to enter the Clearing when the Ant is willing to reach the Hill
     * @param currentTrail the current Trail which the Ant should leave.
     * @param ant  the Ant
     * @return true if the Ant has entered the Clearing successfully.
     */
    synchronized public boolean homewardEnterClearing(Trail currentTrail, Ant ant){
        //TODO implement this.
        return false;
    }

    /**
     * drop the food in the Hill
     *
     * @param c the Hill
     * @return true by successfully dropping food
     */
    public synchronized boolean dropFood(Clearing c) {
        //TODO implement this
        return true;
    }


}
