package com.oracle.log;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;


public class LoggerFormatter extends Formatter {
    public static final String ANSI_BLACK = "\u001B[30m";

    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder(1000);
        builder.append(ANSI_BLACK);
        builder.append(" - ");
        builder.append(record.getMessage());
        builder.append("\n");
        return builder.toString();
    }
}
