package com.ejalaa;

import com.ejalaa.environment.HairDresser;
import com.ejalaa.simulation.SimEngine;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        Main m = new Main();
        m.realMain();

    }

    private void realMain() {
        // Real start time
        long lStartTime = System.nanoTime();

        // Start time of simulation (datetime of first event)
        LocalDateTime simStart = LocalDateTime.of(2016, 11, 10, 8, 45);
        // End time of simulation is 29/12/2016 at 21:45
        LocalDateTime simulationEndTime = LocalDateTime.of(2016, 11, 11, 20, 45);
        SimEngine simEngine = new SimEngine(simulationEndTime);
        // Opening time of the hairdresser is simStart
        HairDresser hairDresser = new HairDresser(simStart);

        // Add the first Event to the simulation Engine
        simEngine.addEvent(hairDresser.getNextEvent());

        // Start the simulation
        simEngine.loop();

        // Real end time
        long lEndTime = System.nanoTime();
        //time elapsed
        long output = lEndTime - lStartTime;
        System.out.println("Elapsed time: " + output / 1000000 + "ms");
    }
}
