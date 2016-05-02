package gui;

import bdf.BdfHeaderData;
import dreamrec.ApplicationException;
import dreamrec.InputEventHandler;
import dreamrec.RecordingSettings;
import graph.GraphViewer;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class MainWindow extends JFrame {
    private final String TITLE = "BioRecorder";
    private final Color BG_COLOR = Color.BLACK;
    private final Color MENU_BG_COLOR = Color.LIGHT_GRAY;
    private final Color MENU_TEXT_COLOR = Color.BLACK;

    protected GraphViewer graphViewer;
    private JToolBar menu = new JToolBar();
    private JDialog deviceSettings;

    private GuiConfig guiConfig;
    private InputEventHandler eventHandler;

    public MainWindow(InputEventHandler eventHandler, GuiConfig guiConfig) {
        this.eventHandler = eventHandler;
        this.guiConfig = guiConfig;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // super.windowClosing(e);
                close();
            }
        });
        setTitle(TITLE);
        getContentPane().setBackground(BG_COLOR);
        menu.setBackground(MENU_BG_COLOR);
        menu.setForeground(MENU_TEXT_COLOR);
        menu.setBorder(BorderFactory.createEmptyBorder());
        formMenu();
        setPreferredSize(getWorkspaceDimension());
        pack();
        setVisible(true);
    }

    private void close() {
        try {
            eventHandler.stopRecording();
        } catch (ApplicationException e) {
            showMessage(e.getMessage());
        }
        System.exit(0);
    }

    public void setGraphViewer(GraphViewer graphViewer) {
        if (this.graphViewer != null) {
            remove(this.graphViewer);
        }
        this.graphViewer = graphViewer;
        add(graphViewer, BorderLayout.CENTER);
        graphViewer.requestFocusInWindow();
        validate();
    }

    private void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }


    private Dimension getWorkspaceDimension() {
        // To get the effective screen size (the size of the screen without the taskbar and etc)
        // GraphicsEnvironment has a method which returns the maximum available size,
        // accounting all taskbars etc. no matter where they are aligned
        Rectangle dimension = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int width = dimension.width;
        int height = dimension.height;
        return new Dimension(width, height);
    }


    private void formMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setBackground(MENU_BG_COLOR);
        fileMenu.setForeground(MENU_TEXT_COLOR);

        JButton fileButton = new JButton("File");
        menu.add(fileButton);


        JButton biorecorderButton = new JButton("BioRecorder");
        menu.add(biorecorderButton);

        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = chooseFileToRead();
                if (file != null) {
                    try {
                        new FileSettings(MainWindow.this, file, eventHandler);
                        setTitle(file.getName());

                    } catch (ApplicationException ex) {
                        showMessage(ex.getMessage());
                    }
                }
            }
        });

        biorecorderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    deviceSettings = new DeviceSettings(MainWindow.this, eventHandler, guiConfig);
                } catch (ApplicationException ex) {
                    showMessage(ex.getMessage());
                }
            }
        });


        add(menu, BorderLayout.NORTH);
    }

    private File chooseFileToRead() {
        String[] fileExtensions = eventHandler.getFileExtensions();
        String extensionDescription = fileExtensions[0];
        for (int i = 1; i < fileExtensions.length; i++) {
            extensionDescription = extensionDescription.concat(", ").concat(fileExtensions[i]);
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(guiConfig.getDirectoryToRead());
        fileChooser.setFileFilter(new FileNameExtensionFilter(extensionDescription, fileExtensions));
        int fileChooserState = fileChooser.showOpenDialog(this);
        if (fileChooserState == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            guiConfig.setDirectoryToRead(file.getParent());
            return file;
        }
        return null;
    }

}
