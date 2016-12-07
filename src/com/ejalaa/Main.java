package com.ejalaa;

import com.ejalaa.environment.ClientGenerator;
import com.ejalaa.environment.Salon;
import com.ejalaa.logging.Logger;
import com.ejalaa.peoples.Client;
import com.ejalaa.simulation.SimEngine;

import java.time.LocalDateTime;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Main m = new Main();
        m.realMain();

    }

    public void playingWithTime() {
        LocalDateTime simStart = LocalDateTime.of(2016, 11, 29, 21, 45);
        LocalDateTime simStart2 = simStart;
        simStart2 = simStart2.plusDays(1);
        simStart2 = simStart2.withHour(8);
        simStart2 = simStart2.withMinute(0);
        simStart2 = simStart2.withSecond(0);
        Logger.getInstance().log(simStart);
        Logger.getInstance().log(simStart2);
    }

    public void playingWithRandom() {
        Random r = new Random();
        int lr = 30;
        for (int i = 0; i < 100; i++) {
            System.out.println(r.nextInt(lr) - lr / 2);
        }

    }

    private void realMain() {
        Logger.getInstance().log(LocalDateTime.now());
        // Start time of simulation (datetime of first event)
        LocalDateTime simStart = LocalDateTime.of(2016, 11, 10, 8, 45);
        // End time of simulation is 29/12/2016 at 21:45
        LocalDateTime simulationEndTime = LocalDateTime.of(2016, 12, 10, 20, 45);
        SimEngine simEngine = new SimEngine(simulationEndTime);
        // Opening time of the hairdresser 10/12/2016 at 21:45
        Salon salon = new Salon(simStart);
        // Client Generator
        ClientGenerator clientGenerator = new ClientGenerator(simStart) {

            @Override
            public void createNewClientAction(String name, LocalDateTime timeToAppear) {
                Client client = new Client(name, timeToAppear) {
                    @Override
                    protected boolean hairdresserIsOpen() {
                        return salon.isOpen();
                    }

                    @Override
                    protected boolean lowQueue() {
                        return salon.getWaitingClientList().size() < this.getMaxWaitingQueueToStay();
                    }

                    @Override
                    protected void handleMe() {
                        salon.handleClient(this);
                    }

                    @Override
                    protected void letMeGo() {
                        salon.letGo(this);
                    }
                };
                simEngine.addEvent(client.getNextEvent());
            }
        };


        simEngine.addEvent(salon.getNextEvent());
        simEngine.addEvent(clientGenerator.getNextEvent());

        simEngine.loop();
        Logger.getInstance().log(LocalDateTime.now());
        System.out.println(salon.getClientHandled());
    }
}
