/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.ashoter;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jdesktop.application.ResourceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Illya Yalovyy
 */
final class ImageSaver implements PreferencesNames {

    private static final String PNG_EXTENSION = ".png";
    private final Mediator mediator;
    private final Preferences p = Preferences.userNodeForPackage(AShot.class);
    private final Logger logger = LoggerFactory.getLogger(ImageSaver.class);
    private JFileChooser fc;

    ImageSaver(Mediator mediator) {
        this.mediator = mediator;
    }

    void saveImage(Image img) {
        File target = requestFile();
        if (target == null) {
            return;
        }
        try {
            ImageIO.write((RenderedImage) img, "PNG", target);
            mediator.setStatus("status.saved", target.getName());
        } catch (IOException ex) {
            logger.error("Cannot save image to file.", ex);
            mediator.getApplication().showErrorMessage("error.save.image", target.getPath());
        }
    }

    private File getNextAutoFile() {
        int cnt = p.getInt(PREF_FILE_COUNTER, 0) + 1;
        String path = p.get(PREF_DEFAULT_FILE_FOLDER, "");
        String prefix = p.get(PREF_DEFAULT_FILE_PREFIX, "screenshot");
        String fileName = String.format("%s_%05d.png", prefix, cnt);
        p.putInt(PREF_FILE_COUNTER, cnt);
        File file = new File(path, fileName);
        return file;
    }

    private File requestFile() {
        if (mediator.isRecording() || mediator.isAutoSave()) {
            return getNextAutoFile();
        } else {
            JFileChooser d = createFileChooser();
            int returnVal = d.showSaveDialog(mediator.getApplication().getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                // add png extension
                String fileName = file.getName();
                if (!fileName.toLowerCase().endsWith(PNG_EXTENSION)) {
                    file = new File(file.getParentFile(), fileName + PNG_EXTENSION);
                }
                return file;
            }
            return null;
        }
    }

    private JFileChooser createFileChooser() {
        if (fc == null) {
            FileFilter ff = new FileNameExtensionFilter("PNG file", "png");
            ResourceMap map = mediator.getApplication().getContext().getResourceMap();
            fc = new JFileChooser();
            fc.setDialogTitle(map.getString("dialog.savefile.title"));
            fc.addChoosableFileFilter(ff);
        }
        return fc;
    }
}
