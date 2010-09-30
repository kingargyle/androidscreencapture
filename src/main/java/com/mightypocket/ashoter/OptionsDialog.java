/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.ashoter;

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
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import org.apache.commons.lang.StringUtils;
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
    private JCheckBox showAboutCheckBox;
    private JCheckBox updateCheckBox;
    private JCheckBox skipDuplicatesCheckBox;
    private JCheckBox saveOriginalCheckBox;
    private JSpinner offsetSpinner;
    private JPanel fsBackgroundPreview;
    private JButton okButton;
    private JTextField sdkPathShowTextField;
    private JTextField savePathShowTextField;

    public OptionsDialog(Mediator mediator) {
        super(mediator.getApplication().getMainFrame(), true);
        setLocationRelativeTo(mediator.getApplication().getMainFrame());
        setMinimumSize(new Dimension(500, 300));
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
        JPanel fullScreenPanel = createFullScreenPanel(actionMap, resourceMap);
        JPanel savePanel = createSavePanel(actionMap, resourceMap);
        JPanel updatePanel = createUpdatePanel(actionMap, resourceMap);

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
        ResourceMap resourceMap = mediator.getApplication().getContext().getResourceMap(OptionsDialog.class);
        String folder = FolderRequestDialog.requestFolderFor(sdkPathShowTextField.getText(),
                resourceMap.getString("sdk.request.title"), resourceMap.getString("sdk.request.desc"));
        if (StringUtils.isNotBlank(folder)) {
            sdkPathShowTextField.setText(folder);
        }
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

    public static final String ACTION_SET_DEFAULT_FOLDER = "setDefaultFolder";
    @Action(name=ACTION_SET_DEFAULT_FOLDER)
    public void setDefaultFolder() {
        ResourceMap resourceMap = mediator.getApplication().getContext().getResourceMap(OptionsDialog.class);
        String folder = FolderRequestDialog.requestFolderFor(savePathShowTextField.getText(),
                resourceMap.getString("save.request.title"), resourceMap.getString("save.request.desc"));
        if (StringUtils.isNotBlank(folder)) {
            savePathShowTextField.setText(folder);
        }
    }

    private void loadPreferences() {
        updateCheckBox.setSelected(p.getBoolean(PREF_CHECK_UPDATES, true));
        showLabelsInToolbarCheckBox.setSelected(p.getBoolean(PREF_GUI_SHOW_TEXT_IN_TOOLBAR, true));
        saveOriginalCheckBox.setSelected(p.getBoolean(PREF_SAVE_ORIGINAL, true));
        skipDuplicatesCheckBox.setSelected(p.getBoolean(PREF_SAVE_SKIP_DUPLICATES, true));
        sdkPathShowTextField.setText(p.get(PREF_ANDROID_SDK_PATH, null));
        savePathShowTextField.setText(p.get(PREF_DEFAULT_FILE_FOLDER, null));
        //TODO p.get(PREF_DEFAULT_FILE_PREFIX, "screenshot");
        showAboutCheckBox.setSelected(p.getBoolean(PREF_SHOW_ABOUT, true));
        fsBackgroundPreview.setBackground(new Color(p.getInt(PREF_GUI_PANEL_BACKGROUND, 0)));
    }

    private void savePreferences() {
        p.putBoolean(PREF_CHECK_UPDATES, updateCheckBox.isSelected());
        p.putBoolean(PREF_GUI_SHOW_TEXT_IN_TOOLBAR, showLabelsInToolbarCheckBox.isSelected());
        p.putBoolean(PREF_SAVE_ORIGINAL, saveOriginalCheckBox.isSelected());
        p.putBoolean(PREF_SAVE_SKIP_DUPLICATES, skipDuplicatesCheckBox.isSelected());
        p.putBoolean(PREF_SHOW_ABOUT, showAboutCheckBox.isSelected());
        p.put(PREF_ANDROID_SDK_PATH, sdkPathShowTextField.getText());
        p.put(PREF_DEFAULT_FILE_FOLDER, savePathShowTextField.getText());
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

        JLabel sdkPathLabel = new JLabel();
        sdkPathLabel.setName("sdkPathLabel");
        JButton setSdkPathButton = new JButton(actionMap.get(ACTION_SET_SDK_PATH));
        showLabelsInToolbarCheckBox = new JCheckBox();
        showLabelsInToolbarCheckBox.setName("showLabelsInToolbarCheckBox");
        showAboutCheckBox = new JCheckBox();
        showAboutCheckBox.setName("showAboutCheckBox");
        sdkPathShowTextField = new JTextField();
        sdkPathShowTextField.setEditable(false);

        gl.setHorizontalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addComponent(sdkPathLabel)
                .addComponent(setSdkPathButton)
                )
            .addComponent(sdkPathShowTextField)
            .addComponent(showLabelsInToolbarCheckBox)
            .addComponent(showAboutCheckBox)
            );
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(sdkPathLabel)
                .addComponent(setSdkPathButton)
                )
            .addComponent(sdkPathShowTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(showLabelsInToolbarCheckBox)
            .addComponent(showAboutCheckBox)
            );
        return generalPanel;
    }

    private JPanel createFullScreenPanel(ApplicationActionMap actionMap, ResourceMap resourceMap) {
        JPanel fullScreenPanel = new JPanel();
        GroupLayout gl = new GroupLayout(fullScreenPanel);
        fullScreenPanel.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);
        JLabel fsBackgroundLabel = new JLabel();
        fsBackgroundLabel.setName("fsBackgroundLabel");
        fsBackgroundPreview = new JPanel();
        fsBackgroundPreview.setBorder(new LineBorder(Color.GRAY));
        fsBackgroundPreview.setPreferredSize(new Dimension(20, 10));
        JButton fsBackgroundButton = new JButton(actionMap.get(ACTION_SET_FS_BACKGROUND));
        gl.setHorizontalGroup(gl.createSequentialGroup()
            .addComponent(fsBackgroundLabel)
            .addComponent(fsBackgroundPreview)
            .addComponent(fsBackgroundButton)
            );
        gl.setVerticalGroup(gl.createBaselineGroup(false, true)
            .addComponent(fsBackgroundLabel)
            .addComponent(fsBackgroundPreview)
            .addComponent(fsBackgroundButton)
            );
        return fullScreenPanel;
    }

    private JPanel createSavePanel(ApplicationActionMap actionMap, ResourceMap resourceMap) {
        JPanel panel = new JPanel();
        GroupLayout gl = new GroupLayout(panel);
        panel.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        JButton browseButton = new JButton(actionMap.get(ACTION_SET_DEFAULT_FOLDER));
        JLabel folderLabel = new JLabel();
        folderLabel.setName("folderLabel");
        skipDuplicatesCheckBox = new JCheckBox();
        skipDuplicatesCheckBox.setName("skipDuplicatesCheckBox");
        saveOriginalCheckBox = new JCheckBox();
        saveOriginalCheckBox.setName("saveOriginalCheckBox");
        JLabel offsetLabel = new JLabel();
        offsetLabel.setName("offsetLabel");
        offsetSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        offsetSpinner.setName("offsetSpinner");
        savePathShowTextField = new JTextField();
        savePathShowTextField.setEditable(false);

        gl.setHorizontalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addComponent(folderLabel)
                .addComponent(browseButton)
                )
            .addComponent(savePathShowTextField)
            .addComponent(saveOriginalCheckBox)
            .addComponent(skipDuplicatesCheckBox)
            .addGroup(gl.createSequentialGroup()
                .addComponent(offsetLabel)
                .addComponent(offsetSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                )
            );
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createBaselineGroup(false, true)
                .addComponent(folderLabel)
                .addComponent(browseButton)
                )
            .addComponent(savePathShowTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(saveOriginalCheckBox)
            .addComponent(skipDuplicatesCheckBox)
            .addGroup(gl.createBaselineGroup(false, true)
                .addComponent(offsetLabel)
                .addComponent(offsetSpinner)
                )

            );

        return panel;
    }

    private JPanel createUpdatePanel(ApplicationActionMap actionMap, ResourceMap resourceMap) {
        JPanel panel = new JPanel();
        GroupLayout gl = new GroupLayout(panel);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        updateCheckBox = new JCheckBox();
        updateCheckBox.setName("updateCheckBox");
        panel.setLayout(gl);
        gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(updateCheckBox));
        gl.setVerticalGroup(gl.createBaselineGroup(false, true).addComponent(updateCheckBox));
        return panel;
    }

    public boolean isOk() {
        return ok;
    }

    public static void showDialog(Mediator mediator) {
        OptionsDialog d = new OptionsDialog(mediator);
        d.setVisible(true);
        d.dispose();
    }
}
