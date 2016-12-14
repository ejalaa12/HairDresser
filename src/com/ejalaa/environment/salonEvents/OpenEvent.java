package com.ejalaa.environment.salonEvents;

import com.ejalaa.environment.Salon;
import com.ejalaa.logging.Logger;
import com.ejalaa.simulation.Event;

/**
 * Created by ejalaa on 14/12/2016.
 */
public class OpenEvent extends Event {

    private Salon salon;

    public OpenEvent(Salon salon) {
        super(salon.getName(), salon.getNextOpeningTime(), "Opening Salon");
        this.salon = salon;
    }

    @Override
    public void doAction() {
        salon.open();
        Logger.getInstance().log(salon.getName(), salon.getSimEngine().getCurrentSimTime(), "Salon Opened");
        salon.updateNextClosingTime();
        salon.getSimEngine().addEvent(salon.getNextEvent());
    }
}
