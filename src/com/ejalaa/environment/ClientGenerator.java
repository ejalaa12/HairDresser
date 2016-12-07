package com.ejalaa.environment;

import com.ejalaa.simulation.Entity;
import com.ejalaa.simulation.Event;
import com.ejalaa.simulation.SimEngine;

import java.time.LocalDateTime;

/**
 * Client generator generates new client at stepped time
 */
public abstract class ClientGenerator extends Entity {

    private static final String name = "Client Generator";
    private int timeBetweenClient = 10;     // in min
    private Event nextClientEvent;
    private int nbOfClientGenerated;
    private LocalDateTime nextClientTime;

    public ClientGenerator(SimEngine simEngine) {
        super(simEngine);
        this.nextClientTime = simEngine.getCurrentSimTime();
        updateNextEvent();
    }

    private void updateNextEvent() {
        // Update time next client is created
        this.nextClientTime = this.nextClientTime.plusMinutes(this.timeBetweenClient);
        // Then update the Event of creating a client
        String clientName = String.format("Client-%d", getNbOfClientGenerated());
        String eventDesc = String.format("Creation of %s", clientName);
        nextClientEvent = new Event(eventDesc, this.nextClientTime, name) {
            @Override
            public void doAction() {
                createNewClientAction(clientName, getScheduledTime());
                updateNbOfClientGenerated();
                updateNextEvent();
//                addGeneratedEvent(getNextEvent());
            }
        };
    }

    private int getNbOfClientGenerated() {
        return nbOfClientGenerated;
    }

    private void updateNbOfClientGenerated() {
        this.nbOfClientGenerated += 1;
    }

    abstract void createNewClientAction(String name, LocalDateTime timeToAppear);
}
