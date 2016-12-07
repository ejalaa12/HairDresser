package com.ejalaa.peoples;

import com.ejalaa.simulation.Entity;
import com.ejalaa.simulation.SimEngine;

/**
 * The parent of all people (clients, workers...)
 * TODO: extends or implements Entity ?
 */
public abstract class People extends Entity {

    public String name;

    People(SimEngine simEngine, String name) {
        super(simEngine);
        this.name = name;
    }

}
