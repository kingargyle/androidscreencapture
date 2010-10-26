package com.mightypocket.utils;

import java.io.File;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Illya_Yalovyy1
 */
public final class AndroidSdkHelper {

    private AndroidSdkHelper() {
    }

    public static boolean validatePath(String basePath) {
        if (StringUtils.isBlank(basePath)) return false;
        File baseDir = new File(basePath);
        if (!baseDir.exists()) return false;
        if (!baseDir.isDirectory()) return false;

        File f1 = new File(baseDir, "tools/lib/ddmlib.jar");
        if (!f1.exists()) return false;
        
        return true;
    }

}
