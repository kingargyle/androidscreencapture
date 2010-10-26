/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.ashot;

import com.mightypocket.utils.AndroidSdkHelper;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

/**
 *
 * @author Illya Yalovyy
 */
public final class AShot extends SingleFrameApplication implements PreferencesNames {

    private final Preferences p = Preferences.userNodeForPackage(AShot.class);
    private Mediator mediator;

    @Override
    protected void startup() {
        getContext().getResourceManager().setApplicationBundleNames(null);
        mediator = new Mediator(this);

        FrameView mainView = getMainView();

        mainView.setStatusBar(mediator.getStatusBar());
        mainView.setMenuBar(mediator.getMenuBar());
        mainView.setToolBar(mediator.getToolBar());
        mainView.setComponent(mediator.getMainPanel());

        show(mainView);
    }

    @Override
    protected void initialize(String[] args) {
        super.initialize(args);
    }

    @Override
    protected void ready() {
        String sdkPath = p.get(PREF_ANDROID_SDK_PATH, null);
        if (!AndroidSdkHelper.validatePath(sdkPath)) {
            do {
                sdkPath = askForSdkPath(sdkPath);
                if (StringUtils.isBlank(sdkPath)) exit();
                if (!AndroidSdkHelper.validatePath(sdkPath))
                    showErrorMessage("error.sdk");
            } while (!AndroidSdkHelper.validatePath(sdkPath));
            p.put(PREF_ANDROID_SDK_PATH, sdkPath);
        }

        mediator.startDemon();

        if (p.getBoolean(PREF_CHECK_UPDATES, true))
            getContext().getTaskService().execute(new UpdateChecker(mediator));

        mediator.setStatus("status.ready");
    }

    @Override
    protected void shutdown() {
        mediator.stopDemon();
        super.shutdown();

    }

    private String askForSdkPath(String sdkPath) {
        ResourceMap resourceMap = getContext().getResourceMap(OptionsDialog.class);
        return FolderRequestDialog.requestFolderFor(sdkPath,
                resourceMap.getString("sdk.request.title"),
                resourceMap.getString("sdk.request.desc"));
    }

    void showErrorMessage(String messageKey, Object... args) {
        ResourceMap resourceMap = getContext().getResourceMap();
        String message = resourceMap.getString(messageKey, args);
        String errorTitle = resourceMap.getString("error.title");

        JOptionPane.showMessageDialog(getMainFrame(), message, errorTitle, JOptionPane.ERROR_MESSAGE);
    }

    void showMessage(String messageKey, Object... args) {
        ResourceMap resourceMap = getContext().getResourceMap();
        String message = resourceMap.getString(messageKey, args);
        String errorTitle = resourceMap.getString("info.title");

        JOptionPane.showMessageDialog(getMainFrame(), message, errorTitle, JOptionPane.INFORMATION_MESSAGE);
    }

}
