package com.ejalaa.environment;

import com.ejalaa.logging.Logger;
import com.ejalaa.peoples.Client;
import com.ejalaa.simulation.Entity;
import com.ejalaa.simulation.Event;
import com.ejalaa.simulation.SimEngine;

import java.time.DayOfWeek;
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

    // Statistics
    private int openedDays;

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

        // Stat
        this.openedDays = 0;
    }

    @Override
    public void start() {
        simEngine.addEvent(getNextEvent());
    }

    private void updateNextOpeningTime() {
        int weekday = simEngine.getCurrentSimTime().getDayOfWeek().getValue();
        // opens only between
        if (DayOfWeek.TUESDAY.getValue() <= weekday && weekday < DayOfWeek.SATURDAY.getValue()) {
            if (simEngine.getCurrentSimTime().getHour() > 9) {
                nextOpeningTime = simEngine.getCurrentSimTime().plusDays(1);
            } else {
                // If the starting time of the simulation is before opening then we can open the same day
                nextOpeningTime = simEngine.getCurrentSimTime();
            }
        } else {
            nextOpeningTime = simEngine.getCurrentSimTime().plusWeeks(1).with(DayOfWeek.TUESDAY);

        }
        this.nextOpeningTime = this.nextOpeningTime.withHour(9).withMinute(0).withSecond(0);
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

    private Event getNextEvent() {
        if (isOpen) {
            return new CloseEvent();
        } else {
            return new OpenEvent();
        }
    }

    /*
    * ********************************************************************
    * Getter and Setter
    * ********************************************************************
    */

    public boolean isOpen() {
        return isOpen;
    }

    public ArrayList<Client> getWaitingClientList() {
        return this.waitingClientsList;
    }

    public int getOpenedDays() {
        return openedDays;
    }

    /*
    Defines what to do when a client enters the shop
     */
    public void handleClient(Client client) {
        this.waitingClientsList.add(client);
        this.clientHandled += 1;
        String str = String.format("Just handled %s. QUEUE_SIZE=%d", client.name, this.waitingClientsList.size());
        Logger.getInstance().log(name, client.getArrivedTime(), str);
        client.goAfter(this.hairDressingDuration);
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

    /*
    * ********************************************************************
    * Client Handling
    * ********************************************************************
    */

    public int getClientHandled() {
        return clientHandled;
    }

    /*
    * ********************************************************************
    * EVENT DEFINITIONS
    * ********************************************************************
    */
    private class OpenEvent extends Event {

        OpenEvent() {
            super(name, nextOpeningTime, "Opening Shop");
        }

        @Override
        public void doAction() {
            Salon.this.isOpen = true;
            Salon.this.openedDays += 1;
            Logger.getInstance().log(name, simEngine.getCurrentSimTime(), "Shop Opened");
            Salon.this.updateNextClosingTime();
            simEngine.addEvent(getNextEvent());
        }
    }

    private class CloseEvent extends Event {

        CloseEvent() {
            super(name, nextClosingTime, "Closing Shop");
        }

        @Override
        public void doAction() {
            Salon.this.isOpen = false;
            Logger.getInstance().log(name, simEngine.getCurrentSimTime(), "Shop Closed");
            Salon.this.updateNextOpeningTime();
            simEngine.addEvent(Salon.this.getNextEvent());
        }
    }
}
