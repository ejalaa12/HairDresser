package com.ejalaa.environment;

import com.ejalaa.logging.Logger;
import com.ejalaa.simulation.Entity;
import com.ejalaa.simulation.Event;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * The HairDresser class represents the place
 */
public class HairDresser extends Entity {

    private static final String name = "COIFFURE";
    public static String address = "XVIe arrondissement, Paris";
    // State
    private boolean isOpen;
    private LocalDateTime nextOpeningTime, nextClosingTime, currentTime;
    // Transitions
    private Event openingEvent, closingEvent;
    private Random rand;

    public HairDresser(LocalDateTime simStartTime) {
        this.rand = new Random();
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

}
