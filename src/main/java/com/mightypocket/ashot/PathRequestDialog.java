/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.ashot;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author Illya Yalovyy
 */
public final class PathRequestDialog extends JDialog {

    JLabel labelDescription;
    JTextField textFieldPath;
    JButton buttonOk;
    JButton buttonCancel;
    JButton buttonBrowse;
    private boolean ok;

    @Resource public String openButtonName;

    private final String title;
    private final boolean selectFolder;
    private FileFilter fileFilter;


    private PathRequestDialog(final String oldPath, final String title, final String description, boolean selectFolder) {
        super(Application.getInstance(AShot.class).getMainFrame(), title, true);
        final AShot app = Application.getInstance(AShot.class);

        final ResourceMap resourceMap = app.getContext().getResourceMap(PathRequestDialog.class);
        resourceMap.injectFields(this);
        setResizable(false);
        this.title = title;

        setModal(true);

        labelDescription = new JLabel(description);
        textFieldPath = new JTextField(oldPath);
        textFieldPath.setColumns(30);

        ApplicationActionMap actionMap = app.getContext().getActionMap(this);

        buttonOk = new JButton(actionMap.get(ACTION_OK));
        buttonCancel = new JButton(actionMap.get(ACTION_CANCEL));
        buttonBrowse = new JButton(actionMap.get(ACTION_BROWSE));

        JSeparator separator = new JSeparator();

        getRootPane().setDefaultButton(buttonOk);

        GroupLayout gl = new GroupLayout(getRootPane());
        getRootPane().setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(labelDescription, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
            .addGroup(gl.createSequentialGroup()
                .addComponent(true, textFieldPath, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(buttonBrowse)
            )
            .addComponent(separator, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
            .addGroup(GroupLayout.Alignment.TRAILING, gl.createSequentialGroup()
                .addComponent(buttonOk)
                .addComponent(buttonCancel)
            )
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
            .addComponent(labelDescription)
            .addGroup(gl.createParallelGroup()
                .addComponent(textFieldPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(buttonBrowse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            )
            .addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGroup(gl.createParallelGroup()
                .addComponent(buttonCancel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(buttonOk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            )
        );

        pack();
        setLocationRelativeTo(app.getMainFrame());
        this.selectFolder = selectFolder;
    }

    public static final String ACTION_OK = "actionOk";
    @Action(name=ACTION_OK)
    public void actionOk() {
        ok = true;
        setVisible(false);
    }

    public static final String ACTION_CANCEL = "actionCancel";
    @Action(name=ACTION_CANCEL)
    public void actionCancel() {
        ok = false;
        setVisible(false);
    }

    public static final String ACTION_BROWSE = "actionBrowse";
    @Action(name=ACTION_BROWSE)
    public void actionBrowse () {
        JFileChooser chooser = new JFileChooser(textFieldPath.getText());
        chooser.setApproveButtonText(openButtonName);
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode((selectFolder)?JFileChooser.DIRECTORIES_ONLY:JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setAcceptAllFileFilterUsed(false);
        if (fileFilter != null) chooser.setFileFilter(fileFilter);
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            textFieldPath.setText(chooser.getSelectedFile().getAbsolutePath());
        }

    }

    private void createApkFilter() {
        fileFilter = new FileNameExtensionFilter("Android application", "apk");
    }

    static String requestFolderFor(String oldPath, String title, String description) {
        PathRequestDialog d = new PathRequestDialog(oldPath, title, description, true);

        d.setVisible(true);
        d.removeAll();
        d.dispose();
        return d.ok?d.textFieldPath.getText():null;
    }

    static String requestFileFor(String oldPath, String title, String description) {
        PathRequestDialog d = new PathRequestDialog(oldPath, title, description, false);

        d.createApkFilter();
        d.setVisible(true);
        d.removeAll();
        d.dispose();
        return d.ok?d.textFieldPath.getText():null;
    }

}
