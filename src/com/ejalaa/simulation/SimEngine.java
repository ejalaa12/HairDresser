package com.ejalaa.simulation;

import com.ejalaa.logging.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * The simulator engine that handles the list of event
 */
public class SimEngine {

    private static final String className = "SimEngine";
    private static final long MAX_LOOPS = 1000000L;

    private ArrayList<Event> events;
    private LocalDateTime startSimTime, currentSimTime, endSimTime;
    private int loops;
    private Random random;

    public SimEngine(long seed, LocalDateTime startSimTime, LocalDateTime endSimTime) {
        this.events = new ArrayList<>();
        this.random = new Random(seed);
        this.startSimTime = startSimTime;
        this.currentSimTime = startSimTime;
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
            simStep();
        }
        Logger.getInstance().log(className, this.currentSimTime, "Simulation has ended !");
    }

    private void simStep() {
        // Get first element
        Event currentEvent = this.events.get(0);
        this.events.remove(0);
        Logger.getInstance().log(currentEvent);
        // Update simulation time with current event time
        currentSimTime = currentEvent.getScheduledTime();
        // Do the action of this event and get all generated events
        currentEvent.doAction();
        // Sometimes an action results in no other event
        this.events.addAll(currentEvent.getGeneratedEvents());
        Collections.sort(this.events);
        // Increment loops
        this.loops += 1;
    }

    private boolean simHasEnded() {
        return this.events.isEmpty() || this.currentSimTime.isAfter(this.endSimTime) || this.loops >= MAX_LOOPS;
    }

    public LocalDateTime getCurrentSimTime() {
        return currentSimTime;
    }

    public Random getRandom() {
        return random;
    }
}
