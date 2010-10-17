/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */

package com.mightypocket.ashot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author Illya Yalovyy
 */
final class StatusBar extends JPanel {

    private final Mediator mediator;

    private final JLabel connectionStateLabel;
    private final JLabel connectionStateIcon;
    private final JLabel statusMessageLabel;

    private final Timer messageTimer;

    private final ResourceMap resourceMap;

    @Resource String msgConnected;
    @Resource String msgDisconnected;
    @Resource Icon iconConnected;
    @Resource Icon iconDisconnected;

    StatusBar(final Mediator mediator) {
        this.mediator = mediator;

        connectionStateLabel = new JLabel("");

        connectionStateIcon = new JLabel();
        connectionStateIcon.setVerticalAlignment(SwingConstants.CENTER);

        statusMessageLabel = new JLabel("");

        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);

        resourceMap = mediator.getApplication().getContext().getResourceMap(StatusBar.class);
        resourceMap.injectFields(this);

        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);

        GroupLayout layout = new GroupLayout(this);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(separator, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 226, Short.MAX_VALUE)
                .addComponent(connectionStateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(connectionStateIcon)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(connectionStateIcon)
                    .addComponent(connectionStateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setLayout(layout);

        // Register a listener for mediator's events
        mediator.addPropertyChangeListener(new MediatorPCL());

        updateAll();
    }

    private void updateConnectionStatus() {
        if (mediator.isConnected()) {
            connectionStateLabel.setText(mediator.getConnectedDevice());
            connectionStateIcon.setIcon(iconConnected);
        } else {
            connectionStateLabel.setText(msgDisconnected);
            connectionStateIcon.setIcon(iconDisconnected);
        }
    }

    private void updateAll() {
        updateConnectionStatus();
    }

    private final class MediatorPCL implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();

            if (Mediator.PROP_CONNECTED.equals(propertyName)) {
                updateConnectionStatus();
            }
        }

    }

    public void setMessage(String message) {
        statusMessageLabel.setText(message);
        messageTimer.restart();
    }
}
