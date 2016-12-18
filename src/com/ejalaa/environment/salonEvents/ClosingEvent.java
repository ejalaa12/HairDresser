package com.ejalaa.environment.salonEvents;

import com.ejalaa.environment.Salon;
import com.ejalaa.logging.Logger;
import com.ejalaa.simulation.Event;

/**
 * Created by ejalaa on 18/12/2016.
 */
public class ClosingEvent extends Event {
    private Salon salon;

    public ClosingEvent(Salon salon) {
        super(salon.getName(), salon.getNextClosingTime(), "Closing Salon");
        this.salon = salon;
    }

    @Override
    public void doAction() {
        salon.stopAcceptingClients();
        Logger.getInstance().log(salon.getName(), salon.getSimEngine().getCurrentSimTime(), "Salon Closed");
        salon.updateNextOpeningTime();
        // no next event. we wait until it's the last client before generating the next opening
    }
}
