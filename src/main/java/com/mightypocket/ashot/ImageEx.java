/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */

package com.mightypocket.ashot;

import java.awt.Image;

/**
 *
 * @author Illya Yalovyy
 */
final class ImageEx {
    private Image value;
    private boolean duplicate = false;
    private boolean landscape = false;
    private boolean ccw = true;

    public ImageEx(Image value) {
        if (value == null ) throw new IllegalArgumentException("Value is null!");
        this.value = value;
    }

    public Image getValue() {
        return value;
    }

    public void setValue(Image value) {
        this.value = value;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public boolean isLandscape() {
        return landscape;
    }

    public void setLandscape(boolean landscape) {
        this.landscape = landscape;
    }

    public boolean isCcw() {
        return ccw;
    }

    public void setCcw(boolean ccw) {
        this.ccw = ccw;
    }

    
}
