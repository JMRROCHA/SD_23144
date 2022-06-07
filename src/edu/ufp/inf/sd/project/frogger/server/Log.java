package edu.ufp.inf.sd.project.frogger.server;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    public static void write(String className, String message) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        String DateToStr = format.format(date);

        Server.jTextAreaLog.append(DateToStr + ": " + className + " -> " + message + "\n");
    }
}