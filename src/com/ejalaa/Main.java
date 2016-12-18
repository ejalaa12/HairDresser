package com.ejalaa;

import com.ejalaa.environment.ClientGenerator;
import com.ejalaa.environment.Salon;
import com.ejalaa.logging.Logger;
import com.ejalaa.peoples.Client;
import com.ejalaa.peoples.Hairdresser;
import com.ejalaa.simulation.SimEngine;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {

    private static final long seed = 1;

    public static void main(String[] args) {
        Main m = new Main();
        m.realMain();

    }

    private void printSep(int x, String c) {
        System.out.println(new String(new char[x]).replace("\0", c));
    }

    private void realMain() {
        Logger.getInstance().log(LocalDateTime.now());
        /*
        * ********************************************************************
        * Starting and ending time of simulation
        * ********************************************************************
        */
        LocalDateTime simStart = LocalDateTime.of(2016, 11, 10, 8, 45);
        LocalDateTime simulationEndTime = LocalDateTime.of(2016, 12, 14, 21, 45);
        /*
        * ********************************************************************
        * Simulator Engine
        * ********************************************************************
        */
        SimEngine simEngine = new SimEngine(seed, simStart, simulationEndTime);
        /*
        * ********************************************************************
        * Creation of entities
        * ********************************************************************
        */
        // Salon -------------------------------------------------------------
        Salon salon = new Salon(simEngine);
        // Client ------------------------------------------------------------
        Client bob = new Client(simEngine, "Bob", salon);
        bob.setArrivedTime(LocalDateTime.of(2016, 11, 18, 10, 45));
        bob.start();
        // Hairdressers ------------------------------------------------------
        Hairdresser sandou = new Hairdresser(simEngine, "Sandou", salon);
        sandou.setLaziness(0.1);
        salon.addHairdresser(sandou);
        Hairdresser Pushmina = new Hairdresser(simEngine, "Pushmina", salon);
        Pushmina.setLaziness(0.1);
        salon.addHairdresser(Pushmina);
        Hairdresser Traore = new Hairdresser(simEngine, "Traore", salon);
        Traore.setLaziness(0);
        salon.addHairdresser(Traore);
        ClientGenerator clientGenerator = new ClientGenerator(simEngine, salon);
        clientGenerator.setFrequency(12);
        salon.start();
        clientGenerator.start();

        /*
        * ****************************************************************************************************************
        * LOGGER SETTINGS
        * ****************************************************************************************************************
        */

        Logger.getInstance().turnOff();
        Logger.getInstance().turnCsvOn();

        /*
        * ****************************************************************************************************************
        * SIMULATION
        * ****************************************************************************************************************
        */
        simEngine.loop();
        Logger.getInstance().log(LocalDateTime.now());

        /*
        * ****************************************************************************************************************
        * RESULTS
        * ****************************************************************************************************************
        */
        System.out.println("=======================================================");
        System.out.println("SIMULATION RESULTS");
        System.out.println("=======================================================");
        salon.printStats();
        printSep(40, "-");
        clientGenerator.printStats();
        printSep(40, "-");
        sandou.printStats();
        printSep(40, "-");
        Pushmina.printStats();
        printSep(40, "-");
        Traore.printStats();
        printSep(60, "=");

        try {
            Logger.getInstance().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
