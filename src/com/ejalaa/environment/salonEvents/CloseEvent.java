package com.ejalaa.environment.salonEvents;

import com.ejalaa.environment.Salon;
import com.ejalaa.logging.Logger;
import com.ejalaa.simulation.Event;

/**
 * Created by ejalaa on 14/12/2016.
 */
public class CloseEvent extends Event {

    private Salon salon;

    public CloseEvent(Salon salon) {
        super(salon.getName(), salon.getNextClosingTime(), "Closing Salon");
        this.salon = salon;
    }

    @Override
    public void doAction() {
        salon.close();
        Logger.getInstance().log(salon.getName(), salon.getSimEngine().getCurrentSimTime(), "Salon Closed");
        salon.updateNextOpeningTime();
        salon.getSimEngine().addEvent(salon.getNextEvent());
    }
}
