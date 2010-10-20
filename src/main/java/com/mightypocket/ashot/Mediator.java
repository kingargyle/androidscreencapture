/*
 * Copyright (C) 2010 mightypocket.com. All rights reserved. Use is
 * subject to license terms.
 */
package com.mightypocket.ashot;

import java.awt.Dimension;
import org.jdesktop.application.Task;
import javax.swing.AbstractButton;
import java.io.File;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.List;
import org.jdesktop.application.Action;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.prefs.Preferences;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.SwingPropertyChangeSupport;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener.Adapter;
import org.jdesktop.beansbinding.BindingGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.mightypocket.utils.BindingHelper.*;

/**
 *
 * @author Illya Yalovyy
 */
public final class Mediator implements PreferencesNames {
    private final Logger logger = LoggerFactory.getLogger(Mediator.class);
    private final Preferences p = Preferences.userNodeForPackage(AShot.class);

    private ImageProcessor imageProcessor = new ImageProcessor();;
    private JMenu menuFileDevices;
    private final Map<String, JRadioButtonMenuItem> devices = new HashMap<String, JRadioButtonMenuItem>();
    private final ButtonGroup devicesGroup = new ButtonGroup();
    private final ImageSaver imageSaver;
    private final FullScreenFrame fullScreenFrame;
    private ImagePresenter presenter;

    // Constants


    // Components
    private final PropertyChangeSupport pcs;
    private final AShot application;

    private final JToolBar toolBar;
    private final JMenuBar menuBar;
    private final StatusBar statusBar;
    private final MainPanel mainPanel;
    private final AndroDemon demon;

    // State
    private ImageEx lastImage;

    public Mediator( final AShot application) {
        this.application = application;

        pcs = new SwingPropertyChangeSupport(this, true);

        toolBar = createToolBar();
        menuBar = createMenuBar();

        statusBar = new StatusBar(this);
        mainPanel = new MainPanel(this);
        presenter = mainPanel.getPresenter();

        imageSaver  = new ImageSaver(this);

        fullScreenFrame = new FullScreenFrame(this);

        demon = new AndroDemon(this);
        installListeners();

        initProperties();

    }

    private ApplicationActionMap getActionMap() {
        return application.getContext().getActionMap(this);
    }

    private void initProperties() {
        //TODO Read from preferences
        setLandscape(p.getBoolean(PREF_SCREENSHOT_LANDSCAPE, false));

        Double scale = p.getDouble(PREF_SCREENSHOT_SCALE, 0.0);

        if (scale == 0.0) {
            setScaleFit(true);
        } else {
            getImageProcessor().setScale(scale);
        }
    }

