package com.ejalaa.environment;

import com.ejalaa.environment.salonEvents.CloseEvent;
import com.ejalaa.environment.salonEvents.OpenEvent;
import com.ejalaa.logging.Logger;
import com.ejalaa.peoples.Client;
import com.ejalaa.peoples.Hairdresser;
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
    public static String address = "XVIe arrondissement, Paris";

    // State
    private boolean isOpen;
    private LocalDateTime nextOpeningTime, nextClosingTime;

    // Transitions
    private Random rand;

    // Attributes about clients
    private ArrayList<Client> waitingClientsList;

    // Statistics
    private int clientHandled, clientLost;
    private int openedDays, completedDays;

    // Workers
    private ArrayList<Hairdresser> hairdressers;

    public Salon(SimEngine simEngine) {
        super(simEngine);
        name = "SALON";
        rand = simEngine.getRandom();
        waitingClientsList = new ArrayList<>();
        hairdressers = new ArrayList<>();

        // Hairdresser is closed at the beginning
        isOpen = false;

        // update opening and closing time for the first time
        updateNextOpeningTime();
        updateNextClosingTime();

        // Stat
        openedDays = 0;
        completedDays = 0;
        clientHandled = 0;
        clientLost = 0;

    }

    public String getName() {
        return name;
    }

    @Override
    public void start() {
        simEngine.addEvent(getNextEvent());
    }

    public void updateNextOpeningTime() {
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

    public void updateNextClosingTime() {
        // next event is closing the same day at 21:00
        this.nextClosingTime = simEngine.getCurrentSimTime();
        this.nextClosingTime = this.nextClosingTime.withHour(21);
        this.nextClosingTime = this.nextClosingTime.withMinute(0);
        this.nextClosingTime = this.nextClosingTime.withSecond(0);
        // Let's add some randomness with an offset
        int offset = rand.nextInt(30) - 30 / 2;
        this.nextClosingTime = this.nextClosingTime.plusMinutes(offset);
    }

    public Event getNextEvent() {
        if (isOpen) {
//            return new CloseEvent();
            return new CloseEvent(this);
        } else {
//            return new OpenEvent();
            return new OpenEvent(this);
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
        Hairdresser freeHairdresser = getFreeHairDresser();
        if (freeHairdresser != null) {
            freeHairdresser.handleClient(client);
            clientHandled += 1;
        } else if (waitingClientsList.size() < client.getQueueSizePatience()) {
            client.setWating();
            waitingClientsList.add(client);
            String str = String.format("%s added to waiting list (%d)", client.name, waitingClientsList.size());
            Logger.getInstance().log(name, client.getArrivedTime(), str);
        } else {
            client.LeaveFull();
            clientLost += 1;
            String str = String.format("Couldn't handle client %s too much people already waiting (%d)", client.name, waitingClientsList.size());
            Logger.getInstance().log(name, client.getArrivedTime(), str);
        }
    }

    public LocalDateTime getNextOpeningTime() {
        return nextOpeningTime;
    }

    public void addHairdresser(Hairdresser newHairdresser) {
        hairdressers.add(newHairdresser);
    }

    /*
    * ********************************************************************
    * Client Handling
    * ********************************************************************
    */

    public int getClientHandled() {
        return clientHandled;
    }

    public SimEngine getSimEngine() {
        return simEngine;
    }

    public void open() {
        isOpen = true;
        openedDays += 1;
    }

    public void close() {
        isOpen = false;
        completedDays += 1;
    }

    public LocalDateTime getNextClosingTime() {
        return nextClosingTime;
    }

    public void printStats() {
        super.printStats();
        System.out.println(String.format("%-30s: %20d", "Opened days", openedDays));
        System.out.println(String.format("%-30s: %20d", "Completed days", completedDays));
        System.out.println(String.format("%-30s: %20d", "Client handled", clientHandled));
        for (Hairdresser h : hairdressers) {
            System.out.println(String.format("%30s: %20d", h.name, h.getClientHandled()));
        }
        System.out.println(String.format("%-30s: %20d", "Client Lost", clientLost));
    }

    private Hairdresser getFreeHairDresser() {
        return hairdressers.stream().filter(Hairdresser::isFree).findFirst().orElse(null);
    }

    public void incClientHandled() {
        clientHandled += 1;
    }
}
