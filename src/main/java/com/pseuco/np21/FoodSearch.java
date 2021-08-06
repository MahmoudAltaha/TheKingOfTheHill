package com.pseuco.np21;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class FoodSearch {

    private final Ant ant;

    /**
     * constructor for the Class
     * @param ant
     */
    public FoodSearch(Ant ant) {
        this.ant = ant;
    }

    /**
     * this methode is used to check whether the chosen Trail still the right one .
     *
     * @param  currentCLearing
     * @param  targetTrail
     * @return true if the targetTrail still valid.
     */
    public boolean checkTrail(Clearing currentCLearing, Trail targetTrail){
        //TODO complete this
        return false;
    }

        public boolean isInSequence(Clearing c){
        if(ant.getClearingSequence().contains(c)){
            return true;
        }
        return false;
        }
    /**
     * this methode is used to choose the right Trail according to the project description.
     *
     * @param  currentClearing
     * @return the targetTrail.
     */
    public Trail getTargetTrail(Clearing currentClearing){
        //TODO complete this
        //list of all connected Trails
         List<Trail> trailList = currentClearing.connectsTo();
        // remove all Trail which leads to Clearing that are already in the sequence.
        for (int i = 0 ; i < trailList.size(); i++ ){
            Trail t =  trailList.get(i);
            if ( isInSequence(t.to()) ){
                trailList.remove(i);
            }
        }
       boolean allNaP = true;  // check if all Trails has FoodPheromone = Nap
        for (int i = 0 ; i <trailList.size(); i++) {
             Trail t = trailList.get(i);
            if(! (t.food().isAPheromone()) ){
                allNaP = false;
            };
        }
        Random random = new Random();
        if (allNaP){  // if so then pick a Trail randomly .
            int index = random.nextInt(trailList.size());
            Trail target = trailList.get(index);
            return target;
        }
       // Trail t = trailList.stream().min(Comparator.comparing(Trail::food)).get();


        return null;
    }

    /**
     * this methode is used to check whether the Clearing has a Connected Trail.
     * @param c  Current Clearing.
     * @return   return true if you found a Trail.
     */
    public boolean checkTrail(Clearing c){
        //TODO complete this
        return true;
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
        ant.getRecorder().enter(ant,c);


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
     * this methode will be used to step back to the last Clearing from the sequence .
     *
     * @param c     The Clearing from which the Ant comes.
     * @param t     The Trail which the Ant is heading to .
     * @return      true if the Ant has entered the Trail successfully
     * @throws InterruptedException
     */
    synchronized public boolean backToTrail(Clearing c, Trail t)throws InterruptedException {
        assert t  != null ;
        while (!t.isSpaceLeft()){
            wait();
        }
        //TODO handle entering
        return false;
    }


    /**
     * this methode will be used to step back to the last Clearing from the sequence .
     *
     * @param t    The Trail from which the Ant comes.
     * @param c    The Clearing which the Ant is heading to .
     * @return     true if the Ant has entered the Clearing successfully.
     * @throws InterruptedException
     */
    synchronized public boolean backTOClearing(Trail t,Clearing c)throws InterruptedException {
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
