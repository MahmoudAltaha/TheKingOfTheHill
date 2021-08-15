package com.pseuco.np21;


import com.pseuco.np21.shared.Position;
import com.pseuco.np21.shared.Recorder;
import com.pseuco.np21.shared.Recorder.DespawnReason;
import com.pseuco.np21.shared.Recorder.SelectionReason;
import com.pseuco.np21.shared.World;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Representation of an ant with behavior.
 * <p>
 * You may change the code however you see fit.
 */
public class Ant extends com.pseuco.np21.shared.Ant implements Runnable {

  private static class AntDiedException extends Throwable {

    private final boolean eaten;
    private final Position where;

    private AntDiedException(final boolean eaten, final Position where) {
      this.eaten = eaten;
      this.where = where;
    }

    private boolean wasEaten() {
      return eaten;
    }

    private Position where() {
      return where;
    }
  }


  private final World<Clearing, Trail> world;

  private final Recorder recorder;

  private Clearing position;

  private final List<Clearing> clearingSequence = new ArrayList<>();
  private boolean adventurer = false;
  private boolean holdFood = false;
  public HashMap<Integer, Trail> TrailsToVisitedClearing = new HashMap<>();
  SearchFoodPathCheck searchFood = new SearchFoodPathCheck(this);


  /**
   * Constructs an ant given a basic ant, the world and a recorder.
   *
   * @param ant      the template ant
   * @param world    the ant has to live in
   * @param recorder to log all actions against
   */
  public Ant(final com.pseuco.np21.shared.Ant ant, final World<Clearing, Trail> world,
             final Recorder recorder) {
    super(ant);
    this.world = world;
    this.recorder = recorder;
  }

  /**
   * check if the Ant holds food.
   *
   * @return true if he Ant holds food.
   */
  public boolean hasFood() {
    return holdFood;
  }

  /**
   * Set holdFood to true when the Ant has picked up some food and to false when it drops the food.
   *
   * @param holdFood true or false.
   */
  public void setHoldFood(boolean holdFood) {
    this.holdFood = holdFood;
  }


  /**
   * check the state of the Ant
   *
   * @return true if the Ant is an adventurer.
   */
  public boolean isAdventurer() {
    return adventurer;
  }

  /**
   * change the current state of the Ant to Adventurer
   */
  public void setAntTOAdventurer() {
    adventurer = true;
  }

  /**
   * return the state of the Ant to normal.
   */
  public void setAntTONormalState() {
    adventurer = false;
  }


  /**
   * this methode used to get the current Sequence.
   *
   * @return the sequence (visited Clearings).
   */
  public List<Clearing> getClearingSequence() {
    return clearingSequence;
  }

  /**
   * this methode used to add a new Clearing to the Sequence.
   *
   * @param c the Clearing to be added.
   */
  public void addClearingToSequence(Clearing c) {
    clearingSequence.add(c);
  }

  /**
   * this methode is used to remove a Clearing from the sequence.
   *
   * @param c the Clearing to be removed.
   */
  public void removeClearingFromSequence(Clearing c) {
    clearingSequence.remove(c);
  }

  /**
   * this methode is used to check if the Clearing is already in the sequence.
   *
   * @param c Clearing
   * @return true if the Clearing is already in the sequence.
   */
  public boolean isInSequence(Clearing c) {
    for (int i = 0; i < getClearingSequence().size(); i++) {
      if (getClearingSequence().get(i).id() == c.id()) {
        return true;
      }
    }
    return false;
  }

  /**
   * this methode is used to check if the Clearing is the second last visited in the sequence.
   *
   * @param c Clearing
   * @return true if the Clearing is already in the sequence.
   */
  public boolean isSecondLastVisitedInSequence(Clearing c) {
    if (clearingSequence.size() >= 2) {
      List<Clearing> sequence1 = new ArrayList<>(clearingSequence);
      int indexLastClearingInSequence1 = sequence1.size() - 1;
      sequence1.remove(indexLastClearingInSequence1);
      int newIndexOfLastClearing = sequence1.size() - 1 ;
      Clearing secondLastClearingInSequence = sequence1.get(newIndexOfLastClearing);

      return secondLastClearingInSequence.name().equals(c.name());
    }
    return false;
  }


