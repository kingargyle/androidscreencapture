/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mightypocket.ashoter;

import com.mightypocket.swing.ImageSelection;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;

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
        imageLabel.setTransferHandler(new ImageSelection());
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
