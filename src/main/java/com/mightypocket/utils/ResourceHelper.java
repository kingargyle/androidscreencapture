/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.utils;

import com.mightypocket.ashoter.AShot;
import java.io.InputStream;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author Illya Yalovyy
 */
public final class ResourceHelper {

    private ResourceHelper() {
    }

    public static String loadString(String name, ResourceMap resourceMap) {
        String result = null;

        InputStream is = null;
        try {
            is = AShot.class.getResourceAsStream("resources/"+name);
            result = IOUtils.toString(is);
            Set<String> keySet = resourceMap.keySet();
            for (String key : keySet) {
                String token = "${" +key + "}";
                if (result.contains(token)) {
                    String value = resourceMap.getString(key);
                    result = result.replace(token, value);
                }
            }
        } catch (Exception e) {
            //should neve be a case
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
        return result;
    }
}