    private void installListeners() {
        demon.addTaskListener(new Adapter<Void, ImageEx>() {
            @Override
            public void process(TaskEvent<List<ImageEx>> event) {
                List<ImageEx> value = event.getValue();

                if (!value.isEmpty()) {
                    final ImageEx img = value.get(0);
                    updateImageProcessor(img);

                    final Image imgp = imageProcessor.process(img.getValue());

                    showImage(imgp);

                    if (isRecording() && !(p.getBoolean(PREF_SAVE_SKIP_DUPLICATES, true) && img.isDuplicate())) {
                        imageSaver.saveImage((saveOriginal)?img.getValue():imgp);
                    }

                    lastImage = img;
                } else {
                    showImage(generateDummyImage());
                }
            }

        });

        addPropertyChangeListener(PROP_CONNECTED_DEVICE, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                final JRadioButtonMenuItem disconnected = devices.get((String)evt.getOldValue());
                final JRadioButtonMenuItem connected = devices.get((String)evt.getNewValue());
                
                if (disconnected != null) {
                    disconnected.setSelected(false);
                }

                if (connected != null) {
                    connected.setSelected(true);
                } else {
                    devicesGroup.clearSelection();
                }
            }
        });

        addPropertyChangeListener(PROP_FULL_SCREEN, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                logger.debug("Full screen: {}", evt.getNewValue());
                boolean fs = (Boolean)evt.getNewValue();
                presenter = fs ? fullScreenFrame.getPresenter() : mainPanel.getPresenter();
            }
        });

    }

    private void updateImageProcessor(final ImageEx img) {
        final boolean ls = isLandscape();
        if (isScaleFit()) {
            Dimension customBounds = presenter.getPresenterDimension();
            
            int w = img.getValue().getWidth(null);
            int h = img.getValue().getHeight(null);

            if (landscape != img.isLandscape()) {
                final int t = w;
                w = h;
                h = t;
            }

            double scale = Math.min(customBounds.getWidth() / w, customBounds.getHeight() / h);
            imageProcessor.setScale(scale);
        }

        final boolean ccw = p.getBoolean(PREF_ROTATION_CCW, true);
        System.out.println("CCW:"+ccw);
        System.out.println("img.CCW:"+img.isCcw());

        if (ls != img.isLandscape()) {
            if (ccw == img.isCcw()) {
                if (ccw)
                    imageProcessor.setRotation(ls ? ImageProcessor.Rotation.R270 : ImageProcessor.Rotation.R90);
                else
                    imageProcessor.setRotation(ls ? ImageProcessor.Rotation.R90 : ImageProcessor.Rotation.R270);
            } else {
                if (ls) {
                    imageProcessor.setRotation(ccw ? ImageProcessor.Rotation.R270 : ImageProcessor.Rotation.R90);
                } else {
                    imageProcessor.setRotation(img.isCcw() ? ImageProcessor.Rotation.R90 : ImageProcessor.Rotation.R270);
                }
            }
        } else {
            if (ccw == img.isCcw()) {
                imageProcessor.setRotation(ImageProcessor.Rotation.R0);
            } else {
                imageProcessor.setRotation(ls ? ImageProcessor.Rotation.R180 :  ImageProcessor.Rotation.R0);
            }
        }
    }
    
    private JMenuBar createMenuBar() {
        BindingGroup menuBinding = new BindingGroup();
        ResourceMap resourceMap = application.getContext().getResourceMap(Mediator.class);
        ApplicationActionMap actionMap = getActionMap();
        JMenuBar bar = new JMenuBar();

        // Menu File
        JMenu menuFile = new JMenu(resourceMap.getString("menu.file"));
        menuFileDevices = new JMenu(resourceMap.getString("menu.file.devices.text"));
        menuFileDevices.setIcon(resourceMap.getIcon("menu.file.devices.icon"));
        menuFile.add(menuFileDevices);
        menuBinding.addBinding( bindRead(this, PROP_DEVICES, menuFileDevices, "enabled"));
        menuFile.addSeparator();
        menuFile.add(new JMenuItem(actionMap.get(ACTION_SAVE_SCREENSHOT)));
        menuFile.add(new JCheckBoxMenuItem(actionMap.get(ACTION_RECORDING)));
        menuFile.add(new JMenuItem(actionMap.get(ACTION_OPEN_DESTINATION_FOLDER)));
        menuFile.addSeparator();
        menuFile.add(new JMenuItem(actionMap.get("quit")));

        // Menu Edit
        JMenu menuEdit = new JMenu(resourceMap.getString("menu.edit"));
        menuEdit.add(new JMenuItem(actionMap.get(ACTION_COPY_TO_CLIPBOARD)));
        menuEdit.addSeparator();
        menuEdit.add(new JMenuItem(actionMap.get(ACTION_OPTIONS)));

        // Menu View
        JMenu menuView = new JMenu(resourceMap.getString("menu.view"));
        menuView.add(new JCheckBoxMenuItem(actionMap.get(ACTION_LANDSCAPE)));
        menuView.add(new JCheckBoxMenuItem(actionMap.get(ACTION_LANDSCAPE_CW)));
        menuView.addSeparator();
        menuView.add(new JMenuItem(actionMap.get(ACTION_ZOOM_IN)));
        menuView.add(new JMenuItem(actionMap.get(ACTION_ZOOM_OUT)));
        menuView.addSeparator();
        menuView.add(new JMenuItem(actionMap.get(ACTION_SIZE_ORIGINAL)));
        menuView.add(new JMenuItem(actionMap.get(ACTION_SIZE_SMALL)));
        menuView.add(new JMenuItem(actionMap.get(ACTION_SIZE_LARGE)));
        menuView.add(new JCheckBoxMenuItem(actionMap.get(ACTION_SIZE_FIT)));
        menuView.addSeparator();
        menuView.add(new JMenuItem(actionMap.get(ACTION_FULL_SCREEN)));

        // Menu Help
        JMenu menuHelp = new JMenu(resourceMap.getString("menu.help"));
        menuHelp.add(new JMenuItem(actionMap.get(ACTION_CHECK_UPDATES)));
        menuHelp.add(new JMenuItem(actionMap.get(ACTION_ABOUT)));

        bar.add(menuFile);
        bar.add(menuEdit);
        bar.add(menuView);
        bar.add(menuHelp);
        
        menuBinding.bind();

        return bar;
    }

    private JToolBar createToolBar() {
        ApplicationActionMap actionMap = getActionMap();
        JToolBar bar = new JToolBar();
        bar.setRollover(true);
        final boolean hideText = !p.getBoolean(PREF_GUI_SHOW_TEXT_IN_TOOLBAR, true);
        for (String actionName : TOOLBAR) {
            if (TOOLBAR_SEPARATOR.equals(actionName)) {
                bar.addSeparator();
            } else {
                AbstractButton bt;
                if (actionName.startsWith(TOOLBAR_TOGGLE_BUTTON)) {
                    bt = new JToggleButton(actionMap.get(StringUtils.substring(actionName, TOOLBAR_TOGGLE_BUTTON.length())));
                } else {
                    bt = new JButton(actionMap.get(actionName));
                }
                bt.setFocusable(false);
                bt.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                bt.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                bt.setHideActionText(hideText);
                bar.add(bt);
            }
        }

        return bar;
    }

    private ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    public AShot getApplication() {
        return application;
    }

    MainPanel getMainPanel() {
        return mainPanel;
    }

    JMenuBar getMenuBar() {
        return menuBar;
    }

    StatusBar getStatusBar() {
        return statusBar;
    }

    JToolBar getToolBar() {
        return toolBar;
    }

    public ImageEx getLastImage() {
        return lastImage;
    }

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public synchronized boolean hasListeners(String propertyName) {
        return pcs.hasListeners(propertyName);
    }

    public synchronized PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return pcs.getPropertyChangeListeners(propertyName);
    }

    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void startDemon() {
        application.getContext().getTaskService().execute(demon);
    }

    public void stopDemon() {
        logger.trace("stopDemon");
        demon.cancel(true);
    }

    void addDevice(final String deviceStr) {
        if (!devices.containsKey(deviceStr)) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        JRadioButtonMenuItem item = new JRadioButtonMenuItem(deviceStr);
                        item.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                JRadioButtonMenuItem source = (JRadioButtonMenuItem) e.getSource();
                                String device = source.getText();
                                demon.connectTo(device);
                            }
                        });
                        devicesGroup.add(item);
                        devices.put(deviceStr, item);
                        menuFileDevices.add(item);
                        pcs.firePropertyChange(PROP_DEVICES, null, null);
                    }
                });
            } catch (Exception ignore) {
            }
        }
    }

    void removeDevice(final String deviceStr) {
        if (devices.containsKey(deviceStr)) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        JRadioButtonMenuItem item = devices.get(deviceStr);
                        devicesGroup.remove(item);
                        devices.remove(item);
                        menuFileDevices.remove(item);
                        pcs.firePropertyChange(PROP_DEVICES, null, null);
                    }
                });
            } catch (Exception ignore) {
            }
        }
    }

    void showMain() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                toolBar.setVisible(true);
                mainPanel.showMain();
            }
        });
    }

    private Image generateDummyImage() {
        return application.getContext().getResourceMap().getImageIcon("dummyImage").getImage();
    }

    // Actions:

    public static final String ACTION_SAVE_SCREENSHOT = "saveScreenshot";
    @Action(name=ACTION_SAVE_SCREENSHOT, enabledProperty=PROP_CONNECTED)
    public void saveScreenshot() {
        imageSaver.saveImage(lastImage.getValue());
    }

    public static final String ACTION_RECORDING = "recording";
    @Action(name=ACTION_RECORDING, enabledProperty=PROP_CONNECTED, selectedProperty=PROP_RECORDING)
    public void recording() {
        demon.resetLastImage();
        
    }

    public static final String ACTION_OPEN_DESTINATION_FOLDER = "openDestinationFolder";
    @Action(name=ACTION_OPEN_DESTINATION_FOLDER)
    public void openDestinationFolder() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(new File(p.get(PREF_DEFAULT_FILE_FOLDER, "")));
            } catch (Exception ignore) {
            }
        }
    }

    public static final String ACTION_COPY_TO_CLIPBOARD = "copyToClipboard";
    @Action(name=ACTION_COPY_TO_CLIPBOARD, enabledProperty=PROP_CONNECTED)
    public void copyToClipboard() {
        mainPanel.getPresenter().copy();
    }

    public static final String ACTION_OPTIONS = "options";
    @Action(name=ACTION_OPTIONS)
    public void options() {
        OptionsDialog.showDialog(this);
    }

    public static final String ACTION_LANDSCAPE = "landscape";
    @Action(name=ACTION_LANDSCAPE, selectedProperty=PROP_LANDSCAPE)
    public void landscape() {
        p.putBoolean(PREF_ROTATION_CCW, true);
        p.putBoolean(PREF_SCREENSHOT_LANDSCAPE, isLandscape());
        updateLastImage();
    }

    public static final String ACTION_LANDSCAPE_CW = "landscapeCW";
    @Action(name=ACTION_LANDSCAPE_CW, selectedProperty=PROP_LANDSCAPE)
    public void landscapeCW() {
        p.putBoolean(PREF_ROTATION_CCW, false);
        p.putBoolean(PREF_SCREENSHOT_LANDSCAPE, isLandscape());
        updateLastImage();
    }

    public static final String ACTION_FULL_SCREEN = "fullScreen";
    @Action(name=ACTION_FULL_SCREEN, enabledProperty=PROP_CONNECTED)
    public void fullScreen() {
        fullScreenFrame.showFullScreen();
    }

    public static final String ACTION_SIZE_ORIGINAL = "sizeOriginal";
    @Action(name=ACTION_SIZE_ORIGINAL)
    public void sizeOriginal() {
        setScale(1.0);
    }

    public static final String ACTION_SIZE_LARGE = "sizeLarge";
    @Action(name=ACTION_SIZE_LARGE)
    public void sizeLarge() {
        setScale(1.5);
    }

    public static final String ACTION_SIZE_SMALL = "sizeSmall";
    @Action(name=ACTION_SIZE_SMALL)
    public void sizeSmall() {
        setScale(0.5);
    }

    public static final String ACTION_ZOOM_IN = "zoomIn";
    @Action(name=ACTION_ZOOM_IN)
    public void zoomIn() {
        double scale = imageProcessor.getScale() * 1.1;
        if (scale > 3.0) scale = 3.0;
        setScale(scale);
    }

    public static final String ACTION_ZOOM_OUT = "zoomOut";
    @Action(name=ACTION_ZOOM_OUT)
    public void zoomOut() {
        double scale = imageProcessor.getScale() * 0.9090909090909091;
        if (scale < 0.1) scale = 0.1;
        setScale(scale);
    }

    public static final String ACTION_SIZE_FIT = "sizeFit";
    @Action(name=ACTION_SIZE_FIT, selectedProperty=PROP_SCALE_FIT)
    public void sizeFit() {
        p.putDouble(PREF_SCREENSHOT_SCALE, 0.0);
        updateLastImage();
    }

    public static final String ACTION_CHECK_UPDATES = "checkUpdates";
    @Action(name=ACTION_CHECK_UPDATES, block=Task.BlockingScope.ACTION)
    public Task checkUpdates() {
        return new UpdateChecker(this);
    }

    public static final String ACTION_ABOUT = "about";
    @Action(name = ACTION_ABOUT)
    public void about() {
        toolBar.setVisible(false);
        mainPanel.showIntro();
    }

    // Properties

    private String connectedDevice;
    public static final String PROP_CONNECTED_DEVICE = "connectedDevice";
    public String getConnectedDevice() {
        return connectedDevice;
    }

    public void setConnectedDevice(String connectedDevice) {
        String oldValue = this.connectedDevice;
        this.connectedDevice = connectedDevice;
        pcs.firePropertyChange(PROP_CONNECTED_DEVICE, oldValue, connectedDevice);

        setConnected(connectedDevice != null);
    }

    private boolean recording;
    public static final String PROP_RECORDING = "recording";
    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        boolean oldRecording = this.recording;
        this.recording = recording;
        pcs.firePropertyChange(PROP_RECORDING, oldRecording, recording);
    }

    private boolean autoSave = false;
    public static final String PROP_AUTO_SAVE = "autoSave";
    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        boolean oldValue = this.autoSave;
        this.autoSave = autoSave;
        pcs.firePropertyChange(PROP_AUTO_SAVE, oldValue, autoSave);
    }

    private boolean fullScreen = false;
    public static final String PROP_FULL_SCREEN = "fullScreen";
    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        boolean oldValue = this.fullScreen;
        this.fullScreen = fullScreen;
        pcs.firePropertyChange(PROP_FULL_SCREEN, oldValue, fullScreen);
    }

    private boolean saveOriginal = true;
    public static final String PROP_SAVE_ORIGINAL = "saveOriginal";
    public boolean isSaveOriginal() {
        return saveOriginal;
    }

    public void setSaveOriginal(boolean saveOriginal) {
        boolean oldValue = this.saveOriginal;
        this.saveOriginal = saveOriginal;
        pcs.firePropertyChange(PROP_SAVE_ORIGINAL, oldValue, saveOriginal);
    }

    private boolean connected = false;
    public static final String PROP_CONNECTED = "connected";
    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        boolean oldValue = this.connected;
        this.connected = connected;
        pcs.firePropertyChange(PROP_CONNECTED, oldValue, connected);
    }

    public static final String PROP_DEVICES = "devices";
    public boolean isDevices() {
        return !devices.isEmpty();
    }

    private volatile boolean landscape = false;
    public static final String PROP_LANDSCAPE = "landscape";
    public boolean isLandscape() {
        return landscape;
    }

    public void setLandscape(boolean landscape) {
        boolean oldValue = this.landscape;
        this.landscape = landscape;
        pcs.firePropertyChange(PROP_LANDSCAPE, oldValue, landscape);
    }

    private boolean scaleFit;
    public static final String PROP_SCALE_FIT = "scaleFit";
    public boolean isScaleFit() {
        return scaleFit;
    }

    public void setScaleFit(boolean scaleFit) {
        boolean oldScaleFit = this.scaleFit;
        this.scaleFit = scaleFit;
        pcs.firePropertyChange(PROP_SCALE_FIT, oldScaleFit, scaleFit);
    }

    private void showImage(Image img) {
        if (presenter != null)
            presenter.setImage(img);
    }

    private void updateLastImage() {
        if (lastImage != null) {
            updateImageProcessor(lastImage);
            showImage(imageProcessor.process(lastImage.getValue()));
        }
    }

    private void setScale(double scale) {
        setScaleFit(false);
        getImageProcessor().setScale(scale);
        p.putDouble(PREF_SCREENSHOT_SCALE, scale);
        updateLastImage();
    }

    public void setStatus(String key, Object... args) {
        String string = application.getContext().getResourceMap().getString(key, args);
        statusBar.setMessage(string);
    }

    // We can provide configuration option for toolbar
    // Toolbar
    private static final String TOOLBAR_SEPARATOR = "-----";
    private static final String TOOLBAR_TOGGLE_BUTTON = "TOGGLE::";
    private static final String[] TOOLBAR = {
        ACTION_SAVE_SCREENSHOT,
        TOOLBAR_SEPARATOR,
        TOOLBAR_TOGGLE_BUTTON + ACTION_RECORDING,
        TOOLBAR_SEPARATOR,
        ACTION_FULL_SCREEN,
        TOOLBAR_SEPARATOR,
        ACTION_OPTIONS,
        ACTION_ABOUT
    };

    void executeAction(String actionName) {
        application.getContext().getActionManager().getActionMap(Mediator.class, this).get(actionName).actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
    }

}
