/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */

package com.mightypocket.ashoter;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 *
 * @author Illya Yalovyy
 */

final class ImageProcessor {

    public enum Rotation {
        R0, R90, R180, R270
    }

    private final GraphicsConfiguration gc;

    private double scale = 1.0;
    private Rotation rotation = Rotation.R0;

    public ImageProcessor() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gc = gd.getDefaultConfiguration();
    }

    Image process(Image source) {
        if (scale == 1.0 && rotation == Rotation.R0) {
            return source;
        }

        int w = (int) (source.getWidth(null));
        int h = (int) (source.getHeight(null));

        final boolean isFlipSides = (rotation.ordinal() % 2) == 1;

        if (isFlipSides) {
            int t = h;
            h = w;
            w = t;
        }

        BufferedImage target = gc.createCompatibleImage((int) (w * scale), (int) (h * scale));

        Graphics2D g2 = target.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        double theta = Math.PI / 2 * rotation.ordinal();

        AffineTransform xform = new AffineTransform();
        xform.scale(scale, scale);
        xform.translate(0.5 * w, 0.5 * h);
        xform.rotate(theta);
        if (isFlipSides) {
            xform.translate(-0.5 * h, -0.5 * w);
        } else {
            xform.translate(-0.5 * w, -0.5 * h);
        }
        g2.drawImage(source, xform, null);

        g2.dispose();
        return target;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

}
