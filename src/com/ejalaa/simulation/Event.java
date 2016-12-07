package com.ejalaa.simulation;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Event class represents a simulation event.
 * It has:
 * - an associated action that must be implemented.
 * - a scheduledTime that corresponds to the time when the event will happen
 */
public abstract class Event implements Comparable<Event> {

    /*
    * ********************************************************************
    * ATTRIBUTES
    * ********************************************************************
    */

    private String description, creator;
    private LocalDateTime scheduledTime;
    private ArrayList<Event> generatedEvents;

    /*
    Constructor
     */
    protected Event(String description, LocalDateTime scheduledTime, String creator) {
        // TODO: 04/12/2016 add postedTime in parameters and attributes
        super();
        this.description = description;
        this.scheduledTime = scheduledTime;
        this.creator = creator;
        this.generatedEvents = new ArrayList<>();
    }

    /*
    Action to be done by the simulator engine
     */
    public abstract void doAction();

    /*
    Each event will generated new action. We use this method to add them to the list
     */
    public void addGeneratedEvent(Event e) {
        if (e != null)
            this.generatedEvents.add(e);
    }

    /*
    Each event is comparable to another, by comparing their scheduled time
     */
    @Override
    public int compareTo(Event other) {
        return this.scheduledTime.compareTo(other.scheduledTime);
    }

    /*
    * ********************************************************************
    * GETTER AND SETTERS
    * ********************************************************************
    */
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public String getCreator() {
        return creator;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        String res;
        res = "EVENT:";
        res += "\t CREATOR: " + this.creator;
        res += "\t Description: " + this.description;
        res += "\t TIME TO BE HANDLED: " + this.scheduledTime.toString();
        return res;
    }

    public ArrayList<Event> getGeneratedEvents() {
        return generatedEvents;
    }
}
