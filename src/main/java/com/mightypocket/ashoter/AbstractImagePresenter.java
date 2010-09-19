/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mightypocket.ashoter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author etf
 */
public abstract class AbstractImagePresenter extends JPanel implements ImagePresenter {
    protected final JLabel imageLabel = new JLabel();

    public AbstractImagePresenter() {
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.BLACK);
        imageLabel.setForeground(Color.red);
    }

    @Override
    public void setImage(Image img) {
        imageLabel.setIcon(new ImageIcon(img));
    }

    @Override
    public int getPresenterHeight() {
        return imageLabel.getHeight();
    }

    @Override
    public int getPresenterWidth() {
        return imageLabel.getWidth();
    }

    public Dimension getPresenterDimension() {
        return imageLabel.getParent().getSize();
    }
}
