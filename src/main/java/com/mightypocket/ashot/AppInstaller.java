package com.mightypocket.ashot;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import java.io.File;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Illya_Yalovyy1
 */
public class AppInstaller extends Task<Void,Void> {
    private final Logger logger = LoggerFactory.getLogger(AppInstaller.class);
    private final Mediator mediator;
    private final File file;
    public AppInstaller(Mediator mediator, File file) {
        super(mediator.getApplication());
        this.mediator = mediator;
        this.file = file;
    }

    @Override
    protected Void doInBackground() throws Exception {
        AndroidDebugBridge bridge = AndroidDebugBridge.getBridge();
        IDevice[] devices = bridge.getDevices();
        IDevice selectedDevice = null;
        String connectedDevice = mediator.getConnectedDevice();

        final String absolutePath = file.getAbsolutePath();
        if (file.exists() && file.isFile() && file.canRead()) {

            for (IDevice iDevice : devices) {
                if (StringUtils.equals(iDevice.toString(), connectedDevice)) {
                    selectedDevice = iDevice;
                    break;
                }
            }

            if (selectedDevice != null) {
                selectedDevice.installPackage(absolutePath, true);
            } else {
                logger.error("Cannot find device: {}", connectedDevice);
                mediator.setStatus("status.error.install.device", connectedDevice);
            }
        } else {
            logger.error("Cannot access file: {}", absolutePath);
            mediator.setStatus("status.error.install.file", file.getAbsolutePath());
        }

        return null;
    }

}
