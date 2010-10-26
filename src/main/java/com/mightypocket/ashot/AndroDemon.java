/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.ashot;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.prefs.Preferences;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Illya Yalovyy
 */
public class AndroDemon extends Task<Void, ImageEx> implements PreferencesNames {
    public static final int CONNECTING_PAUSE = 200;

    private final Logger logger = LoggerFactory.getLogger(AndroDemon.class);
    private final Preferences p = Preferences.userNodeForPackage(AShot.class);
    private final Mediator mediator;
    private String sdkPath;
    private AndroidDebugBridge bridge;
    private IDevice device;
    private boolean active = true;
    private int[] lastRawImage;
    
    private final GraphicsConfiguration gc;

    public AndroDemon(final Mediator mediator) {
        super(mediator.getApplication());
        this.mediator = mediator;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gc = gd.getDefaultConfiguration();
    }

    @Override
    protected Void doInBackground() throws Exception {
        
        sdkPath = p.get(PREF_ANDROID_SDK_PATH, null);
        while (StringUtils.isBlank(sdkPath)) {
            logger.error("Android SDK is not properly configured.");
            sleep(5000);
            sdkPath = p.get(PREF_ANDROID_SDK_PATH, null);
        }

        initBridge();

        while (!isCancelled()) {

     
            if (isCancelled()) {
                break;
            }
            
            ImageEx img = null;
            if (active) {
                if (device != null) {
                        img = fetchScreen();
                } else {
                    sleep(CONNECTING_PAUSE);
                }
            }

            process((img == null)?Collections.EMPTY_LIST:Collections.singletonList(img));
        }

        return null;
    }

    @Override
    protected void cancelled() {
        logger.trace("cancelled");
    }

    @Override
    protected void failed(Throwable cause) {
        logger.trace("failed");
        logger.error("Error in Android Demon.", cause);
    }

    @Override
    protected void finished() {
        logger.trace("finished");
    }

    private ImageEx fetchScreen() {
        final IDevice d = device;
        final boolean landscape = mediator.isLandscape();
        final boolean ccw = p.getBoolean(PREF_ROTATION_CCW, true);
        ImageEx image = null;
        if (d != null) {
            try {
                RawImage screenshot = landscape ? device.getScreenshot().getRotated() : device.getScreenshot();

                if (screenshot != null) {
                    image = renderImage(screenshot, landscape, ccw);
                    image.setLandscape(landscape);
                    image.setCcw(ccw);
                }

            } catch (Exception ex) {
                logger.error("", ex);
            } 
        }
        return image;
    }

    private void initBridge() {
        logger.trace("initBridge");
        AndroidDebugBridge.init(false);
        logger.trace("create bridge");
        String adbPath = sdkPath + File.separator + "tools" + File.separator + "adb";
        bridge = AndroidDebugBridge.createBridge(adbPath, true);
        logger.trace("bridge is created");

        AndroidDebugBridge.addDeviceChangeListener(new AndroidDebugBridge.IDeviceChangeListener() {

            @Override
            public void deviceConnected(IDevice device) {
                logger.info("deviceConnected: {}", device);
                addDevice(device);
            }

            @Override
            public void deviceDisconnected(IDevice device) {
                logger.trace("deviceDisconnected: {}", device);
                removeDevice(device);
            }

            @Override
            public void deviceChanged(IDevice device, int changeMask) {
                logger.trace("deviceChanged: {} - {}", device, changeMask);
            }

        });
    }

    private void sleep(int t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException ignore) {
        }
    }

    private ImageEx renderImage(RawImage screenshot, boolean landscape, boolean ccw) {
        BufferedImage image = gc.createCompatibleImage(screenshot.width, screenshot.height);

        int offset = p.getInt(PREF_SAVE_SKIP_OFFSET, 0);
        int size = screenshot.width * screenshot.height;

        if (lastRawImage == null || lastRawImage.length != size) {
            lastRawImage = new int[size];
        }

        int index = 0;
        int indexInc = screenshot.bpp >> 3;

        boolean duplicate = true;

        int value, pos;
        
        if (!ccw && landscape) {
            index = (size - 1) * indexInc;
            indexInc = -indexInc;
        }

        for (int y = 0; y < screenshot.height; y++) {
            for (int x = 0; x < screenshot.width; x++, index += indexInc) {
                value = screenshot.getARGB(index);
                image.setRGB(x, y, value);
                pos = x + y * screenshot.width;

                if (duplicate && y >= offset)
                    duplicate = duplicate && (lastRawImage[pos] == value);

                lastRawImage[pos] = value;
            }
        }

        ImageEx res = new ImageEx(image);
        res.setDuplicate(duplicate);

        return res;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private void addDevice(IDevice device) {
        mediator.addDevice(device.toString());
        
        if (this.device == null) {
           setDevice(device);
        }
    }

    private void removeDevice(IDevice device) {
        if (this.device.equals(device)) {
            IDevice[] devices = bridge.getDevices();
            if (devices.length == 0) {
                setDevice(null);
            } else {
                setDevice(devices[0]);
            }
        }
        mediator.removeDevice(device.toString());
    }

    private void setDevice(IDevice device) {
        this.device = device;
        mediator.setConnectedDevice((device == null)?null:device.toString());
    }

    public void connectTo(String str) {
        if (StringUtils.isBlank(str)) {
            return ;
        }
        IDevice[] devices = bridge.getDevices();
        for (IDevice d : devices) {
            if (str.equals(d.toString())) {
                setDevice(d);
            }
        }
    }

    public void resetLastImage() {
        lastRawImage = null;
    }
}
