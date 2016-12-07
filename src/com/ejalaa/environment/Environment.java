package com.ejalaa.environment;

import com.ejalaa.peoples.Worker;

import java.util.ArrayList;

/**
 * Created by ejalaa on 02/12/2016.
 */
public class Environment {

    private HairDresser hairDresser;
    private ArrayList<Worker> workers, clients;

    public Environment(ArrayList<Worker> workers) {
        super();
        this.workers = workers;
//        this.hairDresser = new HairDresser();
    }

    public Environment(HairDresser hairDresser) {
        super();
        this.hairDresser = hairDresser;
        this.workers = new ArrayList<>();
    }
}
