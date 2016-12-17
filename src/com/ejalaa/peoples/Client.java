package com.ejalaa.peoples;

import com.ejalaa.environment.Salon;
import com.ejalaa.logging.Logger;
import com.ejalaa.simulation.Event;
import com.ejalaa.simulation.SimEngine;

import java.time.Duration;
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
    protected int queueSizePatience = 6; // max queue size that makes a client stay
    private String descName;
    private LocalDateTime arrivedTime, finishedTime;
    private State state;
    private Salon salon;
    private Duration waitingTime;

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

    public void leaveBecauseNoHairdressers() {
        Logger.getInstance().log(descName, simEngine.getCurrentSimTime(), "No Hairdressers. I'm leaving :(");
        state = State.Gone;
    }

    public void leaveBecauseLongQueue() {
        Logger.getInstance().log(descName, simEngine.getCurrentSimTime(), "Long Queue. I'm leaving :(");
        state = State.Gone;
    }

    public String getName() {
        return name;
    }

    public void hairdressingFinished() {
        state = State.Gone;
        Logger.getInstance().log(descName, simEngine.getCurrentSimTime(), "Hairdressing done I'm leaving");
        // notify salon so have feedback on the client experience
        salon.notifyClientLeaving(this);
    }

    public void setGettingHairdress() {
        waitingTime = Duration.between(arrivedTime, simEngine.getCurrentSimTime());
        state = State.GettingHairdress;
    }

    public void setWaiting() {
        state = State.Waiting;
    }

    public Duration getWaitingTime() {
        return waitingTime;
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
                Client.this.salon.clientCameWhenClosed();
                Client.this.state = State.Gone;
            }

        }
    }

}
