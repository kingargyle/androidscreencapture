/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */

package com.mightypocket.ashoter;

import java.awt.Dimension;
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


    public enum Scale {
        SMALL(0.5), ORIGINAL(1), LARGE(1.5), CUSTOM(0);

        private final double factor;

        private Scale(double factor) {
            this.factor = factor;
        }

        public double getFactor() {
            return factor;
        }
    }

    public enum Rotation {
        R0, R90, R180, R270
    }

    private final GraphicsConfiguration gc;

    private Scale scale = Scale.ORIGINAL;
    private Rotation rotation = Rotation.R0;
    private Dimension customBounds;

    public ImageProcessor() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gc = gd.getDefaultConfiguration();
    }

    Image process(Image source) {
        if (scale == Scale.ORIGINAL && rotation == Rotation.R0) {
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

        final double factor;

        if (Scale.CUSTOM == scale && w > 0 && h > 0) {
            factor = Math.min(customBounds.getWidth() / w, customBounds.getHeight() / h);
        } else {
            factor = scale.getFactor();
        }

        BufferedImage target = gc.createCompatibleImage((int) (w * factor), (int) (h * factor));

        Graphics2D g2 = target.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        double theta = Math.PI / 2 * rotation.ordinal();

        AffineTransform xform = new AffineTransform();
        xform.scale(factor, factor);
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

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }

    public Dimension getCustomBounds() {
        return customBounds;
    }

    public void setCustomBounds(Dimension customBounds) {
        this.customBounds = customBounds;
    }

}
