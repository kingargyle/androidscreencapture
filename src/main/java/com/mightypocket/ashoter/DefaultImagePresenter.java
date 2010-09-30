/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.ashoter;

import com.mightypocket.swing.ImageSelection;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.prefs.Preferences;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;

/**
 *
 * @author Illya Yalovyy
 */
public class DefaultImagePresenter extends JPanel implements ImagePresenter, PreferencesNames {
    protected final JLabel imageLabel = new JLabel();
    private final Preferences p = Preferences.userNodeForPackage(AShoter.class);
    private final Mediator mediator;

    public DefaultImagePresenter(Mediator mediator) {
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(p.getInt(PREF_GUI_PANEL_BACKGROUND, 0)));
        imageLabel.setTransferHandler(new ImageSelection());
        this.mediator = mediator;

        GroupLayout gl = new GroupLayout(this);
        setLayout(gl);

        gl.setHorizontalGroup(
            gl.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
        );
        gl.setVerticalGroup(
            gl.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imageLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
        );
    }

    @Override
    public void setImage(Image img) {
        imageLabel.setIcon(new ImageIcon(img));
    }

    @Override
    public Dimension getPresenterDimension() {
        return imageLabel.getParent().getSize();
    }

    @Override
    public void copy() {
        TransferHandler handler = imageLabel.getTransferHandler();
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        handler.exportToClipboard(imageLabel, systemClipboard,
            TransferHandler.COPY);
    }

}
