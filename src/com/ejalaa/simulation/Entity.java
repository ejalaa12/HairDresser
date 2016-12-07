package com.ejalaa.simulation;

/**
 * An Entity creates Events that are going to be handled by the SimEngine
 */
public abstract class Entity {

    protected SimEngine simEngine;

    public Entity(SimEngine simEngine) {
        this.simEngine = simEngine;
    }

    public abstract Event getNextEvent();

}
