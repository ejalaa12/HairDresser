package com.ejalaa.environment;

import com.ejalaa.peoples.Hairdresser;

import java.util.ArrayList;

/**
 * Created by ejalaa on 02/12/2016.
 */
public class Environment {

    private Salon salon;
    private ArrayList<Hairdresser> hairdressers, clients;

    public Environment(ArrayList<Hairdresser> hairdressers) {
        super();
        this.hairdressers = hairdressers;
//        this.salon = new Salon();
    }

    public Environment(Salon salon) {
        super();
        this.salon = salon;
        this.hairdressers = new ArrayList<>();
    }
}
