package com.ejalaa.simulation;

import com.ejalaa.logging.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The simulator engine that handles the list of event
 */
public class SimEngine {

    private static final String className = "SimEngine";
    private static final long MAX_LOOPS = 1000000L;
    private ArrayList<Event> events;
    private LocalDateTime currentSimTime, endSimTime;
    private int loops;

    public SimEngine(LocalDateTime endSimTime) {
        this.events = new ArrayList<>();
        this.currentSimTime = LocalDateTime.MIN;
        this.endSimTime = endSimTime;
        this.loops = 0;
    }

    public void addEvent(Event newEvent) {
        if (!this.events.contains(newEvent)) {
            this.events.add(newEvent);
            Collections.sort(this.events);
        } else {
            System.out.println("the event is already here I don't know what to do !");
        }
    }

    public void loop() {
        while (!simHasEnded()) {
            // Get first element
            Event currentEvent = this.events.get(0);
            this.events.remove(0);
            Logger.getInstance().log(currentEvent);
            // Do the action of this event and get all generated events
            currentEvent.doAction();
            // Update simulation time with current event time
            this.currentSimTime = currentEvent.getScheduledTime();
            // Sometimes an action results in no other event
            this.events.addAll(currentEvent.getGeneratedEvents());
            Collections.sort(this.events);
            // Increment loops
            this.loops += 1;
        }
        Logger.getInstance().log(className, this.currentSimTime, "Simulation has ended !");
    }

    private boolean simHasEnded() {
        return this.events.isEmpty() || this.currentSimTime.isAfter(this.endSimTime) || this.loops >= MAX_LOOPS;
    }

    public LocalDateTime getCurrentSimTime() {
        return currentSimTime;
    }
}
