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
    private int maxWaitingQueueToStay = 2; // max queue size that makes a client stay
    private String descName;
    private LocalDateTime arrivedTime, finishedTime;
    private State state;
    private Event checkSalonOpenEvent, hairdressingDoneEvent;
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
        simEngine.addEvent(new CheckSalonEvent());
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

    public LocalDateTime getFinishedTime() {
        return finishedTime;
    }

    public void goAfter(int hairDressingDuration) {
        finishedTime = arrivedTime.plusMinutes(hairDressingDuration);
        simEngine.addEvent(new LeavingEvent());
    }

    // states
    protected enum State {
        Created, Inside, Gone

    }

    /*
    * ********************************************************************
    * EVENTS
    * ********************************************************************
    */
    private class CheckSalonEvent extends Event {

        CheckSalonEvent() {
            super(Client.this.descName, Client.this.getArrivedTime(), "Checking salon");
        }

        @Override
        public void doAction() {
            if (Client.this.salon.isOpen()) {
                Logger.getInstance().log(Client.this.descName, scheduledTime, "I can enter");
                Client.this.state = State.Inside;
                Client.this.salon.handleClient(Client.this);
            } else {
                Logger.getInstance().log(Client.this.descName, scheduledTime, "I can not enter");
                Client.this.state = State.Gone;
            }

        }
    }

    /*
    * ********************************************************************
    * MACHINE STATE
    * ********************************************************************
    */

    private class LeavingEvent extends Event {

        LeavingEvent() {
            super(Client.this.descName, finishedTime, "HairDressing Done. I'm Leaving");
        }

        @Override
        public void doAction() {
            Client.this.state = State.Gone;
            Client.this.salon.letGo(Client.this);
        }
    }
}
