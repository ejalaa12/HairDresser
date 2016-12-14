package com.ejalaa.peoples;

import com.ejalaa.environment.Salon;
import com.ejalaa.simulation.SimEngine;

/**
 * This class defines a Boss which is also a worker but has more privileges
 */
public class HairdresserBoss extends Hairdresser {
    public HairdresserBoss(SimEngine simEngine, String name, Salon salon) {
        super(simEngine, name, salon);
    }
}
