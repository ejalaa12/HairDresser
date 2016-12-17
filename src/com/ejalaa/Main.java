package com.ejalaa;

import com.ejalaa.environment.ClientGenerator;
import com.ejalaa.environment.Salon;
import com.ejalaa.logging.Logger;
import com.ejalaa.peoples.Client;
import com.ejalaa.peoples.Hairdresser;
import com.ejalaa.simulation.SimEngine;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main {

    private static final long seed = 1;

    public static void main(String[] args) {
        Main m = new Main();
        m.realMain();
//        m.test();
//        m.test2();

    }

    private void test2() {

        LocalDateTime simStart = LocalDateTime.of(2016, 11, 10, 8, 45);
        LocalDateTime simulationEndTime = LocalDateTime.of(2017, 12, 14, 21, 45);
        Duration x = Duration.between(simStart, simulationEndTime);
        System.out.println(x);
        System.out.println(x.plus(x));
        System.out.println(x.dividedBy(2));
    }

    private void test() {
        Box box1 = new Box("bob");
        Box box2 = new Box("alice");
        Box box3 = new Box("thomas");
        box1.open();
        box1.remove();
        box2.close();
        box2.put();
        box3.open();
        box3.put();
        ArrayList<Box> boxes = new ArrayList<>();
        boxes.add(box1);
        boxes.add(box2);
        boxes.add(box3);
        Box res = boxes.stream().filter(Box::isOpen).filter(Box::isFull).findFirst().orElse(new Box("null"));
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
        sandou.setLaziness(0.5);
        salon.addHairdresser(sandou);
        Hairdresser Pushmina = new Hairdresser(simEngine, "Pushmina", salon);
        Pushmina.setLaziness(0.5);
        salon.addHairdresser(Pushmina);
//        Hairdresser Traore = new Hairdresser(simEngine, "Traore", salon);
//        Traore.setLaziness(0.4);
//        salon.addHairdresser(Traore);
        ClientGenerator clientGenerator = new ClientGenerator(simEngine, salon);
        clientGenerator.setFrequency(12);
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
        printSep(40, "-");
        sandou.printStats();
        printSep(40, "-");
        Pushmina.printStats();
//        printSep(40, "-");
//        Traore.printStats();
        printSep(60, "=");

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
        private boolean open, empty;

        public Box(String name) {
            this.name = name;
            open = false;
            empty = true;
        }

        public void close() {
            open = false;
        }

        public void open() {
            open = true;
        }

        public void put() {
            empty = false;
        }

        public void remove() {
            empty = true;
        }

        public boolean isEmpty() {
            return empty;
        }

        public boolean isFull() {
            return !empty;
        }

        public boolean isOpen() {
            return open;
        }

        public boolean isClosed() {
            return !open;
        }
    }
}
