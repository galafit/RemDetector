package gui;

import comport.ComportFacade;
import device.general.*;
import dreamrec.ApplicationException;
import dreamrec.InputEventHandler;
import gui.comport_gui.ComPortModelMock;
import gui.comport_gui.ComportUI;
import gui.file_gui.DirectoryField;
import gui.file_gui.FileToSaveUI;
import gui.layouts.TableLayout;
import gui.layouts.TableOption;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by gala on 25/04/16.
 */
public class DeviceSettings extends JDialog  {

    private String patientIdentificationLabel = "Patient";
    private String recordingIdentificationLabel = "Record";
    private String spsLabel = "Sampling Frequency (Hz)";

    private String channelsPanelLabel = "Channel";
    private String identificationPanelLabel ="Identification";

    private JComboBox spsField;
    private JComboBox[] channelFrequency;
    private JComboBox[] channelGain;
    private JComboBox[] channelCommutatorState;
    private JCheckBox[] channelEnable;
    private JTextField[] channelName;

    private JComboBox accelerometerFrequency;
    private JTextField accelerometerName;
    private JCheckBox accelerometerEnable;
    private JTextField patientIdentification;
    private JTextField recordingIdentification;
    private JButton startButton = new JButton("Start");
    private JButton stopButton = new JButton("Stop");
    private JButton cancelButton = new JButton("Cancel");

    private FileToSaveUI fileToSaveUI;
    private ComportUI comportUI;


    private String title = "Recorder Configuration";
    private JComponent[] channelsHeaders = {new JLabel("Number"), new JLabel("Enable"), new JLabel("Name"), new JLabel("Frequency (Hz)"),
            new JLabel("Gain"), new JLabel("Commutator State")};

    private InputEventHandler eventHandler;
    private AdsConfiguration deviceConfig;
    private GuiConfig guiConfig;


    public DeviceSettings(JFrame parent,  InputEventHandler eventHandler, GuiConfig guiConfig) throws ApplicationException {
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        deviceConfig = eventHandler.getDeviceConfig();
        this.eventHandler = eventHandler;
        this.guiConfig = guiConfig;

        init();
        arrangeForm();
        setActions();
        loadDataFromModel();
        setVisible(true);
    }


    private void init() {
        int adsChannelsNumber = deviceConfig.getNumberOfAdsChannels();
        spsField = new JComboBox(Sps.values());
        comportUI = new ComportUI(new ComportFacade());
        fileToSaveUI = new FileToSaveUI();
        fileToSaveUI.setDirectory(guiConfig.getDefaultDirectoryToSave());

        int fieldLength = 20;
        patientIdentification = new JTextField(fieldLength);
        fieldLength = 30;
        recordingIdentification = new JTextField(fieldLength);

        channelFrequency = new JComboBox[adsChannelsNumber];
        channelGain = new JComboBox[adsChannelsNumber];
        channelCommutatorState = new JComboBox[adsChannelsNumber];
        channelEnable = new JCheckBox[adsChannelsNumber];
        channelName = new JTextField[adsChannelsNumber];

        fieldLength = 16;
        for (int i = 0; i < adsChannelsNumber; i++) {
            channelFrequency[i] = new JComboBox();
            channelGain[i] = new JComboBox();
            channelCommutatorState[i] = new JComboBox();
            channelEnable[i] = new JCheckBox();
            channelName[i] = new JTextField(fieldLength);
        }
        accelerometerEnable = new JCheckBox();
        accelerometerName = new JTextField(fieldLength);
        accelerometerFrequency = new JComboBox();
    }

