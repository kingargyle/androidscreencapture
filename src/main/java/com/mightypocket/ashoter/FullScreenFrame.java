/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.ashoter;

import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;

/**
 *
 * @author Illya Yalovyy
 */
public class FullScreenFrame extends JFrame {
    private final Mediator mediator;
    private final DefaultImagePresenter presenter;
    private final GraphicsDevice defaultScreenDevice;
    JFrame mainFrame;

    public FullScreenFrame(Mediator mediator) throws HeadlessException {
        this.mediator = mediator;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		defaultScreenDevice = ge.getDefaultScreenDevice();

        presenter = new DefaultImagePresenter(mediator);

		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setUndecorated(true);
		getContentPane().add(presenter);

        presenter.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hideFullScreen();
            }
        });
        presenter.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ESCAPE || evt.getKeyCode() == KeyEvent.VK_F11) {
                    hideFullScreen();
                }
            }

        });

		pack();
    }

    private void hideFullScreen() {
        defaultScreenDevice.setFullScreenWindow(null);
		setVisible(false);
		mainFrame.setVisible(true);
		mainFrame.requestFocus();
        mediator.setFullScreen(false);
    }

    public void showFullScreen() {
        mainFrame = mediator.getApplication().getMainFrame();
        mainFrame.setVisible(false);
        defaultScreenDevice.setFullScreenWindow(this);
        this.validate();
        ((Component)presenter).requestFocus();
        mediator.setFullScreen(true);
    }

    public ImagePresenter getPresenter() {
        return presenter;
    }
    
}
