package com.ejalaa.peoples;

import java.time.LocalDateTime;

/**
 * A Customer is a client that comes often and has a favourite Worker
 */
public abstract class Customer extends Client {

    /*
    * ********************************************************************
    * ATTRIBUTES
    * ********************************************************************
    */
    private static final String className = "Customer ";
    private int maxWaitingQueueToStay = 6; // max queue size that makes a client stay

    public Customer(String name, LocalDateTime arrivingTimeAtHairdresser) {
        super(name, arrivingTimeAtHairdresser);
    }
}
