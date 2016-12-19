package com.ejalaa.peoples;

import com.ejalaa.environment.Salon;
import com.ejalaa.logging.Logger;
import com.ejalaa.simulation.Event;
import com.ejalaa.simulation.SimEngine;

/**
 * This class defines a Hairdresser that works at the Hairdresser
 */
public class Hairdresser extends People {

    private boolean free;
    private int hairDressingDuration = 19;
    private int clientHandled;
    private Salon salon;
    private boolean isPresent;
    private int workedDays, notWorkedDays;
    private double laziness;

    /*
    * ****************************************************************************************************************
    * IMPORTANT STUFF
    * ****************************************************************************************************************
    */

    public Hairdresser(SimEngine simEngine, String name, Salon salon) {
        super(simEngine, name);
        this.free = true;
        this.salon = salon;
        clientHandled = 0;
    }

    @Override
    public void start() {

    }

    public void call() {
//        70 % of the time the hairdresser is present
//        this check is done every opening of the salon
        isPresent = simEngine.getRandom().nextFloat() > laziness;
        if (isPresent) {
            workedDays += 1;
        } else {
            notWorkedDays += 1;
        }
    }

    public void handleClient(Client client) {
        free = false;
        clientHandled += 1;
        salon.setClientHandled(salon.getClientHandled() + 1);

        // logging
        String msg = String.format("Taking care of %s", client.getName());
        client.setGettingHairdress();
        Logger.getInstance().log(name, simEngine.getCurrentSimTime(), msg);

        // Next event for the hairdresser is finishing hairdressing
        simEngine.addEvent(new HairdressingFinishedEvent(client));
//        client.goAfter(hairDressingDuration);
    }

    /*
    * ****************************************************************************************************************
    * GETTER AND SETTERS
    * ****************************************************************************************************************
    */

    public boolean isFree() {
        return free;
    }

    public int getClientHandled() {
        return clientHandled;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setLaziness(double laziness) {
        this.laziness = laziness;
    }

    public void setHairDressingDuration(int hairDressingDuration) {
        this.hairDressingDuration = hairDressingDuration;
    }

    public int getWorkedDays() {
        return workedDays;
    }

    /*
    * ****************************************************************************************************************
    * PRINTING
    * ****************************************************************************************************************
    */

    @Override
    public void printStats() {
        super.printStats();
        float tot = workedDays + notWorkedDays;
        System.out.println(String.format("%-30s: %20.2f %%", "Worked days", 100 * workedDays / tot));
    }

    @Override
    public String toString() {
        return name;
    }

    /*
    * ****************************************************************************************************************
    * EVENTS
    * ****************************************************************************************************************
    */

    private class HairdressingFinishedEvent extends Event {
        private Client currentClient;

        protected HairdressingFinishedEvent(Client currentClient) {
            super(Hairdresser.this.name, Hairdresser.this.simEngine.getCurrentSimTime().plusMinutes(hairDressingDuration), "Hairdressing finished");
            this.currentClient = currentClient;
        }

        @Override
        public void doAction() {
            Hairdresser.this.free = true;
            currentClient.hairdressingFinished();
            if (Hairdresser.this.salon.getWaitingClientList().size() > 0) {
                Client nextClient = Hairdresser.this.salon.getWaitingClientList().get(0);
                Hairdresser.this.salon.getWaitingClientList().remove(0);
                Hairdresser.this.handleClient(nextClient);
            }

        }
    }
}
