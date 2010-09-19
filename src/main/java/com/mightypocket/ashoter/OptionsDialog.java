/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.ashoter;

import java.awt.BorderLayout;
import java.util.prefs.Preferences;
import javax.swing.JButton;
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
public final class OptionsDialog extends JDialog {
    private final Mediator mediator;
    private final Preferences p = Preferences.userNodeForPackage(AShoter.class);
    private boolean ok;

    public OptionsDialog(Mediator mediator) {
        super(mediator.getApplication().getMainFrame(), true);
        this.mediator = mediator;

        initComponents();
        ResourceMap resourceMap = mediator.getApplication().getContext().getResourceMap(OptionsDialog.class);
        resourceMap.injectComponents(this);

        loadPreferences();
    }

    private void initComponents() {
        ApplicationActionMap actionMap = mediator.getApplication().getContext().getActionMap(this);
        getRootPane().setLayout(new BorderLayout());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(new JButton(actionMap.get(ACTION_OK)));
        buttonsPanel.add(new JButton(actionMap.get(ACTION_CANCEL)));

        JPanel generalPanel = new JPanel();
        generalPanel.add(new JLabel("General"));

        JTabbedPane pane = new JTabbedPane();
        pane.addTab("General", generalPanel);


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

    private void loadPreferences() {
    }

    private void savePreferences() {
    }
}
