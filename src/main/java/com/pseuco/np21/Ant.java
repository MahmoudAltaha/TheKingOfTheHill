package com.pseuco.np21;


import com.pseuco.np21.shared.Position;
import com.pseuco.np21.shared.Recorder;
import com.pseuco.np21.shared.World;


import java.util.ArrayList;
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
  public List<Trail> TrailsToVisetedClearing = new ArrayList<>();

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
   * @param holdFood
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
   * @param c Cleaing
   * @return  true if the Clearing is already in the sequence.
   */
  public boolean isInSequence(Clearing c){
    return getClearingSequence().contains(c);
  }

  /**
   * this methode is used to check if the Clearing is the second last visited in the sequence.
   *
   * @param c Cleaing
   * @return  true if the Clearing is already in the sequence.
   */
  public boolean isSecondLastVisitedInSequence(Clearing c){
    if (clearingSequence.size()>=2) {
      Clearing lastClearing = clearingSequence.get(clearingSequence.size() - 1);
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





  /**
   * Primary ant behavior.
   */
  public void run() {
    position = world.anthill();
    recorder.spawn(this);
    addClearingToSequence(position);  // adding the antHill to the sequence
    HomeWardPathCheack homeward = new HomeWardPathCheack(this);
    SearchFoodPathCheck searchFood = new SearchFoodPathCheck(this);
    while (world.isFoodLeft()) {

      Trail target = searchFood.getTargetTrail(position);
      try{
        target.enterTrail(position, this, EntryReason.FOOD_SEARCH);
        Clearing to = target.to();
        to.enterClearing(target, this, EntryReason.FOOD_SEARCH);
        addClearingToSequence(to);
        position = to;
        if(to.TakeOnPieceOfFood(this)){
          while(position.id() != world.anthill().id()){
            target = homeward.getTargetTrail(position);
            target.enterTrail(position, this, EntryReason.HEADING_BACK_HOME);
            target.to().enterClearing(target, this, EntryReason.HEADING_BACK_HOME);
            position = target.to();
          }
          position.dropFood(position, this);
          continue;

        }else{
          addClearingToSequence(position);
          continue;

        }

      }catch (InterruptedException e){
        Thread.currentThread().interrupt();
      }




    }

    // TODO handle termination

  }


}
