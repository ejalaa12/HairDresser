package com.ejalaa.environment;

import com.ejalaa.peoples.Client;
import com.ejalaa.simulation.Entity;
import com.ejalaa.simulation.Event;
import com.ejalaa.simulation.SimEngine;

import java.time.LocalDateTime;

/**
 * Client generator generates new client at stepped time
 */
public class ClientGenerator extends Entity {

    private int timeBetweenClient = 12;     // in min
    private int nbOfClientGenerated;
    private LocalDateTime nextClientTime;
    private Salon salon;

    public ClientGenerator(SimEngine simEngine, Salon salon) {
        super(simEngine);
        name = "Client Generator";
        this.salon = salon;
        this.nextClientTime = simEngine.getCurrentSimTime();
        // Update time next client is created
        updateNextClientTime();

    }

    @Override
    public void start() {
        simEngine.addEvent(new CreatingClientEvent());
    }

    private void updateNextClientTime() {
        this.nextClientTime = this.nextClientTime.plusMinutes(this.timeBetweenClient);
        // let's add randomness
        int offset = simEngine.getRandom().nextInt(6) - 6 / 2;
        nextClientTime = nextClientTime.plusMinutes(offset);
        if (!salon.isOpen() || !salon.acceptClients()) {
            this.nextClientTime = salon.getNextOpeningTime();
        }
    }

    public int getNbOfClientGenerated() {
        return nbOfClientGenerated;
    }

    @Override
    public void printStats() {
        super.printStats();
        System.out.println(String.format("%-30s: %20d", "Client generated", nbOfClientGenerated));
    }

    public void setFrequency(int frequencyInMin) {
        timeBetweenClient = frequencyInMin;
    }

    /*
    * ********************************************************************
    * Events
    * ********************************************************************
    */
    private class CreatingClientEvent extends Event {
        private Client createdClient;

        CreatingClientEvent() {
            super(ClientGenerator.this.name, nextClientTime, "Creating a Client ");
            String name = String.format("No-%03d", ClientGenerator.this.getNbOfClientGenerated());
            createdClient = new Client(simEngine, name, ClientGenerator.this.salon);
            setDescription("Creating " + createdClient.getName());
        }

        @Override
        public void doAction() {
            createdClient.arrivesAt(nextClientTime);
            ClientGenerator.this.nbOfClientGenerated += 1;
            // Now next Client
            updateNextClientTime();
            simEngine.addEvent(new CreatingClientEvent());
        }
    }
}
