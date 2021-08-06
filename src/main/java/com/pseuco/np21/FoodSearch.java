package com.pseuco.np21;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FoodSearch {

    private final Ant ant;

    /**
     * constructor for the Class
     * @param ant the Ant
     */
    public FoodSearch(Ant ant) {
        this.ant = ant;
    }

    /**
     * this methode is used to check whether the chosen Trail still the right one .
     *
     * @param  currentCLearing the current Clearing
     * @param  targetTrail  the TargetTrail
     * @return true if the targetTrail still valid.
     */
     public boolean checkTrail(Clearing currentCLearing, Trail targetTrail){
         Trail t = getTargetTrail(currentCLearing);
         return t.equals(targetTrail);
     }



    /**
     * this method used intern to compare the pheromone of two Trails and to add the min one to the list.
     *
     * @param t1 first Trail
     * @param t2 second Trail
     * @param minTrails list of min Trails.
     */
        private void compareTrails(Trail t1, Trail t2, List<Trail> minTrails){
            com.pseuco.np21.shared.Trail.Pheromone p1 = t1.food();
            com.pseuco.np21.shared.Trail.Pheromone p2 = t2.food();
            if ( p1.value() > p2.value()){
                minTrails.add(t2);
                if (minTrails.contains(t1)){
                    minTrails.remove(t1);
                }
            }else if (p1.value() < p2.value()){
                minTrails.add(t1);
                if (minTrails.contains(t2)){
                    minTrails.remove(t2);
                }
            } else {
                minTrails.add(t1);
                minTrails.add(t2);
            }
        }

     /**
     * this methode is used to choose the right Trail according to the project description.
     *
     * @param  currentClearing the currentClearing
     * @return the targetTrail.
     */
     public Trail getTargetTrail(Clearing currentClearing){
        //list of all connected Trails
         List<Trail> trailList = currentClearing.connectsTo();
        // remove all Trails which leads to Clearings that are already in the sequence.
        for (int i = 0 ; i < trailList.size(); i++ ){
            Trail t =  trailList.get(i);
            if (ant.isInSequence(t.to()) ){
                trailList.remove(i);
            }
        }
         Trail targetTrail;
       boolean allNaP = true;  // check if all Trails has FoodPheromone = Nap
        for (int i = 0 ; i <trailList.size(); i++) {
             Trail t = trailList.get(i);
            if(! (t.food().isAPheromone()) ){
                allNaP = false;
            }
        }
        Random random = new Random();
        if (allNaP){  // if so then pick a Trail randomly .
            int index = random.nextInt(trailList.size());
            targetTrail = trailList.get(index);
        }
        else {  // the trailList has Trails with non Nap-foodPheromone. it may also have Trails with Nap-ph tho.
            int size = trailList.size();  // get the size of the list
            List<Trail> trailsListNonNap = new ArrayList<>(); // list with Trails which has non Nap-Food-ph.
            List<Trail> trailsListWithJustNap = new ArrayList<>();// list with Trails which has Nap-food-ph.
            for (int i = 0; i < size; i++) {
                int valueToCheck = trailList.get(i).food().value(); // pheromone of the Trail.
                if (valueToCheck == -2) {  // check if the Trail has Nap food-Ph.
                    trailsListWithJustNap.add(trailList.get(i)); // if so add it to the JustNap list.
                } else {
                    trailsListNonNap.add(trailList.get(i)); //otherwise add it to the NonNap list.
                }
            }
            List<Trail> minTrails = new ArrayList<>(); // list which should contains the min-NonNap Pheromones.
            for (int i = 0; i < (size - 1); i++) {  // compare the NonNap-Pheromones and add it to the list.
                Trail t1 = trailsListNonNap.get(i);
                Trail t2 = trailsListNonNap.get(i + 1);
                compareTrails(t1, t2, minTrails);
            }
            int randomIndex = random.nextInt(trailsListNonNap.size());//get random number
            Trail suggestedTrail = trailsListNonNap.get(randomIndex);// the randomly picked Trail which has the min Ph.
            // check if the impatience of the Ant are smaller than the min pheromone. if so get a random Nap-food-ph Trail.
            if (ant.impatience() < suggestedTrail.food().value() && !trailsListWithJustNap.isEmpty()){
                int randomNapIndex = random.nextInt(trailsListWithJustNap.size());
                targetTrail = trailsListWithJustNap.get(randomNapIndex);
            } else{
                targetTrail = suggestedTrail; /* otherwise take that randomly picked Trail with NonNap-food-pheromone
                as a target Trail */
            }
        }
        return targetTrail;
    }

    /**
     * this methode is used to check whether the Clearing has a Connected Trail.
     *
     * @param c  Current Clearing.
     * @return   return true if you found a Trail.
     */
    public boolean checkTrail(Clearing c){
        List<Trail> connectedTrails = c.connectsTo();
        // if the Clearing is the Hill and has one single Trail Or the Clearing is normal and has more than 1
        if (connectedTrails.size() > 1 ||( connectedTrails.size() == 1 && c.equals(ant.getWorld().anthill())) ) {
            return true;
        }
        return false;
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
