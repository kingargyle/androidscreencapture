/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.ashoter;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.jdesktop.application.Application;

public class Main
{
    public static void main( String[] args )
    {
        configureLogging();
        Application.launch(AShoter.class, args);
    }

    private static void configureLogging() throws SecurityException {
        // Configure default handler
        Handler[] handlers = Logger.getLogger("").getHandlers();
        if (handlers != null && handlers.length > 0)
            handlers[0].setLevel(Level.ALL);

        // Configure log levels
        Logger.getLogger(Mediator.class.getCanonicalName()).setLevel(Level.ALL);
        Logger.getLogger(AndroDemon.class.getCanonicalName()).setLevel(Level.ALL);
    }
}
