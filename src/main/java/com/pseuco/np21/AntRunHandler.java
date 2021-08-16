package com.pseuco.np21;

import com.pseuco.np21.shared.Recorder;
import com.pseuco.np21.shared.World;

import java.util.List;

public class AntRunHandler {

        private final Ant  ant;

    public AntRunHandler(Ant ant) {
        this.ant = ant;
    }



    private void recorderStuffEnterClearingLeaveTrail(Clearing ourNextClearing, Trail ourLastTrail){
        ant.getRecorder().enter(ant, ourNextClearing);
        ant.getRecorder().leave(ant, ourLastTrail);
    }

    private  void recorderStuffDeadDespawn(Trail ourLastTrail){
        ant.getRecorder().attractAttention(ant); // added new
        ant.getRecorder().leave(ant, ourLastTrail);
        ant.getRecorder().despawn(ant, Recorder.DespawnReason.DISCOVERED_AND_EATEN);
    }


    private void recorderStuffTerminateNowOnLastTrail(Trail trail){
        ant.getRecorder().leave(ant, trail);
        ant.getRecorder().despawn(ant, Recorder.DespawnReason.TERMINATED);
    }



    public void EnterTrailRecorderStuff(Clearing currentClearing , Trail target, boolean success) throws InterruptedException {
        if(success){
            ant.getRecorder().enter(ant,target);
            ant.getRecorder().leave(ant,currentClearing);
        }else {
            ant.getRecorder().leave(ant,currentClearing);
            ant.getRecorder().despawn(ant, Recorder.DespawnReason.TERMINATED);
            throw new InterruptedException();
        }
    }



    public void handleEnterClearingFoodSearch(Clearing ourNextClearing, Trail ourLastTrail) throws InterruptedException {
        boolean enteredSuccess = ourNextClearing.enterClearing(ourLastTrail, ant, EntryReason.FOOD_SEARCH); // enter the Clearing (trail.To)
        if (enteredSuccess) {
            recorderStuffEnterClearingLeaveTrail(ourNextClearing,ourLastTrail);
            com.pseuco.np21.shared.Trail.Pheromone hillPheromone = ourLastTrail.reverse().getOrUpdateHillPheromone(false, null);
            ant.getRecorder().updateAnthill(ant, ourLastTrail.reverse(), hillPheromone);
        } else {
            if (ant.isDied()) {
                recorderStuffDeadDespawn(ourLastTrail);
            } else {
                recorderStuffTerminateNowOnLastTrail(ourLastTrail);
            }
            throw new InterruptedException();
        }
    }



    public Clearing goOneStepBackWithImmediatReturn(Trail ourTrail,Clearing position) throws InterruptedException {
        SearchFoodPathCheck searchFood = ant.searchFood;
        Recorder recorder = ant.getRecorder();
        Trail targetTrail = searchFood.getTrailToStepBack(position, ourTrail); // get the reverse of ourTrail
        recorder.select(ant, targetTrail, position.connectsTo(), Recorder.SelectionReason.IMMEDIATE_RETURN);
        boolean success =targetTrail.enterTrail(position, ant, EntryReason.IMMEDIATE_RETURN); // enter the trail
        EnterTrailRecorderStuff(position,targetTrail,success); // do the recorder stuff
        ourTrail = targetTrail; // update our Trail
        Clearing ourNextClearing = ourTrail.to(); // get our next Clearing
        boolean enterSuccess = ourNextClearing.enterClearing(ourTrail, ant, EntryReason.IMMEDIATE_RETURN); // enter the Clearing
        if (enterSuccess) {
           recorderStuffEnterClearingLeaveTrail(ourNextClearing,ourTrail);
        } else {
            if (ant.isDied()) {
                recorderStuffDeadDespawn(ourTrail);
            } else {
                recorderStuffTerminateNowOnLastTrail(ourTrail);
            }
            throw new InterruptedException();
        }
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
        boolean success = targetTrail.enterTrail(position, ant, EntryReason.NO_FOOD_RETURN);
        EnterTrailRecorderStuff(position,targetTrail,success); // do the recorder stuff

        Trail ourTrail = targetTrail;
        Clearing ourNextClearing = targetTrail.to();
        boolean enterSuccess = ourNextClearing.enterClearing(ourTrail, ant, EntryReason.NO_FOOD_RETURN);
        if (enterSuccess) {
            recorderStuffEnterClearingLeaveTrail(ourNextClearing,ourTrail);
            com.pseuco.np21.shared.Trail.Pheromone foodPheromone = ourTrail.reverse().getOrUpdateFoodPheromone(false, null, false);
            ant.getRecorder().updateFood(ant, ourTrail.reverse(), foodPheromone); // recorder stuff.
        } else {
            if (ant.isDied()) {
                recorderStuffDeadDespawn(ourTrail);
            } else {
                recorderStuffTerminateNowOnLastTrail(ourTrail);
            }
            throw new InterruptedException();
        }
        position = ourNextClearing;
        return  position;
    }

    public void leaveAndInterruptAtHill(Ant ant, Clearing position) throws InterruptedException {
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
            Clearing ourNextClearing = target.to();
            if (update){
                boolean success = target.enterTrail(position, ant, EntryReason.HEADING_BACK_HOME_WITH_UPDATING);
                EnterTrailRecorderStuff(position,target,success); // do the recorder stuff
                Trail ourLastTrail = target;
                boolean enterSuccess = ourNextClearing.enterClearing(target, ant, EntryReason.HEADING_BACK_HOME_WITH_UPDATING);
                if (enterSuccess) {
                    recorderStuffEnterClearingLeaveTrail(ourNextClearing,target);
                    com.pseuco.np21.shared.Trail.Pheromone FoodPheromone = ourLastTrail.reverse().getOrUpdateFoodPheromone(false,null,false);
                    ant.getRecorder().updateFood(ant, ourLastTrail.reverse(), FoodPheromone); // recorder stuff
                } else {
                    if (ant.isDied()) {
                        recorderStuffDeadDespawn(ourLastTrail);
                    } else {
                        recorderStuffTerminateNowOnLastTrail(ourLastTrail);
                    }
                    throw new InterruptedException();
                }
            }else {
                boolean success =target.enterTrail(position, ant, EntryReason.HEADING_BACK_HOME_WITHOUT_UPDATING);
                EnterTrailRecorderStuff(position,target,success); // do the recorder stuff
                Trail ourLastTrail = target;
                boolean enterSuccess =ourNextClearing.enterClearing(target, ant, EntryReason.HEADING_BACK_HOME_WITHOUT_UPDATING);
                if (enterSuccess) {
                    recorderStuffEnterClearingLeaveTrail(ourNextClearing,target);
                } else {
                    if (ant.isDied()) {
                        recorderStuffDeadDespawn(ourLastTrail);
                    } else {
                        recorderStuffTerminateNowOnLastTrail(ourLastTrail);
                    }
                    throw new InterruptedException();
                }
            }
            position = target.to();
        }
        boolean droppedSuccess =position.dropFood(position, ant);
        if (!droppedSuccess){
            ant.getRecorder().leave(ant,position);
            ant.getRecorder().despawn(ant, Recorder.DespawnReason.TERMINATED);
            throw new InterruptedException();
        }
        sequence.clear();
        ant.TrailsToVisitedClearings.clear();
        ant.TrailSequence.clear();
        ant.setAntTONormalState();
        ant.alreadyEnteredTrails.clear();
        ant.getRecorder().returnedFood(ant);
        ant.addClearingToSequence(position);  // adding the antHill to the sequence
    }

}
