/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.utils;

import com.mightypocket.ashot.AShot;
import java.io.InputStream;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.jdesktop.application.ResourceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Illya Yalovyy
 */
public final class ResourceHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(ResourceHelper.class);

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
            //should never be a case
            logger.error ("Cannot load resource", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return result;
    }
}
