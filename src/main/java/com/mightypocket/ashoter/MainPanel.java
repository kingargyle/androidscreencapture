/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */

package com.mightypocket.ashoter;

import com.mightypocket.utils.ResourceHelper;
import java.awt.CardLayout;
import java.awt.Desktop;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 * @author etf
 */
final class MainPanel extends AbstractImagePresenter {
    private CardLayout layout = new CardLayout();
    private final Mediator mediator;

    MainPanel(Mediator mediator) {
        this.mediator = mediator;

        initComponents();
    }

    private void initComponents() {
        setLayout(layout);

        JTextPane intro = new JTextPane();
        intro.setContentType("text/html");
        intro.setText(ResourceHelper.loadString("intro.html"));//
        intro.setEditable(false);
        intro.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (!HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType()))
                    return;
                if ("action:connect".equals(e.getDescription())) {
                    mediator.showMain();
                } else {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (Exception ignore) {
                        }
                    }
                }
            }
        });

        JPanel presenter = new JPanel();
        GroupLayout gl = new GroupLayout(presenter);
        presenter.setLayout(gl);

        gl.setHorizontalGroup(
            gl.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
        );
        gl.setVerticalGroup(
            gl.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imageLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
        );

        add(intro,"intro");
        add(presenter,"main");
        layout.first(this);
        
    }

    public void showIntro() {
        layout.first(this);
    }

    public void showMain() {
        layout.last(this);
    }

}