  /**
   * this methode is used to get to recorder.
   *
   * @return Recorder
   */
  public Recorder getRecorder() {
    return recorder;
  }

  /**
   * getter for the World
   *
   * @return world.
   */
  public World<Clearing, Trail> getWorld() {
    return world;
  }




  private void forwardMoving(Clearing currentPosition) throws InterruptedException {
    Clearing position = currentPosition;
    boolean foundATrail = searchFood.checkTrail(position);
    // if the Clearing was the Hill and has no Trails terminate. (we need this just for the first round )
    if (position.id() == world.anthill().id() && !foundATrail) {
      recorder.leave(this, position);
      recorder.despawn(this, Recorder.DespawnReason.TERMINATED);
      throw new InterruptedException();
    }
    // now the Clearing is not the Hill(when this is recursion round 2 or more) or maybe hill(first round or more) but has (for sure)some Trails.
    if (foundATrail) {
      Trail targetTrail = searchFood.getTargetTrail(position); // get The Trail
      if (targetTrail.getOrUpdateFoodPheromone(false, null, false).isAPheromone()) { // select one reason
        recorder.select(this, targetTrail, position.connectsTo(), SelectionReason.FOOD_SEARCH);
      } else {
        recorder.select(this, targetTrail, position.connectsTo(), SelectionReason.EXPLORATION);
      }
      targetTrail.enterTrail(position, this, EntryReason.FOOD_SEARCH);  // enter the Trail
      Trail ourTrail = targetTrail;
      Clearing ourNextClearing = targetTrail.to();

      boolean clearingAlreadyInSequence = isInSequence(ourNextClearing);

      ourNextClearing.enterClearing(targetTrail, this, EntryReason.FOOD_SEARCH, true); // enter the Clearing (trail.To)
      position = ourNextClearing; // update the Position
      // now check if the Clearing is in the sequence:
      if (!clearingAlreadyInSequence) {
        if (!this.holdFood) {  // if the Ant does not have food already try to get some
          if (position.TakeOnPieceOfFood(this)) { // if the Ant picked up some start homeward.
            recorder.pickupFood(this, position);
            recorder.startFoodReturn(this);
            if (position.getOrSetFood(FoodInClearing.HAS_FOOD)) { // was that the last piece of food ??
              homewardMoving(true, position);// don't update pheromone
              return;
            } else {
              homewardMoving(false, position); // update pheromone.
              return;
            }
          } else { // the Clearing has no food it was not in the sequence so continue (do the same above --> recursion)
            forwardMoving(position);
          }
        } // the Clearing is already in The sequence so we go one step by Immediate-return the keep going back if we don't find way.
      } else {
        targetTrail = searchFood.getTrailToStepBack(position, ourTrail); // get the reverse of ourTrail
        recorder.select(this, targetTrail, position.connectsTo(), SelectionReason.IMMEDIATE_RETURN);
        targetTrail.enterTrail(position, this, EntryReason.IMMEDIATE_RETURN); // enter the trail
        ourTrail = targetTrail; // update our Trail
        ourNextClearing = ourTrail.to(); // get our next Clearing
        ourNextClearing.enterClearing(ourTrail, this, EntryReason.IMMEDIATE_RETURN, false); // enter the Clearing
        position = ourNextClearing; // update the Ant Position(current Clearing);

        // now we stepped back one step ,,,we should see if the clearing here has other choices
        // as long as we don't find a Clearing with undiscovered Trail we go with no Food Return.
        while (!searchFood.checkTrail(position) && getClearingSequence().size() > 1) {
          targetTrail = searchFood.getTrailByNofoodReturn(position, this);
          recorder.select(this, targetTrail, position.connectsTo(), SelectionReason.NO_FOOD_RETURN);
          targetTrail.enterTrail(position, this, EntryReason.NO_FOOD_RETURN);
          ourTrail = targetTrail;
          ourNextClearing = ourTrail.to();
          ourNextClearing.enterClearing(ourTrail, this, EntryReason.NO_FOOD_RETURN, false);
          position = ourNextClearing;
        }
        // now we are out the while-Loop that means we found a Clearing with some Trail Or we went so many Trails back
        // until we reached the Hill ,,so we need a check.
        if (position.id() == world.anthill().id()) {  // if we are in the Hill and no more choices terminate
          recorder.leave(this, position);
          recorder.despawn(this, DespawnReason.TERMINATED);
          throw new InterruptedException();
        }
// Or we are in a Trail which has some Other undiscovered Trails. so we need to continue FoodSearch --> recursion.
        else {
          forwardMoving(position);
        }
      }
      // if you are here then the Ant has reached by normal FoodSearch a Trails with no more new Choices: so NO-Food- Return.
    } else {
      keepGoingBackByNoFoodReturnUntilTheAntFindATrailThenContinueFoodSearch(searchFood,position);
    }

  }

