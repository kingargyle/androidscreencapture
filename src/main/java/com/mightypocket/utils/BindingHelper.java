/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */

package com.mightypocket.utils;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

/**
 *
 * @author Illya Yalovyy
 */
public final class BindingHelper {

    private BindingHelper() {}

    public static Binding bindRead(Object ss, String svn, Object ts, String tvn) {
        return bind(UpdateStrategy.READ, ss, svn, ts, tvn);
    }

    public static Binding bindReadWrite(Object ss, String svn, Object ts, String tvn) {
        return bind(UpdateStrategy.READ_WRITE, ss, svn, ts, tvn);
    }

    public static Binding bind(UpdateStrategy str, Object ss, String svn, Object ts, String tvn) {
        return Bindings.createAutoBinding(
                str,
                ss, ELProperty.create("${"+svn+"}"),
                ts, ELProperty.create("${"+tvn+"}"));
    }

}
