package com.ejalaa;

import com.ejalaa.environment.ClientGenerator;
import com.ejalaa.environment.Salon;
import com.ejalaa.logging.Logger;
import com.ejalaa.peoples.Hairdresser;
import com.ejalaa.simulation.SimEngine;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main {

    private static final long seed = 1;

    public static void main(String[] args) {
        Main m = new Main();
        m.realMain();
//        m.test();

    }

    private void test() {
        Box box1 = new Box("bob");
        Box box2 = new Box("alice");
        box1.open();
        ArrayList<Box> boxes = new ArrayList<>();
        boxes.add(box1);
        boxes.add(box2);
        Box res = boxes.stream().filter(Box::isOpen).findFirst().orElse(new Box("null"));
        System.out.println(res.name);
    }

    private void realMain() {
        Logger.getInstance().log(LocalDateTime.now());
        /*
        * ********************************************************************
        * Starting and ending time of simulation
        * ********************************************************************
        */
        LocalDateTime simStart = LocalDateTime.of(2016, 11, 10, 8, 45);
        LocalDateTime simulationEndTime = LocalDateTime.of(2016, 12, 10, 21, 45);
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
        Salon salon = new Salon(simEngine);
//        Client bob = new Client(simEngine, "Bob", salon);
//        bob.setArrivedTime(LocalDateTime.of(2016, 11, 18, 10, 45));
//        bob.start();
        Hairdresser Sandou = new Hairdresser(simEngine, "Sandou", salon);
        salon.addHairdresser(Sandou);
        ClientGenerator clientGenerator = new ClientGenerator(simEngine, salon);
//        bob.arrivesAt(LocalDateTime.of(2016, 12, 10, 15, 0));
        salon.start();
        clientGenerator.start();

        Logger.getInstance().turnOn();
        Logger.getInstance().turnCsvOn();
        simEngine.loop();
        Logger.getInstance().log(LocalDateTime.now());

        System.out.println("=======================================================");
        System.out.println("SIMULATION RESULTS");
        System.out.println("=======================================================");
        salon.printStats();
        printSep(40, "-");
        clientGenerator.printStats();
        printSep(50, "=");

        try {
            Logger.getInstance().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printSep(int x, String c) {
        System.out.println(new String(new char[x]).replace("\0", c));
    }

    private class Box {
        public String name;
        private boolean open;

        public Box(String name) {
            this.name = name;
            open = false;
        }

        public void close() {
            open = false;
        }

        public void open() {
            open = true;
        }

        public boolean isOpen() {
            return open;
        }
    }
}
