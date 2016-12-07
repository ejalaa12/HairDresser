package com.ejalaa.environment;

import com.ejalaa.logging.Logger;
import com.ejalaa.peoples.Client;
import com.ejalaa.simulation.Entity;
import com.ejalaa.simulation.Event;
import com.ejalaa.simulation.SimEngine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

/**
 * The Salon class represents the place
 */
public class Salon extends Entity {

    /*
    * ********************************************************************
    * ATTRIBUTES
    * ********************************************************************
    */
    private static final String name = "COIFFURE";
    public static String address = "XVIe arrondissement, Paris";

    // State
    private boolean isOpen;
    private LocalDateTime nextOpeningTime, nextClosingTime;

    // Transitions
    private Event openingEvent, closingEvent;
    private Random rand;

    // Attributes about clients
    private int hairDressingDuration = 30;
    private ArrayList<Client> waitingClientsList;
    private int clientHandled;
    private Event clientArrived, clientHasFinished;

    public Salon(SimEngine simEngine) {
        super(simEngine);
        this.rand = simEngine.getRandom();
        this.waitingClientsList = new ArrayList<>();
        this.clientHandled = 0;

        // Hairdresser is closed at the beginning
        this.isOpen = false;

        // update opening and closing time for the first time
        updateNextOpeningTime();
        updateNextClosingTime();
        // updating event first time
        updateNextOpeningEvent();
        updateNextClosingEvent();
    }

    // TODO: 07/12/2016 make salon open only tuesday to saturday
    private void updateNextOpeningTime() {
        if (simEngine.getCurrentSimTime().getHour() > 9) {
            this.nextOpeningTime = simEngine.getCurrentSimTime().plusDays(1);
        } else {
            // If the starting time of the simulation is before opening then we can open the same day
            this.nextOpeningTime = simEngine.getCurrentSimTime();
        }
        this.nextOpeningTime = this.nextOpeningTime.withHour(9);
        this.nextOpeningTime = this.nextOpeningTime.withMinute(0);
        this.nextOpeningTime = this.nextOpeningTime.withSecond(0);
        // Let's add some randomness with an offset
        int offset = rand.nextInt(30) - 30 / 2;
        this.nextOpeningTime = this.nextOpeningTime.plusMinutes(offset);
    }

    private void updateNextClosingTime() {
        // next event is closing the same day at 21:00
        this.nextClosingTime = simEngine.getCurrentSimTime();
        this.nextClosingTime = this.nextClosingTime.withHour(21);
        this.nextClosingTime = this.nextClosingTime.withMinute(0);
        this.nextClosingTime = this.nextClosingTime.withSecond(0);
        // Let's add some randomness with an offset
        int offset = rand.nextInt(30) - 30 / 2;
        this.nextClosingTime = this.nextClosingTime.plusMinutes(offset);
    }

    private void updateNextOpeningEvent() {
        this.openingEvent = new Event(name, nextOpeningTime, "Asking for opening") {
            @Override
            public void doAction() {
                openingAction();
                addGeneratedEvent(getNextEvent());
            }
        };
    }

    private void updateNextClosingEvent() {
        this.closingEvent = new Event(name, nextClosingTime, "Asking for closing") {
            @Override
            public void doAction() {
                closingAction();
                addGeneratedEvent(getNextEvent());
            }
        };
    }

    private void openingAction() {
        //"Opening shop from SimEngine and send back openedEvent";
        this.isOpen = true;
        Logger.getInstance().log(name, simEngine.getCurrentSimTime(), "Shop opened");
        updateNextClosingTime();
        updateNextClosingEvent();
    }

    private void closingAction() {
        // Closing the shop
        this.isOpen = false;
        Logger.getInstance().log(name, simEngine.getCurrentSimTime(), "Shop closed");
        updateNextOpeningTime();
        updateNextOpeningEvent();
    }

    @Override
    public Event getNextEvent() {
        if (isOpen) {
            return this.closingEvent;
        } else {
            return this.openingEvent;
        }
    }

    /*
    * ********************************************************************
    * Getter and Setter
    * ********************************************************************
    */

    public LocalDateTime getNextOpeningTime() {
        return nextOpeningTime;
    }

    public LocalDateTime getNextClosingTime() {
        return nextClosingTime;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void showSchedule() {
        System.out.print("Current Time: ");
        System.out.println(simEngine.getCurrentSimTime());
        System.out.print("Next Opening: ");
        System.out.println(nextOpeningTime);
        System.out.print("Next Closing: ");
        System.out.println(nextClosingTime);
    }

    public ArrayList<Client> getWaitingClientList() {
        return this.waitingClientsList;
    }

    /*
    * ********************************************************************
    * Client Handling
    * ********************************************************************
    */

    /*
    Defines what to do when a client enters the shop
     */
    public void handleClient(Client client) {
        this.waitingClientsList.add(client);
        this.clientHandled += 1;
        client.setFinishedTime(client.getArrivedTime().plusMinutes(this.hairDressingDuration));
        String str = String.format("Just handled %s. QUEUE_SIZE=%d", client.name, this.waitingClientsList.size());
        Logger.getInstance().log(name, client.getArrivedTime(), str);
    }

    /*
    Defines what to do when a client has finished
     */
    public void letGo(Client client) {
        if (this.waitingClientsList.contains(client)) {
            this.waitingClientsList.remove(client);
            String str = String.format("Finished with %s. QUEUE_SIZE=%d", client.name, this.waitingClientsList.size());
            Logger.getInstance().log(name, client.getFinishedTime(), str);
        } else {
            throw new ArrayStoreException();
        }
    }

    public int getClientHandled() {
        return clientHandled;
    }
}
