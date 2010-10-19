/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.ashot;

import java.io.InputStream;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.jdesktop.application.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Illya Yalovyy
 */
public final class UpdateChecker extends Task<String, Void>{
    private final static Logger logger = LoggerFactory.getLogger(UpdateChecker.class);
    public static final String VERSION = "version [";
    private static final String UPDATE_URL = "http://update-check.appspot.com/check?app=ashot";
    private final Mediator mediator;

    public UpdateChecker(Mediator mediator) {
        super(mediator.getApplication());
        this.mediator = mediator;
    }

    public static String check() {
        String version = null;
        try {
            URL url = new URL(UPDATE_URL);
            Object content = url.getContent();
            if (content instanceof InputStream) {
                String text = IOUtils.toString((InputStream) content);
                int st = text.indexOf(VERSION);
                if (st >=0) {
                   st += VERSION.length();
                   int end = text.indexOf("]", st);
                   version = text.substring(st, end);
                }
            }
        } catch (Exception ex) {
            logger.error("Cannot check for updates.", ex);
        }

        return version;
    }

    @Override
    protected String doInBackground() throws Exception {
        return check();
    }

    @Override
    protected void succeeded(String newVersion) {
        if (newVersion == null) {
            mediator.setStatus("status.error.updates");
        }
        String oldVersion = mediator.getApplication().getContext().getResourceMap().getString("Application.version");
        if (oldVersion.compareTo(newVersion) < 0) {
            mediator.getApplication().showMessage("info.newVersion", newVersion);
        } else {
            mediator.setStatus("status.noupdates");
        }
    }


}