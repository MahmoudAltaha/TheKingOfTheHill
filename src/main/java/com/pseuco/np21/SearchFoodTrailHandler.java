package com.pseuco.np21;

import java.util.*;

public class SearchFoodTrailHandler {


    /**   !!!ignore this methode for now.!!!
     * this methode is used to check whether the chosen Trail still the right one .
     *
     * @param  currentClearing the current Clearing
     * @param  targetTrail  the TargetTrail
     * @return true if the targetTrail still valid.
     */
    public boolean checkTrail(Clearing currentClearing , Trail targetTrail,Ant ant){
        List<Trail> trailList = currentClearing.connectsTo();
        Trail t = getTargetTrail(trailList,ant);
        return t.id() == targetTrail.id();
    }


    /**
     * this method used intern to compare the pheromone of two Trails and to add the min one to the list.
     *you need to read this methode with the loop to understand what she do
     * @param t1 first Trail
     * @param t2 second Trail
     * @param minTrails list of min Trails.
     */
    private void compareTrails(Trail t1, Trail t2, List<Trail> minTrails){
        com.pseuco.np21.shared.Trail.Pheromone p1 = t1.getOrUpdateFood(false,null,false);
        com.pseuco.np21.shared.Trail.Pheromone p2 = t2.getOrUpdateFood(false,null,false);
        if ( p1.value() > p2.value()){
            minTrails.add(t2);
            // remove t1 if it is present and all the Trails with the same food-Value, if not nothing is happening
            for (int i = 0; i < minTrails.size(); i++) {
                if (minTrails.get(i).getOrUpdateHill(false, null).value() ==
                        t1.getOrUpdateFood(false, null,false).value()) {
                    minTrails.remove(minTrails.get(i));
                }
            }
        }else if (p1.value() < p2.value()){
            if (! minTrails.contains(t1)) {
                minTrails.add(t1);
            }
        } else {
            if(!minTrails.contains(t1)){
                minTrails.add(t1);
            }
            minTrails.add(t2);
        }
    }

    /**
     * this methode used to remove the Trails which lead to the Clearing that i just visited and found that i
     * have visited it in tha past.
     *
     * @param trailsList the Trails which are connected to the Current Clearing
     * @param ant the Ant
     */
    private void removeTrailsThatConnectsToVisitedClearing(List<Trail> trailsList, Ant ant){
        Map<Integer, Trail > trailsToVisitedClearings = ant.TrailsToVisetedClearing;
        for (int i = 0 ; i< trailsList.size() ; i++){
            int trailsID = trailsList.get(i).id();
                if(trailsToVisitedClearings.containsKey(trailsID) ){
                trailsList.remove(trailsList.get(i));
            }
        }
    }

    /**
     * this methode remove all Trails with Map-Pheromone from the given list.
     * @param trailList trailsList
     * @param ant ant
     */
    private void removeMapTrailsFromTheList(List<Trail> trailList,Ant ant){
        assert (!trailList.isEmpty());  // check if the list are not Empty
        for (int i = 0; i < trailList.size(); i++) {
            Trail t = trailList.get(i);
            // remove all Trails which are already has Map Value Or the last one in Sequence.
            com.pseuco.np21.shared.Trail.Pheromone p = t.getOrUpdateFood(false,null,false);
            if (ant.isSecondLastVisitedInSequence(t.to()) || p.isInfinite()) {
                trailList.remove(t);
            }
        }
    }

    /**
     *  this methode checks if all the Trails in the list has Nap-Pheromones.
     *
     * @param trailList trailList
     * @return true if all the Trails in the list has Nap-Pheromones.
     */
    private boolean checkIfAllTrailsHasNaP(List<Trail> trailList){
        assert (!trailList.isEmpty());  // check if the list are not Empty
        for (Trail t : trailList) {
            com.pseuco.np21.shared.Trail.Pheromone p = t.getOrUpdateFood(false,null,false);
            if (!(p.isAPheromone())) {
                return false;
            }
        }
        return true;
    }

    /**
     * this methode separate one list into two Lists, one of them has all Nap_trails and the other one
     * has all Non-NapTrails.
     * @param trailList trailList
     * @param napTrails NapList
     * @param nonNapTrails NON-Nap List
     */
    private void separateNapTrailsFromNonNapTrails(List<Trail> trailList,List<Trail> napTrails,List<Trail> nonNapTrails){
        assert (!trailList.isEmpty());  // check if the list are not Empty
        for (Trail trail : trailList) {
            com.pseuco.np21.shared.Trail.Pheromone p = trail.getOrUpdateFood(false,null,false);
            int valueToCheck = p.value(); // pheromone of the Trail.
            if (valueToCheck == -2) {  // check if the Trail has Nap food-Ph.
                napTrails.add(trail); // if so add it to the JustNap list.
            } else {
                nonNapTrails.add(trail); //otherwise add it to the NonNap list.
            }
        }
    }

