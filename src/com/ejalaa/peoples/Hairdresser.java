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
    private int hairDressingDuration = 30;
    private int clientHandled;
    private Salon salon;

    public Hairdresser(SimEngine simEngine, String name, Salon salon) {
        super(simEngine, name);
        this.free = true;
        this.salon = salon;
        clientHandled = 0;
    }

    @Override
    public void start() {

    }

    public void handleClient(Client client) {
        free = false;
        clientHandled += 1;
        String msg = String.format("Taking care of %s", client.getName());
        client.setGettingHairdress();
        Logger.getInstance().log(name, simEngine.getCurrentSimTime(), msg);
        simEngine.addEvent(new HairdressingFinishedEvent(client));
//        client.goAfter(hairDressingDuration);
    }

    public void finishedWithClient() {
        free = true;
    }

    public boolean isFree() {
        return free;
    }

    public int getClientHandled() {
        return clientHandled;
    }

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
                Hairdresser.this.salon.incClientHandled();
            }

        }
    }
}
