package com.oracle.log;

import java.util.logging.*;

public class LogUtil {

    public static Logger getLogger(Class clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        setFormatter(logger);
        return logger;
    }

    private static void setFormatter(Logger LOGGER) {
        ConsoleHandler handler = new ConsoleHandler();

        LogRecord record = new LogRecord(Level.FINEST, "Vote Counter");
        Formatter formatter = new LoggerFormatter();
        formatter.format(record);
        handler.setFormatter(formatter);
        LOGGER.addHandler(handler);
        LOGGER.log(record);

        for (Handler iHandler : LOGGER.getParent().getHandlers()) {
            LOGGER.getParent().removeHandler(iHandler);
        }
    }
}
