/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.ashot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author Illya Yalovyy
 */
public final class OptionsDialog extends JDialog implements PreferencesNames {
    private final Mediator mediator;
    private final Preferences p = Preferences.userNodeForPackage(AShot.class);
    private boolean ok;

    private JCheckBox updateCheckBox;
    private JCheckBox skipDuplicatesCheckBox;
    private JCheckBox saveOriginalCheckBox;
    private JSpinner offsetSpinner;
    private JPanel fsBackgroundPreview;
    private JButton okButton;

    public OptionsDialog(Mediator mediator) {
        super(mediator.getApplication().getMainFrame(), true);
        
        setMinimumSize(new Dimension(500, 400));
        setResizable(false);
        
        this.mediator = mediator;

        initComponents();
        ResourceMap resourceMap = mediator.getApplication().getContext().getResourceMap(OptionsDialog.class);
        resourceMap.injectComponents(this);
        setTitle(resourceMap.getString("title"));

        loadPreferences();
        getRootPane().setDefaultButton(okButton);
        this.pack();
    }

    private void initComponents() {
        ApplicationActionMap actionMap = mediator.getApplication().getContext().getActionMap(this);
        ResourceMap resourceMap = mediator.getApplication().getContext().getResourceMap(OptionsDialog.class);
        getRootPane().setLayout(new BorderLayout());

        JPanel buttonsPanel = createButtonsPanel(actionMap, resourceMap);
        JPanel generalPanel = createGeneralPanel(actionMap, resourceMap);

        JTabbedPane pane = new JTabbedPane();
        pane.addTab(resourceMap.getString("tab.general"), generalPanel);

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

    public static final String ACTION_SET_FS_BACKGROUND = "setFsBackground";
    @Action(name=ACTION_SET_FS_BACKGROUND)
    public void setFsBackground() {
        ResourceMap resourceMap = mediator.getApplication().getContext().getResourceMap(OptionsDialog.class);
        Color c = JColorChooser.showDialog(this, resourceMap.getString("color.request.title"), fsBackgroundPreview.getBackground());
        if (c != null) {
            fsBackgroundPreview.setBackground(c);
        }
    }

    private void loadPreferences() {
        updateCheckBox.setSelected(p.getBoolean(PREF_CHECK_UPDATES, true));
        saveOriginalCheckBox.setSelected(p.getBoolean(PREF_SAVE_ORIGINAL, true));
        skipDuplicatesCheckBox.setSelected(p.getBoolean(PREF_SAVE_SKIP_DUPLICATES, true));
        offsetSpinner.setValue(p.getInt(PREF_SAVE_SKIP_OFFSET, 40));
        fsBackgroundPreview.setBackground(new Color(p.getInt(PREF_GUI_PANEL_BACKGROUND, 0)));
    }

    private void savePreferences() {
        p.putBoolean(PREF_CHECK_UPDATES, updateCheckBox.isSelected());
        p.putBoolean(PREF_SAVE_ORIGINAL, saveOriginalCheckBox.isSelected());
        p.putBoolean(PREF_SAVE_SKIP_DUPLICATES, skipDuplicatesCheckBox.isSelected());
        p.putInt(PREF_SAVE_SKIP_OFFSET, (Integer)offsetSpinner.getValue());
        p.putInt(PREF_GUI_PANEL_BACKGROUND, fsBackgroundPreview.getBackground().getRGB());
        try {
            p.flush();
        } catch (BackingStoreException ex) {
            
        }
    }

    private JPanel createButtonsPanel(ApplicationActionMap actionMap, ResourceMap resourceMap) {
        JPanel buttonsPanel = new JPanel();
        okButton = new JButton(actionMap.get(ACTION_OK));
        buttonsPanel.add(okButton);
        buttonsPanel.add(new JButton(actionMap.get(ACTION_CANCEL)));
        return buttonsPanel;
    }

    private JPanel createGeneralPanel(ApplicationActionMap actionMap, ResourceMap resourceMap) {
        JPanel generalPanel = new JPanel();
        GroupLayout gl = new GroupLayout(generalPanel);
        generalPanel.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        JLabel fsBackgroundLabel = new JLabel();
        fsBackgroundLabel.setName("fsBackgroundLabel");
        fsBackgroundPreview = new JPanel();
        fsBackgroundPreview.setBorder(new LineBorder(Color.GRAY));
        fsBackgroundPreview.setPreferredSize(new Dimension(20, 10));
        JButton fsBackgroundButton = new JButton(actionMap.get(ACTION_SET_FS_BACKGROUND));

        skipDuplicatesCheckBox = new JCheckBox();
        skipDuplicatesCheckBox.setName("skipDuplicatesCheckBox");
        saveOriginalCheckBox = new JCheckBox();
        saveOriginalCheckBox.setName("saveOriginalCheckBox");
        JLabel offsetLabel = new JLabel();
        offsetLabel.setName("offsetLabel");
        offsetSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        offsetSpinner.setName("offsetSpinner");

        updateCheckBox = new JCheckBox();
        updateCheckBox.setName("updateCheckBox");

        gl.setHorizontalGroup(gl.createParallelGroup()
            .addComponent(saveOriginalCheckBox)
            .addComponent(skipDuplicatesCheckBox)
            .addGroup(gl.createSequentialGroup()
                .addComponent(offsetLabel)
                .addComponent(offsetSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                )
            .addGroup(gl.createSequentialGroup()
                .addComponent(fsBackgroundLabel)
                .addComponent(fsBackgroundPreview)
                .addComponent(fsBackgroundButton)
                )
            );
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addComponent(saveOriginalCheckBox)
            .addComponent(skipDuplicatesCheckBox)
            .addGroup(gl.createBaselineGroup(false, true)
                .addComponent(offsetLabel)
                .addComponent(offsetSpinner)
                )
            .addGroup(gl.createBaselineGroup(false, true)
                .addComponent(fsBackgroundLabel)
                .addComponent(fsBackgroundPreview)
                .addComponent(fsBackgroundButton)
                )
            );
        return generalPanel;
    }

    public boolean isOk() {
        return ok;
    }

    public static void showDialog(Mediator mediator) {
        OptionsDialog d = new OptionsDialog(mediator);
        d.setLocationRelativeTo(mediator.getApplication().getMainFrame());
        d.setVisible(true);
        d.dispose();
    }
}
