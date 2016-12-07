package com.ejalaa.environment;

import com.ejalaa.logging.Logger;
import com.ejalaa.peoples.Client;
import com.ejalaa.simulation.Entity;
import com.ejalaa.simulation.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

/**
 * The Salon class represents the place
 */
public class Salon extends Entity {

    private static final String name = "COIFFURE";
    public static String address = "XVIe arrondissement, Paris";
    private int hairDressingDuration = 30;
    // State
    private boolean isOpen;
    private LocalDateTime nextOpeningTime, nextClosingTime, currentTime;
    // Transitions
    private Event openingEvent, closingEvent;
    private Random rand;
    // Attributes about clients
    private ArrayList<Client> waitingClientsList;
    private int clientHandled;
    private Event clientArrived, clientHasFinished;

    public Salon(LocalDateTime simStartTime) {
        this.rand = new Random();
        this.waitingClientsList = new ArrayList<>();
        this.clientHandled = 0;
        // Hairdresser is closed at the beginning
        this.isOpen = false;
        this.currentTime = simStartTime;
        // Updating time
        updateNextOpeningTime();
        updateNextClosingTime();
        // Creating first events
        updateNextOpeningEvent();
        updateNextClosingEvent();
        Logger.getInstance().log(name, this.currentTime, "Shop created");
    }

    private void updateNextOpeningTime() {
        if (this.currentTime.getHour() > 9) {
            this.nextOpeningTime = this.currentTime.plusDays(1);
        } else {
            // If the starting time of the simulation is before opening then we can open the same day
            this.nextOpeningTime = this.currentTime;
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
        this.nextClosingTime = this.currentTime;
        this.nextClosingTime = this.nextClosingTime.withHour(21);
        this.nextClosingTime = this.nextClosingTime.withMinute(0);
        this.nextClosingTime = this.nextClosingTime.withSecond(0);
        // Let's add some randomness with an offset
        int offset = rand.nextInt(30) - 30 / 2;
        this.nextClosingTime = this.nextClosingTime.plusMinutes(offset);
    }

    private void updateNextOpeningEvent() {
        this.openingEvent = new Event("Asking for opening", nextOpeningTime, name) {
            @Override
            public void doAction() {
                openingAction(getScheduledTime());
                addGeneratedEvent(getNextEvent());
            }
        };
    }

    private void updateNextClosingEvent() {
        this.closingEvent = new Event("Asking for closing", nextClosingTime, name) {
            @Override
            public void doAction() {
                closingAction(getScheduledTime());
                addGeneratedEvent(getNextEvent());
            }
        };
    }

    private void openingAction(LocalDateTime eventTime) {
        //"Opening shop from SimEngine and send back openedEvent";
        this.isOpen = true;
        this.currentTime = eventTime;
        Logger.getInstance().log(name, eventTime, "Shop opened");
        updateNextClosingTime();
        updateNextClosingEvent();
    }

    private void closingAction(LocalDateTime eventTime) {
        // Closing the shop
        this.isOpen = false;
        this.currentTime = eventTime;
        Logger.getInstance().log(name, eventTime, "Shop closed");
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

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void showSchedule() {
        System.out.print("Current Time: ");
        System.out.println(this.currentTime);
        System.out.print("Next Opening: ");
        System.out.println(this.nextOpeningTime);
        System.out.print("Next Closing: ");
        System.out.println(this.nextClosingTime);
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
