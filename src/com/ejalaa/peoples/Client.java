package com.ejalaa.peoples;

import com.ejalaa.environment.Salon;
import com.ejalaa.logging.Logger;
import com.ejalaa.simulation.Event;
import com.ejalaa.simulation.SimEngine;

import java.time.LocalDateTime;

/**
 * Class defining a client that comes to the hairdresser
 */
public class Client extends People {

    /*
    * ********************************************************************
    * ATTRIBUTES
    * ********************************************************************
    */
    private static final String className = "Client ";
    private int queueSizePatience = 3; // max queue size that makes a client stay
    private String descName;
    private LocalDateTime arrivedTime, finishedTime;
    private State state;
    private Salon salon;

    /*
    CONSTRUCTOR
     */
    public Client(SimEngine simEngine, String name, Salon salon) {
        super(simEngine, name);
        this.descName = className + this.name;
        this.salon = salon;
        this.state = State.Created;
    }

    public void start() {
        // First Event
        simEngine.addEvent(new ArrivesAtSalonEvent());
    }

    public void arrivesAt(LocalDateTime arrivedTime) {
        setArrivedTime(arrivedTime);
        start();
    }

    /*
    * ********************************************************************
    * GETTER AND SETTER
    * ********************************************************************
    */

    public LocalDateTime getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(LocalDateTime arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    public int getQueueSizePatience() {
        return queueSizePatience;
    }

    public void LeaveFull() {
        Logger.getInstance().log(name, simEngine.getCurrentSimTime(), "Too much waiting people. I'm leaving");
    }

    public String getName() {
        return name;
    }

    public void hairdressingFinished() {
        state = State.Gone;
        Logger.getInstance().log(name, simEngine.getCurrentSimTime(), "Hairdressing done I'm leaving");
    }

    public void setGettingHairdress() {
        state = State.GettingHairdress;
    }

    public void setWating() {
        state = State.Waiting;
    }

    // states
    protected enum State {
        Created, Inside, Waiting, GettingHairdress, Gone

    }

    /*
    * ********************************************************************
    * EVENTS
    * ********************************************************************
    */
    private class ArrivesAtSalonEvent extends Event {

        ArrivesAtSalonEvent() {
            super(Client.this.descName, Client.this.getArrivedTime(), "Checking salon");
        }

        @Override
        public void doAction() {
            if (Client.this.salon.isOpen()) {
                Logger.getInstance().log(Client.this.descName, scheduledTime, "I can enter");
                Client.this.state = State.Inside;
                Client.this.salon.handleClient(Client.this);
            } else {
                Logger.getInstance().log(Client.this.descName, scheduledTime, "Salon is closed -> I can not enter");
                Client.this.state = State.Gone;
            }

        }
    }

}
