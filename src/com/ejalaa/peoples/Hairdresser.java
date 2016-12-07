package com.ejalaa.peoples;

import com.ejalaa.simulation.Event;
import com.ejalaa.simulation.SimEngine;

/**
 * This class defines a Hairdresser that works at the Hairdresser
 */
public class Hairdresser extends People {
    public Hairdresser(SimEngine simEngine, String name) {
        super(simEngine, name);
    }

    @Override
    public Event getNextEvent() {
        return null;
    }
}
