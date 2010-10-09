/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */

package com.mightypocket.ashoter;

import com.mightypocket.utils.ResourceHelper;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author etf
 */
final class MainPanel extends JPanel {
    static final String IMAGE_CACHE_PROPERTY = "imageCache";

    private CardLayout layout = new CardLayout();
    private final Mediator mediator;
    private ImagePresenter presenter;

    MainPanel(Mediator mediator) {
        this.mediator = mediator;

        initComponents();
    }

    private void initComponents() {
        setLayout(layout);

        final JTextPane intro = new JTextPane();

        intro.setContentType("text/html");
        final ResourceMap resourceMap = mediator.getApplication().getContext().getResourceMap();
        intro.setText(ResourceHelper.loadString("about.html", resourceMap));
        intro.setEditable(false);
        intro.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (!HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType()))
                    return;
                if ("action:close".equals(e.getDescription())) {
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

        ((HTMLDocument)intro.getDocument()).setBase(AShoter.class.getResource("resources/about.html"));
        presenter = new DefaultImagePresenter(mediator);

        add(intro, "intro");
        add((Component) presenter, "main");
        if(Preferences.userNodeForPackage(AShoter.class).getBoolean(PreferencesNames.PREF_SHOW_ABOUT, false)) {
            layout.first(this);
        }  else {
            mediator.showMain();
        }
        
    }

    public void showIntro() {
        layout.first(this);
    }

    public void showMain() {
        layout.last(this);
    }

    public ImagePresenter getPresenter() {
        return presenter;
    }

}
