package com.ejalaa.environment;

import com.ejalaa.environment.salonEvents.ClosingEvent;
import com.ejalaa.environment.salonEvents.OpenEvent;
import com.ejalaa.logging.Logger;
import com.ejalaa.peoples.Client;
import com.ejalaa.peoples.Customer;
import com.ejalaa.peoples.Hairdresser;
import com.ejalaa.simulation.Entity;
import com.ejalaa.simulation.Event;
import com.ejalaa.simulation.SimEngine;

import java.time.DayOfWeek;
import java.time.Duration;
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
    private SalonState salonState;
    private boolean acceptClients;
    private LocalDateTime nextOpeningTime, nextClosingTime;
    // Transitions
    private Random rand;
    // Attributes about clients
    private ArrayList<Client> waitingClientsList;
    // Statistics
    private int clientHandled, clientLost, finishedClient;
    private int openedDays, completedDays;
    private Duration averageWaitingTime, delayAfterClosing;
    // Workers
    private ArrayList<Hairdresser> hairdressers;
    private int clientDuringCloseTime;


    public Salon(SimEngine simEngine) {
        super(simEngine);
        name = "SALON";
        rand = simEngine.getRandom();
        waitingClientsList = new ArrayList<>();
        hairdressers = new ArrayList<>();

        // Hairdresser is closed at the beginning
        salonState = SalonState.Closed;
        // And therefore accepts no clients
        acceptClients = false;

        // update opening and closing time for the first time
        updateNextOpeningTime();
        updateNextClosingTime();

        // Stat
        openedDays = 0;
        completedDays = 0;
        clientHandled = 0;
        clientLost = 0;
        clientDuringCloseTime = 0;
        finishedClient = 0;
        averageWaitingTime = Duration.ZERO;
        delayAfterClosing = Duration.ZERO;
    }

    public boolean acceptClients() {
        return acceptClients;
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
        switch (salonState) {
            case Open:
                return new ClosingEvent(this);
            case Closing:
                return null;
            case Closed:
                return new OpenEvent(this);
            default:
                return null;
        }
    }

    public String getName() {
        return name;
    }

    /*
    * ********************************************************************
    * Getter and Setter
    * ********************************************************************
    */

    public boolean isOpen() {
        return salonState == SalonState.Open;
    }

    public ArrayList<Client> getWaitingClientList() {
        return this.waitingClientsList;
    }

    public int getOpenedDays() {
        return openedDays;
    }

    public Duration getDelayAfterClosing() {
        return delayAfterClosing;
    }

    /*
        Defines what to do when a client enters the shop
         */
    public void handleClient(Client client) {
        // Handling client
        if (anyHairDresserIsPresent()) {
            Hairdresser freeHairdresser = getFreeHairDresser();
            if (freeHairdresser != null) {
                // if a hairdresser is free, get hairdressing by him
                freeHairdresser.handleClient(client);
            } else if (waitingClientsList.size() < client.getQueueSizePatience()) {
                // if no hairdresser is free, wait if the waiting queue is low
                client.setWaiting();
                waitingClientsList.add(client);
                String str = String.format("%s added to waiting list (%d)", client.getName(), waitingClientsList.size());
                Logger.getInstance().log(name, client.getArrivedTime(), str);
            } else {
                // else just leave not happy
                client.leaveBecauseLongQueue();
                clientLost += 1;
                String str = String.format("Couldn't handle client %s too much people already waiting (%d)", client.getName(), waitingClientsList.size());
                Logger.getInstance().log(name, client.getArrivedTime(), str);
            }
        } else {
            // else just leave not happy
            clientLost += 1;
            String str = "Sorry no hairdressers today...";
            Logger.getInstance().log(name, client.getArrivedTime(), str);
            client.leaveBecauseNoHairdressers();
        }
    }

    private boolean anyHairDresserIsPresent() {
        for (Hairdresser h : hairdressers) {
            if (h.isPresent()) {
                return true;
            }
        }
        return false;
    }

    private int waitingListFor(Hairdresser fav) {
        int count = 0;
        for (Client c : waitingClientsList) {
            if ((c instanceof Customer) && (((Customer) c).getFavorite() == fav)) {
                count += 1;
            }
        }
        return count;
    }

    public LocalDateTime getNextOpeningTime() {
        return nextOpeningTime;
    }

    public void addHairdresser(Hairdresser newHairdresser) {
        hairdressers.add(newHairdresser);
    }

    public int getClientHandled() {
        return clientHandled;
    }

    public void setClientHandled(int clientHandled) {
        this.clientHandled = clientHandled;
    }

    public SimEngine getSimEngine() {
        return simEngine;
    }

    public LocalDateTime getNextClosingTime() {
        return nextClosingTime;
    }

    private Hairdresser getFreeHairDresser() {
        return hairdressers.stream().filter(Hairdresser::isPresent).filter(Hairdresser::isFree).findFirst().orElse(null);
    }

    public void open() {
        salonState = SalonState.Open;
        acceptClients = true;
        openedDays += 1;
    }

    /*
    * ********************************************************************
    * Client Handling
    * ********************************************************************
    */

    public void close() {
        // TODO: 18/12/2016 save closing time
        salonState = SalonState.Closed;
        completedDays += 1;
        updateAverageDelay();
        simEngine.addEvent(getNextEvent());
    }

    private void updateAverageDelay() {
        Duration delay = Duration.between(nextClosingTime, simEngine.getCurrentSimTime());
        if (completedDays == 1) {
            delayAfterClosing = delay;
        } else {
            delayAfterClosing = delayAfterClosing.multipliedBy(completedDays - 1);
            delayAfterClosing = delayAfterClosing.plus(delay);
            delayAfterClosing = delayAfterClosing.dividedBy(completedDays);
        }
    }

    public void printStats() {
        super.printStats();
        System.out.println(String.format("%-30s: %20d", "Opened days", openedDays));
        System.out.println(String.format("%-30s: %20d", "Completed days", completedDays));
        System.out.println(String.format("%-30s: %20d", "Client handled", clientHandled));
        for (Hairdresser h : hairdressers) {
            System.out.println(String.format("%30s: %20d", h.getName(), h.getClientHandled()));
        }
        System.out.println(String.format("%-30s: %20d", "Client Lost", clientLost));
        System.out.println(String.format("%-30s: %20d", "Client during close", clientDuringCloseTime));
        System.out.println(String.format("%-30s: %20s", "Average waiting time", averageWaitingTime.toString()));
        System.out.println(String.format("%-30s: %20s", "Average delay after closing", delayAfterClosing.toString()));
        System.out.println(String.format("%-30s: %20d", "BENEFIT", calculateBenefit()));
    }

    public void makeTheCall() {
        String msg = "Morning call: ";
        for (Hairdresser h :
                hairdressers) {
            h.call();
            msg += String.format("%s (%b)\t", h.getName(), h.isPresent());
        }
        Logger.getInstance().log(name, simEngine.getCurrentSimTime(), msg);
    }

    public void clientCameWhenClosed() {
        clientDuringCloseTime += 1;
    }

    public void notifyClientLeaving(Client client) {
        if (finishedClient != 0) {
            averageWaitingTime = averageWaitingTime.multipliedBy(finishedClient);
            averageWaitingTime = averageWaitingTime.plus(client.getWaitingTime());
            finishedClient += 1;
            averageWaitingTime = averageWaitingTime.dividedBy(finishedClient);
        } else {
            averageWaitingTime = client.getWaitingTime();
            finishedClient += 1;
        }
        closeIfLastClientOfTheDay();
    }

    private void closeIfLastClientOfTheDay() {
        // if its the last client leaving then all hairdresser must be free and the queue is empty
        boolean lastClient = salonState == SalonState.Closing && allHairDresserAreFree() && waitingClientsList.isEmpty();
        if (lastClient) {
            close();
            Logger.getInstance().log(name, simEngine.getCurrentSimTime(), "Last client ! Salon Closed");
        }
    }

    private boolean allHairDresserAreFree() {
        for (Hairdresser h : hairdressers) {
            if (!h.isFree()) {
                return false;
            }
        }
        return true;
    }

    private int calculateBenefit() {
        int expensePerOpeningDay = 100;
        int incomePerClientHandled = 23;
        int dailySalary = 100;
        int totalIncome = clientHandled * incomePerClientHandled;
        int totalWorkedDays = 0;
        for (Hairdresser hairdresser : hairdressers) {
            totalWorkedDays += hairdresser.getWorkedDays();
        }
        int totalExpense = openedDays * expensePerOpeningDay + dailySalary * totalWorkedDays;

        int benefit = totalIncome - totalExpense;
        return benefit;
    }

    public void stopAcceptingClients() {
        acceptClients = false;
        salonState = SalonState.Closing;
    }

    // State
    private enum SalonState {
        Open, Closing, Closed
    }
}
