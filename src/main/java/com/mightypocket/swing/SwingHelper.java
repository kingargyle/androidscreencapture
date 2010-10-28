/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */

package com.mightypocket.swing;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

/**
 *
 * @author IllyaYalovyy
 */
public final class SwingHelper {
    private SwingHelper() {}

    public static <T extends AbstractButton> T addToButtonGroup(ButtonGroup bg, T button ) {
        bg.add(button);
        return button;
    }

}
