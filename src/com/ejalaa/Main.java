package com.ejalaa;

import com.ejalaa.environment.Salon;
import com.ejalaa.logging.Logger;
import com.ejalaa.simulation.SimEngine;

import java.time.LocalDateTime;

public class Main {

    private static final long seed = 1;

    public static void main(String[] args) {
        Main m = new Main();
        m.realMain();

    }

    private void realMain() {
        Logger.getInstance().log(LocalDateTime.now());
        // Start time of simulation (datetime of first event)
        LocalDateTime simStart = LocalDateTime.of(2016, 11, 10, 8, 45);
        // End time of simulation is 29/12/2016 at 21:45
        LocalDateTime simulationEndTime = LocalDateTime.of(2016, 12, 10, 20, 45);
        SimEngine simEngine = new SimEngine(seed, simStart, simulationEndTime);
        // Opening time of the hairdresser 10/12/2016 at 21:45
        Salon salon = new Salon(simEngine);
        // Client Generator


        simEngine.addEvent(salon.getNextEvent());

        simEngine.loop();
        Logger.getInstance().log(LocalDateTime.now());
        System.out.println(salon.getClientHandled());
    }
}
