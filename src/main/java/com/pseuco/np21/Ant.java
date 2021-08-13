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
      Clearing lastClearing = clearingSequence.get(clearingSequence.size() - 2);
      return lastClearing.id() == c.id();
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

  private void forwardMoving(Trail from) throws InterruptedException {
   try {
     Trail trailFrom = from;
     while (!this.holdFood) {
       if (position.TakeOnPieceOfFood(this)) {
         recorder.pickupFood(this, position);
         recorder.startFoodReturn(this);
         if (this.isAdventurer()) {
           recorder.select(this, from.reverse(), position.connectsTo(), SelectionReason.RETURN_IN_SEQUENCE);
         } else {
           recorder.select(this, from.reverse(), position.connectsTo(), SelectionReason.RETURN_FOOD);
         }

         if (position.getOrSetFood(FoodInClearing.HAS_FOOD)) {
           homewardMoving(true);
         } else {
           homewardMoving(false);
         }
         break;
       }


       if (searchFood.checkTrail(position)) {
         Trail target = searchFood.getTargetTrail(position);
         if (target.getOrUpdateFood(false, null, false).isAPheromone()) {
           recorder.select(this, target, position.connectsTo(), SelectionReason.FOOD_SEARCH);
         } else {
           recorder.select(this, target, position.connectsTo(), SelectionReason.EXPLORATION);
         }
         target.enterTrail(position, this, EntryReason.FOOD_SEARCH);
         Clearing nextClearing = target.to();
         boolean isClearingInSequence = isInSequence(nextClearing);
         nextClearing.enterClearing(target, this, EntryReason.FOOD_SEARCH, true);
         position = nextClearing;
         trailFrom = target;
         if (isClearingInSequence) {
           target = searchFood.getTrailToStepBack(position, target);
           recorder.select(this, target, position.connectsTo(), SelectionReason.IMMEDIATE_RETURN);
           target.enterTrail(nextClearing, this, EntryReason.IMMEDIATE_RETURN);
           target.to().enterClearing(target, this, EntryReason.IMMEDIATE_RETURN, false);
           position = target.to();
           trailFrom = target;
         }
         //from = target;

       } else {
         Trail t = searchFood.getTrailToStepBack(position, trailFrom);
         recorder.select(this, t, position.connectsTo(), SelectionReason.NO_FOOD_RETURN);
         t.enterTrail(position, this, EntryReason.NO_FOOD_RETURN);
         t.to().enterClearing(t, this, EntryReason.NO_FOOD_RETURN, false);
         position = t.to();
         trailFrom = t;
       }
     }
    }  catch(InterruptedException ex) {
     recorder.despawn(this, DespawnReason.TERMINATED);
     Thread.currentThread().interrupt();
   }
  }

  private Trail init() throws InterruptedException {

    if (searchFood.checkTrail(position)) {
      Trail target = searchFood.getTargetTrail(position);
      if (target.getOrUpdateFood(false, null, false).isAPheromone()) {
        recorder.select(this, target, position.connectsTo(), SelectionReason.FOOD_SEARCH);
      } else {
        recorder.select(this, target, position.connectsTo(), SelectionReason.EXPLORATION);
      }
      target.enterTrail(position, this, EntryReason.FOOD_SEARCH);
      position = target.to();
      position.enterClearing(target, this, EntryReason.FOOD_SEARCH, true);
      return target;
    } else {
      recorder.leave(this,position);
     // recorder.despawn(this, Recorder.DespawnReason.TERMINATED);
      throw new InterruptedException();
    }

  }


  private void homewardMoving(boolean update) throws InterruptedException {
   try {
     HomeWardPathCheck homeward = new HomeWardPathCheck(this);
     //recorder.startFoodReturn(this);
     Trail target;
     while (position.id() != this.getWorld().anthill().id()) {

       target = homeward.getTargetTrail(position);
       // is already in forward  added
       // recorder.select(this, target, position.connectsTo(), SelectionReason.RETURN_FOOD);
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
   catch(InterruptedException ex) {
      recorder.despawn(this, DespawnReason.TERMINATED);
      Thread.currentThread().interrupt();
    }
  }


  /**
   * Primary ant behavior.
   */
  public void run() {
    position = world.anthill();
    recorder.spawn(this);
    //TODO CHECK, Anthill should not be added to the Sequence

    recorder.enter(this, position);
    recorder.startFoodSearch(this);
    recorder.startExploration(this);
    try {

      while (world.isFoodLeft()) {
        addClearingToSequence(position);  // adding the antHill to the sequence
        Trail from = init();
        forwardMoving(from);
        if (world.isFoodLeft()) {
          recorder.startFoodSearch(this);
        }

      }
      recorder.leave(this, position);
      recorder.despawn(this, DespawnReason.ENOUGH_FOOD_COLLECTED);
      Thread.currentThread().interrupt();

    } catch (InterruptedException e) {
      recorder.despawn(this, DespawnReason.TERMINATED);
      Thread.currentThread().interrupt();
    }

    // TODO handle termination

  }


}
