package com.pseuco.np21;

public class TrailEntryHandler {

    public void EnterTheTrail(Trail trail,Ant ant)  {
        trail.enter();  // enter the Trail
        ant.getRecorder().enter(ant, trail);  // recorder stuff.
    }

    public void LeaveTheClearing(Clearing clearing, Ant ant)  {
        clearing.leave(); // leave the Clearing
        ant.getRecorder().leave(ant, clearing); // recorder stuff
    }
}
