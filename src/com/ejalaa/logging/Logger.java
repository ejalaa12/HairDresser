package com.ejalaa.logging;

import com.ejalaa.simulation.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * A Logger which is a Singleton that allows to log all informations
 */
public class Logger {

    private static final DateTimeFormatter logicalDateTimeFormatter;
    private static final DateTimeFormatter logicalTimeFormatter;
    private static final DateTimeFormatter logicalDateFormatter;
    private static Logger instance = null;

    static {
        logicalTimeFormatter = DateTimeFormatter.ISO_TIME;
        logicalDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        DateTimeFormatterBuilder dtfb = new DateTimeFormatterBuilder();
        dtfb.parseCaseInsensitive();
        dtfb.append(logicalDateFormatter);
        dtfb.appendLiteral(" ");
        dtfb.append(logicalTimeFormatter);
        logicalDateTimeFormatter = dtfb.toFormatter();
    }

    private Logger() {
        // exist only to defeat instanciation
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(Event event) {
        mlog(event.getCreator(), event.getScheduledTime(), event.getDescription());
    }

    public void log(String creatorName, LocalDateTime logTime, String message) {
        mlog(creatorName, logTime, message);
    }

    private void mlog(String creatorName, LocalDateTime logTime, String message) {
        String timestamp = logicalDateTimeFormatter.format(logTime);
        String res = String.format("[%s] %-20s: %s", timestamp, creatorName, message);
//        System.out.println(res);
    }

    public void log(LocalDateTime logTime) {
        System.out.println(logicalDateTimeFormatter.format(logTime));
    }

}