    /**
     * this methode is used to choose the right Trail according to the project description.
     *
     * @param trailList the Trails from which we the right one choose.
     * @param ant       Ant.
     * @return      The Target Trail.
     */
    public Trail getTargetTrail(List<Trail> trailList,Ant ant){
        assert (!trailList.isEmpty());  // check if the list are not Empty
        removeTrailsThatConnectsToVisitedClearing(trailList,ant); // remove the Trails That Connect To Visited Clearing.
        removeMapTrailsFromTheList(trailList,ant); //remove Map Trails From The List.
        Trail targetTrail;
        boolean allNaP;  // check if all Trails has FoodPheromone = Nap
        allNaP = checkIfAllTrailsHasNaP(trailList);
        Random random = new Random();
        if (allNaP){  // if so then pick a NaP Trail randomly .
            int index = random.nextInt(trailList.size());
            targetTrail = trailList.get(index);
            targetTrail.setSelectionReason(2);  // update the SelectionReason in the Trail.
        }
        else {  // the trailList has Trails with non Nap-foodPheromone. it may also have Trails with Nap-ph tho.
            List<Trail> trailsListNonNap = new ArrayList<>(); // list with Trails which has non Nap-Food-ph.
            List<Trail> trailsListWithJustNap = new ArrayList<>();// list with Trails which has Nap-food-ph.
            separateNapTrailsFromNonNapTrails(trailList,trailsListWithJustNap,trailsListNonNap);
            List<Trail> minTrails = new ArrayList<>(); // list which should contains the min-NonNap Pheromones.
            for (int i = 0; i < (trailsListNonNap.size() - 1); i++) {  // compare the NonNap-Pheromones and add the min-ones to the list.
                Trail t1 = trailsListNonNap.get(i);
                Trail t2 = trailsListNonNap.get(i + 1);
                compareTrails(t1, t2, minTrails);
            }
            int randomIndex = random.nextInt(trailsListNonNap.size());//get random number
            Trail suggestedTrail = trailsListNonNap.get(randomIndex);// the randomly picked Trail which has the min Ph.
            // check if the impatience of the Ant are smaller than the min pheromone. if so get a random Nap-food-ph Trail.
            com.pseuco.np21.shared.Trail.Pheromone p = suggestedTrail.getOrUpdateFood(false,null,false);
            if (ant.impatience() < p.value() && !trailsListWithJustNap.isEmpty()){
                int randomNapIndex = random.nextInt(trailsListWithJustNap.size());
                targetTrail = trailsListWithJustNap.get(randomNapIndex);
                targetTrail.setSelectionReason(2); // update the SelectionReason in the Trail.
            } else{
                targetTrail = suggestedTrail; /* otherwise take that randomly picked Trail with NonNap-food-pheromone
                as a target Trail */
                targetTrail.setSelectionReason(1); // update the SelectionReason in the Trail.
            }
        }
        return targetTrail;
    }

    /**
     * this methode is used to check whether the Clearing has a Connected Trail.
     *
     * @param c  Current Clearing.
     * @param connectedTrails the Trails that are connected to the currentClearing c.
     * @return   return true if you found a Trail.
     */
    public boolean checkTrail(Clearing c,List<Trail> connectedTrails,Ant ant){
        if (connectedTrails.isEmpty()){
            return false;
        }
        removeTrailsThatConnectsToVisitedClearing(connectedTrails,ant);
        // if the Clearing is the Hill and has one single Trail which its Food not MaP return true
        if (connectedTrails.size() == 1 && c.id() == ant.getWorld().anthill().id() ) {
            com.pseuco.np21.shared.Trail.Pheromone p = connectedTrails.get(0).getOrUpdateFood(false,null,false);
            return !p.isInfinite();
        }  // Hill or normal Clearing with more than one Trail( the one from which the Ant has reached this Clearing)
        if (connectedTrails.size() > 1 ){
            List<Trail> TrailWithoutMaP = new ArrayList<>(); // list of non-Map-food-pheromone Trails
            for (Trail t : connectedTrails) {
                com.pseuco.np21.shared.Trail.Pheromone p = t.getOrUpdateFood(false,null,false);
                if (! p.isInfinite()) {
                    TrailWithoutMaP.add(t);
                }
            }
            // if the number of Trails with (non-Map-Food-ph.)
            // bigger than 1( cause there is always the one from which we come) return true
            return TrailWithoutMaP.size() > 1;
        }
        return false;
    }


}
