package com.ejalaa.environment;

import com.ejalaa.peoples.Worker;

import java.util.ArrayList;

/**
 * Created by ejalaa on 02/12/2016.
 */
public class Environment {

    private Salon salon;
    private ArrayList<Worker> workers, clients;

    public Environment(ArrayList<Worker> workers) {
        super();
        this.workers = workers;
//        this.salon = new Salon();
    }

    public Environment(Salon salon) {
        super();
        this.salon = salon;
        this.workers = new ArrayList<>();
    }
}