  private void keepGoingBackByNoFoodReturnUntilTheAntFindATrailThenContinueFoodSearch(SearchFoodPathCheck searchFood, Clearing currentPosition) throws InterruptedException {
    Clearing position = currentPosition;

    // as long as we don't find a Clearing with undiscovered Trail we go with no Food Return.
    while (!searchFood.checkTrail(position) && getClearingSequence().size() > 1) {
      Trail targetTrail = searchFood.getTrailByNofoodReturn(position, this);
      recorder.select(this, targetTrail, position.connectsTo(), SelectionReason.NO_FOOD_RETURN);
      targetTrail.enterTrail(position, this, EntryReason.NO_FOOD_RETURN);
      Clearing ourNextClearing = targetTrail.to();
      ourNextClearing.enterClearing(targetTrail, this, EntryReason.NO_FOOD_RETURN, false);
      position = ourNextClearing;
    }
    // if we are in the Hill and no more choices terminate
    if (position.id() == world.anthill().id() && !searchFood.checkTrail(position)) {
      recorder.leave(this, position);
      recorder.despawn(this, DespawnReason.TERMINATED);
      throw new InterruptedException();
    }// Or we are in a Trail which has some Other undiscovered Trails. so we need to continue FoodSearch --> recursion.
    else {
      forwardMoving(position);
    }
  }


    private void homewardMoving ( boolean update, Clearing currentPosition) throws InterruptedException {
    Clearing position = currentPosition;
      HomeWardPathCheck homeward = new HomeWardPathCheck(this);
      Trail target;
      while (position.id() != this.getWorld().anthill().id()) {
        assert (getClearingSequence().size()>1);
        target = homeward.getTargetTrail(position);
        if(this.isAdventurer() ){
          getRecorder().select(this, target, position.connectsTo(), SelectionReason.RETURN_IN_SEQUENCE);
        } else {
          getRecorder().select(this, target, position.connectsTo(), SelectionReason.RETURN_FOOD);
        }
        target.enterTrail(position, this, EntryReason.HEADING_BACK_HOME);
        target.to().enterClearing(target, this, EntryReason.HEADING_BACK_HOME, update);
        position = target.to();
      }
      position.dropFood(position, this);
      clearingSequence.clear();
      TrailsToVisitedClearing.clear();
      this.setAntTONormalState();
      recorder.returnedFood(this);
    }


    /**
     * Primary ant behavior.
     */
    public void run () {
      try {
        position = world.anthill();
        recorder.spawn(this);
        //TODO CHECK, Anthill should not be added to the Sequence

        recorder.enter(this, position);
        if (!world.isFoodLeft()) {
          recorder.leave(this, position);
          recorder.despawn(this, DespawnReason.ENOUGH_FOOD_COLLECTED);
          throw new InterruptedException();
        }
        recorder.startFoodSearch(this);
        recorder.startExploration(this);

        while (world.isFoodLeft()) {
          this.addClearingToSequence(position);  // adding the antHill to the sequence

          forwardMoving(position);
          if (world.isFoodLeft()) {
            recorder.startFoodSearch(this);
          }
        }
        recorder.leave(this, position);
        recorder.despawn(this, DespawnReason.ENOUGH_FOOD_COLLECTED);

        throw new InterruptedException();
      } catch (InterruptedException e) {

        Thread.currentThread().interrupt();
      }

      // TODO handle termination

    }



}
