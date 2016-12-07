package com.ejalaa.simulation;

/**
 * An Entity creates Events that are going to be handled by the SimEngine
 */
public abstract class Entity {

    public abstract Event getNextEvent();

}
