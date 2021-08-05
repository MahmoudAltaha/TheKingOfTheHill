package com.pseuco.np21;


import com.pseuco.np21.shared.Position;
import com.pseuco.np21.shared.Recorder;
import com.pseuco.np21.shared.World;

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

    /**
     * Constructs an ant given a basic ant, the world and a recorder.
     *
     * @param ant         the template ant
     * @param world       the ant has to live in
     * @param recorder    to log all actions against
     */
    public Ant(final com.pseuco.np21.shared.Ant ant, final World<Clearing, Trail> world, final Recorder recorder) {
        super(ant);
        this.world = world;
        this.recorder = recorder;
    }


    //TODO Add Methods to get the Target Trail

    /**
     *
     * @param currentCLearing
     * @param targetTrail
     * @return
     */
    private boolean checkTrail(Clearing currentCLearing, Trail targetTrail){
        return false;
    }

    /**
     *
     * @param currentClearing
     * @return
     */
    private Trail getTrgetTrail(Clearing currentClearing){
        return null;
    }

    /**
     * this methode used to check whether the Clearing has a Connected Trail.
     * @param c  Current Clearing
     * @return   return true if you found a Trail
     */
    private boolean checkTrail(Clearing c){
        //TODO complete this
        return true;
    }


    /**
     * Primary ant behavior.
     */
    public void run() {
        position = world.anthill();
        recorder.spawn(this);


        while (world.isFoodLeft()) {

        }

        // TODO handle termination

    }
}
