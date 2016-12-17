package com.ejalaa.simulation;

/**
 * An Entity creates Events that are going to be handled by the SimEngine
 */
public abstract class Entity {

    protected SimEngine simEngine;
    protected String name;

    public Entity(SimEngine simEngine) {
        this.simEngine = simEngine;
    }

    public abstract void start();

    public void printStats() {
        System.out.println(String.format("%-40s | ------------", name));
        System.out.println(new String(new char[40]).replace("\0", "-"));
    }

    public String getName() {
        return name;
    }
}
