package com.ejalaa.peoples;

import com.ejalaa.environment.Salon;
import com.ejalaa.logging.Logger;
import com.ejalaa.simulation.Event;
import com.ejalaa.simulation.SimEngine;

import java.time.LocalDateTime;

/**
 * Class defining a client that comes to the hairdresser
 */
public abstract class Client extends People {

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
    private Event checkHairDresserAndQueueStateEvent, hairdressingDoneEvent;
    private Salon salon;

    /*
    CONSTRUCTOR
     */
    protected Client(SimEngine simEngine, String name, Salon salon) {
        super(simEngine, name);
        this.descName = className + this.name;
        this.salon = salon;
        this.arrivedTime = arrivedTime;
        this.state = State.Created;
        // event creation
        defineIsHairdresserOpenAndLowQueueEvent();
    }

    /*
    * ********************************************************************
    * GETTER AND SETTER
    * ********************************************************************
    */

    protected int getMaxWaitingQueueToStay() {
        return this.maxWaitingQueueToStay;
    }

    public LocalDateTime getArrivedTime() {
        return arrivedTime;
    }

    public LocalDateTime getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(LocalDateTime finishedTime) {
        this.finishedTime = finishedTime;
    }

    /*
    * ********************************************************************
    * EVENTS
    * ********************************************************************
    */
    private void defineIsHairdresserOpenAndLowQueueEvent() {
        this.checkHairDresserAndQueueStateEvent = new Event("Is the hairdresser Open and not a lot of people?",
                this.arrivedTime, this.descName) {
            @Override
            public void doAction() {
                checkingHairdresserAction(this.getScheduledTime());
                addGeneratedEvent(getNextEvent());
            }
        };
    }

    private void defineHairDressingDoneEvent() {
        this.hairdressingDoneEvent = new Event("Hairdressing finished",
                this.finishedTime, this.descName) {
            @Override
            public void doAction() {
                hairdressingDoneAction(this.getScheduledTime());
                addGeneratedEvent(getNextEvent());
            }
        };
    }

    /*
    * ********************************************************************
    * ACTIONS
    * ********************************************************************
    */
    private void checkingHairdresserAction(LocalDateTime scheduledTime) {
        if (salon.isOpen() & lowQueue()) {
            Logger.getInstance().log(this.descName, scheduledTime, "hairdresser is open and low queue. I enter");
            this.state = State.Inside;
            handleMe();
            defineHairDressingDoneEvent();
        } else if (salon.isOpen()) {
            Logger.getInstance().log(this.descName, scheduledTime, "hairdresser is open BUT big queue. I Go");
            this.state = State.Gone;
        } else {
            Logger.getInstance().log(this.descName, scheduledTime, "hairdresser is closed. I Go");
            this.state = State.Gone;
        }
    }

    private void hairdressingDoneAction(LocalDateTime scheduledTime) {
        letMeGo();
        Logger.getInstance().log(this.descName, scheduledTime, "hairdressing done. I go");
        this.state = State.Gone;
    }

    /*
    * ********************************************************************
    * MACHINE STATE
    * ********************************************************************
    */
    @Override
    public Event getNextEvent() {
        switch (this.state) {
            case Created:
                return this.checkHairDresserAndQueueStateEvent;
            case Inside:
                return this.hairdressingDoneEvent;
            case Gone:
                return null;
            default:
                return null;
        }
    }


    protected abstract boolean lowQueue();

    protected abstract void handleMe();

    protected abstract void letMeGo();

    // states
    protected enum State {
        Created, Inside, Gone
    }
}
