package com.pseuco.np21;

import com.pseuco.np21.shared.Recorder;
import com.pseuco.np21.shared.World;

import java.util.List;

public class AntRunHandler {

        private final Ant  ant;

    public AntRunHandler(Ant ant) {
        this.ant = ant;
    }


    public Clearing goOneStepBackWithImmediatReturn(Trail ourTrail,Clearing position) throws InterruptedException {
        SearchFoodPathCheck searchFood = ant.searchFood;
        Recorder recorder = ant.getRecorder();
        Trail targetTrail = searchFood.getTrailToStepBack(position, ourTrail); // get the reverse of ourTrail
        recorder.select(ant, targetTrail, position.connectsTo(), Recorder.SelectionReason.IMMEDIATE_RETURN);
        targetTrail.enterTrail(position, ant, EntryReason.IMMEDIATE_RETURN); // enter the trail
        ourTrail = targetTrail; // update our Trail
        Clearing ourNextClearing = ourTrail.to(); // get our next Clearing
        ourNextClearing.enterClearing(ourTrail, ant, EntryReason.IMMEDIATE_RETURN, false); // enter the Clearing
        position = ourNextClearing; // update the Ant Position(current Clearing);
        return position;
    }

    public Clearing GoBackByNoFoodReturn(SearchFoodPathCheck searchFood, Clearing currentPosition) throws InterruptedException {
        Clearing position = currentPosition;
        Recorder recorder = ant.getRecorder();
        World<Clearing, Trail> world = ant.getWorld();
        List<Clearing> sequence = ant.getClearingSequence();
        // as long as we don't find a Clearing with undiscovered Trail we go with no Food Return.
            Trail targetTrail = searchFood.getTrailByNofoodReturn(position, ant);
            recorder.select(ant, targetTrail, position.connectsTo(), Recorder.SelectionReason.NO_FOOD_RETURN);
            targetTrail.enterTrail(position, ant, EntryReason.NO_FOOD_RETURN);
            Clearing ourNextClearing = targetTrail.to();
            ourNextClearing.enterClearing(targetTrail, ant, EntryReason.NO_FOOD_RETURN, false);
            position = ourNextClearing;
            return  position;
    }

    public void leaveAndInterrupt(Ant ant,Clearing position) throws InterruptedException {
        position.leave();
        ant.getRecorder().leave(ant, position);
        ant.getRecorder().despawn(ant, Recorder.DespawnReason.TERMINATED);
        throw new InterruptedException();
    }


    public void homewardMoving ( boolean update, Clearing currentPosition,List<Clearing> sequence) throws InterruptedException {
        World<Clearing, Trail> world = ant.getWorld();
        Clearing position = currentPosition;
        HomeWardPathCheck homeward = new HomeWardPathCheck(ant);
        Trail target;
        while (position.id() != world.anthill().id()) {
            assert (sequence.size()>1);
            target = homeward.getTargetTrail(position);
            if(ant.isAdventurer() ){
                ant.getRecorder().select(ant, target, position.connectsTo(), Recorder.SelectionReason.RETURN_IN_SEQUENCE);
            } else {
                ant.getRecorder().select(ant, target, position.connectsTo(), Recorder.SelectionReason.RETURN_FOOD);
            }
            target.enterTrail(position, ant, EntryReason.HEADING_BACK_HOME);
            target.to().enterClearing(target, ant, EntryReason.HEADING_BACK_HOME, update);
            position = target.to();
        }
        position.dropFood(position, ant);
        sequence.clear();
        ant.TrailsToVisitedClearings.clear();
        ant.TrailSequence.clear();
        ant.setAntTONormalState();
        ant.alreadyEnteredTrails.clear();
        ant.getRecorder().returnedFood(ant);
    }

}
