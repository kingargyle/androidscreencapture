/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */

package com.mightypocket.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 *
 * @author IllyaYalovyy
 */
public class BusyAndroidAnimation {

    private static final int IMG_WIDTH = 80;
    private static final int IMG_HEIGHT = 5;
    private static final int FRAMES_COUNT = 20;
    private static final int SIGN_WIDTH = 10;
    private static final Object lock = new Object();
    private static Image[] frames;
    private int index = 0;

    private static Image[] generateFrames() {
        final int halfPath = (IMG_WIDTH - SIGN_WIDTH) / 2;
        final int centerPos = IMG_WIDTH / 2;
        final double step = 2 * Math.PI / FRAMES_COUNT;
        final int offset = SIGN_WIDTH / 2;
        double angle = 0;

        final Image[] images = new Image[FRAMES_COUNT];

        for (int i = 0; i < FRAMES_COUNT; i++) {
            final BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
            final Graphics g = image.getGraphics();
            final int pos = (int) Math.round(Math.cos(angle) * halfPath) + centerPos;
            angle += step;
            g.setColor(Color.red);
            g.fillRect(pos - offset, 0, SIGN_WIDTH, IMG_HEIGHT);
            g.dispose();
            images[i] = image;
        }
        return images;
    }

    public Image getNextImage() {
        return getFrames()[index++ % FRAMES_COUNT];
    }

    private static Image[] getFrames() {
        if (frames != null) {
            return frames;
        }
        synchronized (lock) {
            if (frames == null) {
                frames = generateFrames();
            }
        }
        return frames;
    }
}