    private void setActions() {

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    saveDataToModel();
                    eventHandler.startRecording(getDirectory(), getFilename(), getPatientIdentification(), getRecordingIdentification());
                    dispose();
                }
                catch (ApplicationException ex) {
                    showMessage(ex.getMessage());
                }


            }

        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    eventHandler.stopRecording();
                    dispose();
                } catch (ApplicationException e) {
                    showMessage(e.getMessage());
                }
            }
        });


        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                saveDataToModel();
                dispose();
            }
        });


        for (int i = 0; i < deviceConfig.getNumberOfAdsChannels(); i++) {
            channelEnable[i].addActionListener(new AdsChannelEnableListener(i));
        }

        accelerometerEnable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JCheckBox checkBox = (JCheckBox) actionEvent.getSource();
                if (checkBox.isSelected()) {
                    enableAccelerometer(true);
                } else {
                    enableAccelerometer(false);
                }
            }
        });


        spsField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JComboBox comboBox = (JComboBox) actionEvent.getSource();
                Sps sps = (Sps) comboBox.getSelectedItem();
                setChannelsFrequencies(sps);
            }
        });



        patientIdentification.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                patientIdentification.selectAll();
            }
        });



        recordingIdentification.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                recordingIdentification.selectAll();
            }
        });


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                saveDataToModel();

            }
        });
    }


    private void arrangeForm() {
        setTitle(title);

        int hgap = 5;
        int vgap = 0;
        JPanel spsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
        spsPanel.add(new JLabel(spsLabel));
        spsPanel.add(spsField);

        JPanel comPortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
        comPortPanel.add(comportUI);

        hgap = 60;
        vgap = 15;
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, hgap, vgap));
        topPanel.setBorder(BorderFactory.createTitledBorder(""));
        topPanel.add(comPortPanel);
        topPanel.add(spsPanel);


        hgap = 20;
        vgap = 5;
        JPanel channelsPanel = new JPanel(new TableLayout(channelsHeaders.length, new TableOption(TableOption.CENTRE, TableOption.CENTRE), hgap, vgap));

        for (JComponent component : channelsHeaders) {
            channelsPanel.add(component);
        }

        for (int i = 0; i < deviceConfig.getNumberOfAdsChannels(); i++) {
            channelsPanel.add(new JLabel(" " + (i + 1) + " "));
            channelsPanel.add(channelEnable[i]);
            channelsPanel.add(channelName[i]);
            channelsPanel.add(channelFrequency[i]);
            channelsPanel.add(channelGain[i]);
            channelsPanel.add(channelCommutatorState[i]);
        }

        // Add line of accelerometer
        channelsPanel.add(new JLabel(" " + (1 + deviceConfig.getNumberOfAdsChannels() + " ")));
        channelsPanel.add(accelerometerEnable);
        channelsPanel.add(accelerometerName);
        channelsPanel.add(accelerometerFrequency);


        hgap = 5;
        vgap = 0;
        JPanel patientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
        patientPanel.add(new JLabel(patientIdentificationLabel));
        patientPanel.add(patientIdentification);

        JPanel recordingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
        recordingPanel.add(new JLabel(recordingIdentificationLabel));
        recordingPanel.add(recordingIdentification);

        hgap = 5;
        vgap = 0;
        JPanel identificationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
        identificationPanel.add(patientPanel);
        identificationPanel.add(recordingPanel);

        hgap = 20;
        vgap = 5;
        JPanel identificationBorderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
        identificationBorderPanel.setBorder(BorderFactory.createTitledBorder(identificationPanelLabel));
        identificationBorderPanel.add(identificationPanel);


        hgap = 0;
        vgap = 5;
        JPanel adsPanel = new JPanel(new BorderLayout(hgap, vgap));
        adsPanel.add(channelsPanel, BorderLayout.NORTH);
        adsPanel.add(identificationBorderPanel, BorderLayout.CENTER);
        adsPanel.add(fileToSaveUI, BorderLayout.SOUTH);

        hgap = 10;
        vgap = 10;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, hgap, vgap));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(cancelButton);

        // Root Panel of the SettingsWindow
        add(topPanel, BorderLayout.NORTH);
        add(adsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // set the same size for identificationPanel and  saveAsPanel
        int height = Math.max(identificationBorderPanel.getPreferredSize().height, fileToSaveUI.getPreferredSize().height);
        int width = Math.max(identificationBorderPanel.getPreferredSize().width, fileToSaveUI.getPreferredSize().width);
        fileToSaveUI.setPreferredSize(new Dimension(width, height));
        identificationBorderPanel.setPreferredSize(new Dimension(width, height));


        pack();
        // place the window to the screen center
        setLocationRelativeTo(null);
    }

    private void disableEnableFields(boolean isEnable) {
        spsField.setEnabled(isEnable);
        patientIdentification.setEnabled(isEnable);
        recordingIdentification.setEnabled(isEnable);
        fileToSaveUI.setEnabled(isEnable);

        accelerometerName.setEnabled(isEnable);
        accelerometerEnable.setEnabled(isEnable);
        accelerometerFrequency.setEnabled(isEnable);

        for (int i = 0; i < deviceConfig.getNumberOfAdsChannels(); i++) {
            channelEnable[i].setEnabled(isEnable);
            channelName[i].setEnabled(isEnable);
            channelFrequency[i].setEnabled(isEnable);
            channelGain[i].setEnabled(isEnable);
            channelCommutatorState[i].setEnabled(isEnable);
        }
    }


    private void loadDataFromModel() {
        spsField.setSelectedItem(deviceConfig.getSps());
        comportUI.setCurrentPort(deviceConfig.getComPortName());
        patientIdentification.setText("Default patient");
        recordingIdentification.setText("Default record");
        int numberOfAdsChannels = deviceConfig.getNumberOfAdsChannels();
        for (int i = 0; i < numberOfAdsChannels; i++) {
            channelName[i].setText(deviceConfig.getChannelName(i));
            channelEnable[i].setSelected(deviceConfig.isChannelEnabled(i));
            if (!deviceConfig.isChannelEnabled(i)) {
                enableAdsChannel(i, false);
            }
        }

        accelerometerName.setText("Accelerometer");
        accelerometerEnable.setSelected(deviceConfig.isAccelerometerEnabled());
        if (! deviceConfig.isAccelerometerEnabled()) {
            enableAccelerometer(false);
        }
        setChannelsFrequencies(deviceConfig.getSps());
        setChannelsGain();
        setChannelsCommutatorState();
    }


    private void saveDataToModel() {
        deviceConfig.setSps(getSps());
        deviceConfig.setComPortName(getComPortName());
        for (int i = 0; i < deviceConfig.getNumberOfAdsChannels(); i++) {
            deviceConfig.setChannelDivider(i, getChannelDivider(i));
            deviceConfig.setChannelEnabled(i, isChannelEnable(i));
            deviceConfig.setChannelGain(i, getChannelGain(i));
            deviceConfig.setChannelName(i, getChannelName(i));
            deviceConfig.setChannelCommutatorState(i, getChannelCommutatorState(i));
        }
        deviceConfig.setAccelerometerEnabled(isAccelerometerEnable());
        deviceConfig.setAccelerometerDivider(getAccelerometerDivider());
        deviceConfig.save();
        guiConfig.setDefaultDirectoryToSave(getDirectory());

    }

    private void setChannelsFrequencies(Sps sps) {
        Divider[] adsChannelsDividers = deviceConfig.getChannelsAvailableDividers();
        // set available frequencies
        for (int i = 0; i < deviceConfig.getNumberOfAdsChannels(); i++) {
            channelFrequency[i].removeAllItems();
            for (Divider divider : adsChannelsDividers) {
                channelFrequency[i].addItem(sps.getValue()/divider.getValue());
            }
            // select channel frequency
            Integer frequency = sps.getValue() / deviceConfig.getChannelDivider(i).getValue();
            channelFrequency[i].setSelectedItem(frequency);
        }
        accelerometerFrequency.removeAllItems();
        accelerometerFrequency.addItem(sps.getValue()/deviceConfig.getAccelerometerDivider().getValue());
        if (deviceConfig.getNumberOfAdsChannels() > 0) {
            // put the size if field   accelerometerFrequency equal to the size of fields  channelFrequency
            accelerometerFrequency.setPreferredSize(channelFrequency[0].getPreferredSize());
        }
    }

    private String getComPortName() {
        return comportUI.getComPortName();
    }

    private void setChannelsGain(){

        for (int i = 0; i < deviceConfig.getNumberOfAdsChannels(); i++) {
            channelGain[i].removeAllItems();
            for (Gain gain : Gain.values()) {
                channelGain[i].addItem(gain.getValue());
            }
            channelGain[i].setSelectedItem(deviceConfig.getChannelGain(i).getValue());
        }
    }

    private void setChannelsCommutatorState(){
        for (int i = 0; i < deviceConfig.getNumberOfAdsChannels(); i++) {
            channelCommutatorState[i].removeAllItems();
            for (CommutatorState commutatorState : CommutatorState.values()) {
                channelCommutatorState[i].addItem(commutatorState.toString());
            }
            channelCommutatorState[i].setSelectedItem(deviceConfig.getChannelCommutatorState(i).toString());
        }
    }

    private void enableAdsChannel(int channelNumber, boolean isEnable) {
        channelFrequency[channelNumber].setEnabled(isEnable);
        channelGain[channelNumber].setEnabled(isEnable);
        channelCommutatorState[channelNumber].setEnabled(isEnable);
        channelName[channelNumber].setEnabled(isEnable);
    }


    private void enableAccelerometer(boolean isEnable) {
        accelerometerName.setEnabled(isEnable);
        accelerometerFrequency.setEnabled(isEnable);

    }

    private Divider getChannelDivider(int channelNumber) {
        int divider = deviceConfig.getSps().getValue() / getChannelFrequency(channelNumber);
        return Divider.valueOf(divider);
    }

    private Divider getAccelerometerDivider() {
        int divider = deviceConfig.getSps().getValue() / getAccelerometerFrequency();
        return Divider.valueOf(divider);
    }


    private int getChannelFrequency(int channelNumber) {
        return (Integer) channelFrequency[channelNumber].getSelectedItem();
    }

    private Gain getChannelGain(int channelNumber) {
        return Gain.valueOf(((Integer)channelGain[channelNumber].getSelectedItem()));
    }

    private CommutatorState getChannelCommutatorState(int channelNumber) {
        return CommutatorState.valueOf(((String)channelCommutatorState[channelNumber].getSelectedItem()));
    }

    private boolean isChannelEnable(int channelNumber) {
        return channelEnable[channelNumber].isSelected();
    }

    private String getChannelName(int channelNumber) {
        return channelName[channelNumber].getText();
    }


    private String getPatientIdentification() {
        return patientIdentification.getText();
    }

    private String getRecordingIdentification() {
        return recordingIdentification.getText();
    }

    private boolean isAccelerometerEnable() {
        return accelerometerEnable.isSelected();
    }

    private int getAccelerometerFrequency() {
        return (Integer) accelerometerFrequency.getSelectedItem();
    }

    private Sps getSps() {
        return (Sps) spsField.getSelectedItem();
    }

    private String getFilename() {
        return fileToSaveUI.getFilename();
    }

    private String getDirectory() {
        return fileToSaveUI.getDirectory();
    }



    private String convertToHtml(String text, int rowLength) {
        StringBuilder html = new StringBuilder("<html>");
        String[] givenRows = text.split("\n");
        for (String givenRow : givenRows) {
            String[] splitRows = split(givenRow, rowLength);
            for (String row : splitRows) {
                html.append(row);
                html.append("<br>");
            }
        }
        html.append("</html>");
        return html.toString();
    }

    // split input string to the  array of strings with length() <= rowLength
    private String[] split(String text, int rowLength) {
        ArrayList<String> resultRows = new ArrayList<String>();
        StringBuilder row = new StringBuilder();
        String[] words = text.split(" ");
        for (String word : words) {
            if ((row.length() + word.length()) < rowLength) {
                row.append(word);
                row.append(" ");
            } else {
                resultRows.add(row.toString());
                row = new StringBuilder(word);
                row.append(" ");
            }
        }
        resultRows.add(row.toString());
        String[] resultArray = new String[resultRows.size()];
        return resultRows.toArray(resultArray);
    }


    private class AdsChannelEnableListener implements ActionListener {
        private int channelNumber;

        private AdsChannelEnableListener(int channelNumber) {
            this.channelNumber = channelNumber;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JCheckBox checkBox = (JCheckBox) actionEvent.getSource();
            if (checkBox.isSelected()) {
                enableAdsChannel(channelNumber, true);
            } else {
                enableAdsChannel(channelNumber, false);
            }
        }
    }

    private void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }
}