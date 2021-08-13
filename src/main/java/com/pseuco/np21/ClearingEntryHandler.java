package com.pseuco.np21;

public class ClearingEntryHandler {

    public void EnterTheClearing(Clearing clearing,Ant ant)  {
            clearing.enter(); // enter the Clearing
            ant.getRecorder().enter(ant, clearing); // recorder stuff.
    }

    public void LeaveTheTrail(Trail trail, Ant ant)  {
            trail.leave(); // leave the Trail.
            ant.getRecorder().leave(ant, trail); // recorder stuff
    }

    public void pheromonesUpdatingFoodSearch(Trail t , Ant ant) throws InterruptedException {

            com.pseuco.np21.shared.Trail.Pheromone hillPheromone = t.reverse().getOrUpdateHill(false, null);
            com.pseuco.np21.shared.Trail.Pheromone newPheromone;
            // if the  Clearing was not twice in the sequence then update Hill-Pheromone. (no special cases)
            if (!ant.TrailsToVisitedClearing.containsKey(t.id())) {
                // get the new Hill_Pheromone value
                if (hillPheromone.isAPheromone()) {
                    int w = ant.getClearingSequence().size() - 1;
                    int value = Math.min(hillPheromone.value(), w);
                    newPheromone = com.pseuco.np21.shared.Trail.Pheromone.get(value);
                } else {
                    int w = ant.getClearingSequence().size() - 1;
                    newPheromone = com.pseuco.np21.shared.Trail.Pheromone.get(w);
                }
                t.reverse().getOrUpdateHill(true, newPheromone); // update the HIll-Pheromone.
                ant.getRecorder().updateAnthill(ant, t.reverse(), newPheromone); // recorder stuff.
            } else { // don't update the Pheromone.
                ant.getRecorder().updateAnthill(ant, t.reverse(), hillPheromone);
            }// recorder stuff.
        }


        public void pheromonesUpdatingNoFoodReturn  (Trail t, Ant ant) throws InterruptedException {
                //create a Map food-Pheromone .
                com.pseuco.np21.shared.Trail.Pheromone mapPheromone = com.pseuco.np21.shared.Trail.Pheromone.get(-1);
                //update the Food-Pheromone of the Trail to Map.
                t.reverse().getOrUpdateFood(true, mapPheromone, ant.isAdventurer());
                ant.getRecorder().updateFood(ant, t.reverse(), mapPheromone); // recorder stuff.
            }



    public void pheromonesUpdatingHomeward  (Trail t,Clearing clearing, Ant ant, boolean update) throws InterruptedException {
            com.pseuco.np21.shared.Trail.Pheromone currentPheromone = t.reverse().getOrUpdateFood(false, null, false);
            com.pseuco.np21.shared.Trail.Pheromone newPheromone;
            if (update) {
                int currentClearingNumberFromTheSequence = 0; // get the index of the currentClearing from sequence.
                for (int i = 0; i < ant.getClearingSequence().size(); i++) {     // by looping the sequence
                    if (ant.getClearingSequence().get(i).id() != clearing.id()) {
                        currentClearingNumberFromTheSequence++;
                    } else {
                        break;
                    }
                }/*  sequence={A,B,Curr,C,D,Last} we want to update on Trail (curr->B)
                currIndexInSeq = 2; , LastIndexInSeq =size()-1 = 5
                5 - 2 = 3  ->> we have three Trails between the last und curr and the fourth is our Trail
                (1) Last->D, (2) D->C , now we must write (3) C->Curr,
                 SO size()=6 - (CurrIndex= 2) = 4 the right result is 3  so we do (-1)
                */
                int r = (ant.getClearingSequence().size() - 1) - currentClearingNumberFromTheSequence;
                //new added
                if (!ant.isAdventurer()) {
                    newPheromone = com.pseuco.np21.shared.Trail.Pheromone.get(r);
                } else {
                    if (currentPheromone.isAPheromone() && (!currentPheromone.isInfinite())) {
                        int minPheromoneValue = Math.min(r, currentPheromone.value());
                        newPheromone = com.pseuco.np21.shared.Trail.Pheromone.get(minPheromoneValue);
                    } else {
                        newPheromone = com.pseuco.np21.shared.Trail.Pheromone.get(r);
                    }
                }
                t.reverse().getOrUpdateFood(true, newPheromone, ant.isAdventurer()); // update the HIll-Pheromone.
                ant.getRecorder().updateFood(ant, t.reverse(), newPheromone); // recorder stuff
            }
    }





}
