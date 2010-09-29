/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.ashoter;

import java.awt.BorderLayout;
import java.util.prefs.Preferences;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author Illya Yalovyy
 */
public final class OptionsDialog extends JDialog implements PreferencesNames {
    private final Mediator mediator;
    private final Preferences p = Preferences.userNodeForPackage(AShoter.class);
    private boolean ok;

    private JCheckBox showLabelsInToolbarCheckBox;

    public OptionsDialog(Mediator mediator) {
        super(mediator.getApplication().getMainFrame(), true);
        this.mediator = mediator;

        initComponents();
        ResourceMap resourceMap = mediator.getApplication().getContext().getResourceMap(OptionsDialog.class);
        resourceMap.injectComponents(this);

        loadPreferences();
        this.pack();
    }

    private void initComponents() {
        ApplicationActionMap actionMap = mediator.getApplication().getContext().getActionMap(this);
        ResourceMap resourceMap = mediator.getApplication().getContext().getResourceMap(OptionsDialog.class);
        getRootPane().setLayout(new BorderLayout());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(new JButton(actionMap.get(ACTION_OK)));
        buttonsPanel.add(new JButton(actionMap.get(ACTION_CANCEL)));

        JPanel generalPanel = new JPanel();
        GroupLayout gl1 = new GroupLayout(generalPanel);
        generalPanel.setLayout(gl1);
        gl1.setAutoCreateGaps(true);
        gl1.setAutoCreateContainerGaps(true);

        JLabel sdkPathLabel = new JLabel();
        sdkPathLabel.setName("sdkPathLabel");
        JButton setSdkPathButton = new JButton(actionMap.get(ACTION_SET_SDK_PATH));
        showLabelsInToolbarCheckBox = new JCheckBox();
        showLabelsInToolbarCheckBox.setName("showLabelsInToolbarCheckBox");

        gl1.setHorizontalGroup(gl1.createParallelGroup()
            .addGroup(gl1.createSequentialGroup()
                .addComponent(sdkPathLabel)
                .addComponent(setSdkPathButton)
                )
            .addComponent(showLabelsInToolbarCheckBox)
            );
        gl1.setVerticalGroup(gl1.createSequentialGroup()
            .addGroup(gl1.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(sdkPathLabel)
                .addComponent(setSdkPathButton)
                )
            .addComponent(showLabelsInToolbarCheckBox)
            );
        JPanel fullScreenPanel = new JPanel();

        JPanel savePanel = new JPanel();

        JPanel updatePanel = new JPanel();

        JTabbedPane pane = new JTabbedPane();
        pane.addTab(resourceMap.getString("tab.general"), generalPanel);
        pane.addTab(resourceMap.getString("tab.fullScreen"), fullScreenPanel);
        pane.addTab(resourceMap.getString("tab.saving"), savePanel);
        pane.addTab(resourceMap.getString("tab.updating"), updatePanel);


        getRootPane().add(pane, BorderLayout.CENTER);
        getRootPane().add(buttonsPanel, BorderLayout.PAGE_END);
    }

    public static final String ACTION_OK = "actionOk";
    @Action(name=ACTION_OK)
    public void actionOk() {
        ok = true;
        setVisible(false);
        savePreferences();

    }

    public static final String ACTION_CANCEL = "actionCancel";
    @Action(name=ACTION_CANCEL)
    public void actionCancel() {
        ok = false;
        setVisible(false);
    }

    public static final String ACTION_SET_SDK_PATH = "setSdkPath";
    @Action(name=ACTION_SET_SDK_PATH)
    public void setSdkPath() {

    }

    private void loadPreferences() {
    }

    private void savePreferences() {
    }
}
