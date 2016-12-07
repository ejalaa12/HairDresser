package com.ejalaa.peoples;

import com.ejalaa.environment.Salon;
import com.ejalaa.simulation.SimEngine;

/**
 * A Customer is a client that comes often and has a favourite Hairdresser
 */
public abstract class Customer extends Client {

    /*
    * ********************************************************************
    * ATTRIBUTES
    * ********************************************************************
    */
    private static final String className = "Customer ";
    private int maxWaitingQueueToStay = 6; // max queue size that makes a client stay


    protected Customer(SimEngine simEngine, String name, Salon salon) {
        super(simEngine, name, salon);
    }
}
