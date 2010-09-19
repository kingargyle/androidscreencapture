/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.utils;

import com.mightypocket.ashoter.AShoter;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Illya Yalovyy
 */
public final class ResourceHelper {

    private ResourceHelper() {
    }

    public static String loadString(String name) {
        String result = null;

        InputStream is = null;
        try {
            is = AShoter.class.getResourceAsStream("resources/intro.html");
            result = IOUtils.toString(is);
        } catch (Exception e) {
            //should neve be a case
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
        return result;
    }
}
