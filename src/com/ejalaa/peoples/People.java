package com.ejalaa.peoples;

import com.ejalaa.simulation.Entity;

/**
 * The parent of all people (clients, workers...)
 * TODO: extends or implements Entity ?
 */
public abstract class People extends Entity {

    public String name;

    public People() {
        super();
        this.name = "People 0";
    }

    public People(String name) {
        this.name = name;
    }

}
