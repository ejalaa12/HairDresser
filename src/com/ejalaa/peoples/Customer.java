package com.ejalaa.peoples;

import com.ejalaa.environment.Salon;
import com.ejalaa.simulation.SimEngine;

/**
 * A Customer is a client that comes often and has a favourite Hairdresser
 */
public class Customer extends Client {

    /*
    * ********************************************************************
    * ATTRIBUTES
    * ********************************************************************
    */
    Hairdresser favorite;

    protected Customer(SimEngine simEngine, String name, Salon salon, Hairdresser favorite) {
        super(simEngine, name, salon);
        this.favorite = favorite;
    }
}
