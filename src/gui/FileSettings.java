package gui;

import bdf.BdfHeaderData;
import dreamrec.ApplicationException;
import dreamrec.InputEventHandler;
import dreamrec.RecordingSettings;
import gui.layouts.TableLayout;
import gui.layouts.TableOption;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Created by gala on 25/04/16.
 */
public class FileSettings extends JDialog {

    private final String PATIENT_IDENTIFICATION_LABEL = "Patient";
    private final String RECORD_IDENTIFICATION_LABEL = "Record";

    private final int IDENTIFICATION_LENGTH = 40;
    private final int CHANNEL_NAME_LENGTH = 16;

    private JButton startButton = new JButton("Read");
    private JButton cancelButton = new JButton("Cancel");

    private JLabel[] channelsFrequenciesFields;
    private JTextField[] channelsLabelsFields;

    private JTextField patientIdentificationField;
    private JTextArea recordIdentificationField;


    private JComponent[] headersForChannelsSettings = {new JLabel("Number"), new JLabel("Name"),
            new JLabel("Frequency (Hz)")};

    private BdfHeaderData bdfHeaderData;
    private InputEventHandler eventHandler;


    public FileSettings(JFrame parent, File file, InputEventHandler eventHandler) throws ApplicationException {
        super(parent, ModalityType.APPLICATION_MODAL);
        bdfHeaderData = eventHandler.getFileInfo(file);
        this.eventHandler = eventHandler;
        setTitle("File: "+ bdfHeaderData.getFile().getName());
        int numberOfChannels = bdfHeaderData.getNumberOfSignals();
        patientIdentificationField = new JTextField(IDENTIFICATION_LENGTH);
        recordIdentificationField = new JTextArea(2, IDENTIFICATION_LENGTH);
        patientIdentificationField.setDocument(new FixSizeDocument(IDENTIFICATION_LENGTH * 2));
        recordIdentificationField.setDocument(new FixSizeDocument(IDENTIFICATION_LENGTH * 2));

        channelsFrequenciesFields = new JLabel[numberOfChannels];
        channelsLabelsFields = new JTextField[numberOfChannels];

        for (int i = 0; i < numberOfChannels; i++) {
            channelsFrequenciesFields[i] = new JLabel();
            channelsLabelsFields[i] = new JTextField(CHANNEL_NAME_LENGTH);
            channelsLabelsFields[i].setDocument(new FixSizeDocument(CHANNEL_NAME_LENGTH));
        }
        arrangeForm();
        setActions();
        loadData();
        setVisible(true);
    }

    private void loadData() {
        patientIdentificationField.setText(bdfHeaderData.getPatientIdentification());
        recordIdentificationField.setText(bdfHeaderData.getRecordingIdentification());

        int[] frequencies = bdfHeaderData.getNormalizedSignalsFrequencies();
        String[] labels = bdfHeaderData.getSignalsLabels();
        for (int i = 0; i < labels.length; i++) {
            channelsFrequenciesFields[i].setText(String.valueOf(frequencies[i]));
            channelsLabelsFields[i].setText(labels[i]);

        }
        pack();

    }

    private void setActions() {
        patientIdentificationField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                patientIdentificationField.selectAll();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                getData();
                try{
                    eventHandler.readFromFile(bdfHeaderData);
                    dispose();
                } catch (ApplicationException e) {
                    showMessage(e.getMessage());
                }
            }
        });
    }


    private void getData() {
        bdfHeaderData.setSignalsLabels(getChannelsLabels());
        bdfHeaderData.setRecordingIdentification(getRecordIdentification());
        bdfHeaderData.setPatientIdentification(getPatientIdentification());
    }


    private void arrangeForm() {
        int hgap = 10;
        int vgap = 5;

        JPanel patientPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
        patientPanel.add(new JLabel(PATIENT_IDENTIFICATION_LABEL));
        patientPanel.add(patientIdentificationField);
        patientIdentificationField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel recordingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
        recordingPanel.add(new JLabel(RECORD_IDENTIFICATION_LABEL));
        recordingPanel.add(recordIdentificationField);
        recordIdentificationField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        hgap = 5;
        vgap = 0;
        JPanel identificationPanel = new JPanel(new BorderLayout(hgap, vgap));
        identificationPanel.add(patientPanel, BorderLayout.CENTER);
        identificationPanel.add(recordingPanel, BorderLayout.SOUTH);

        hgap = 20;
        vgap = 0;
        JPanel topPanel = new JPanel(new BorderLayout(hgap, vgap));
        topPanel.add(identificationPanel,BorderLayout.CENTER);

        JPanel channelsBorderPanel = new JPanel();

        hgap = 20;
        vgap = 5;
        JPanel channelsPanel = new JPanel(new TableLayout(headersForChannelsSettings.length, new TableOption(TableOption.CENTRE, TableOption.CENTRE), hgap, vgap));
        for (JComponent component : headersForChannelsSettings) {
            channelsPanel.add(component);
        }
        int numberOfChannels = channelsLabelsFields.length;
        for (int i = 0; i < numberOfChannels; i++) {
            channelsPanel.add(new JLabel(" " + (i + 1) + " "));
            channelsPanel.add(channelsLabelsFields[i]);
            channelsPanel.add(channelsFrequenciesFields[i]);
        }

        if (numberOfChannels > 0) {
            hgap = 0;
            vgap = 10;
            channelsBorderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
            channelsBorderPanel.setBorder(BorderFactory.createTitledBorder("Channels"));
            channelsBorderPanel.add(channelsPanel);
        }


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);



        // Root Panel of the SettingsWindow
        add(topPanel, BorderLayout.NORTH);
        add(channelsBorderPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);


        pack();
        // place the window to the screen center
        setLocationRelativeTo(null);
    }

    private String[] getChannelsLabels() {
        String[] labels = new String[channelsLabelsFields.length];
        for (int i = 0; i < channelsLabelsFields.length; i++) {
            labels[i] = channelsLabelsFields[i].getText();
        }
        return labels;
    }

    private String getPatientIdentification() {
        return patientIdentificationField.getText();
    }

    private String getRecordIdentification() {
        return recordIdentificationField.getText();
    }

    private void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

}
