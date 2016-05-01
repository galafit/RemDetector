package bdf;


import com.sun.istack.internal.Nullable;
import dreamrec.ApplicationException;
import dreamrec.RemChannels;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Al on 03.11.14.
 */
public class BdfHeaderData extends BdfConfigWrapper {
    public static final String[] FILE_EXTENSIONS = {"bdf", "edf"};
    private long startTime = -1;
    private String patientIdentification = "Default patient";
    private String recordingIdentification = "Default record";
    private int numberOfDataRecords = -1;
    private File file;


    public BdfHeaderData(BdfConfig bdfConfig) {
        super(bdfConfig);
    }

    public BdfHeaderData(double durationOfDataRecord, int numberOfBytesInDataFormat, SignalConfig... signalsConfigList) {
        super(new DeviceBdfConfig(durationOfDataRecord, numberOfBytesInDataFormat, signalsConfigList));
    }

    public File getFile() {
        return file;
    }


    public void setFile(File parent, String filename) {
        if(parent.isDirectory()) {
            file = new File(parent, normalizeFilename(filename));
        }
        else  {
            file = new File(parent.getParent(), normalizeFilename(filename));
        }
    }

    public void setFile(String parent, String filename) {
        setFile(new File(parent), filename);
    }

    public void setFile(File file) {
        if (file.isFile()) {
            this.file = file;
        } else {
            setFile(file, null);
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getPatientIdentification() {
        return patientIdentification;
    }

    public void setPatientIdentification(String patientIdentification) {
        this.patientIdentification = patientIdentification;
    }

    public String getRecordingIdentification() {
        return recordingIdentification;
    }

    public void setRecordingIdentification(String recordingIdentification) {
        this.recordingIdentification = recordingIdentification;
    }

    public int getNumberOfDataRecords() {
        return numberOfDataRecords;
    }

    public void setNumberOfDataRecords(int numberOfDataRecords) {
        this.numberOfDataRecords = numberOfDataRecords;
    }

    public int getNumberOfSignals() {
        return getSignalConfigs().length;
    }

    public double[] getSignalFrequencies() {
        double[] signalsFrequencies = new double[getNumberOfSignals()];
        for (int i = 0; i < getNumberOfSignals(); i++) {
            signalsFrequencies[i] = getSignalConfigs()[i].getNumberOfSamplesInEachDataRecord() / getDurationOfDataRecord();
        }
        return signalsFrequencies;
    }

    public void setSignalsLabels(String[] signalsLabels) {
        if (signalsLabels != null) {
            SignalConfig[] signalsConfigList = getSignalConfigs();
            int length = Math.min(signalsConfigList.length, signalsLabels.length);
            for (int i = 0; i < length; i++) {
                if (signalsLabels[i] != null) {
                    signalsConfigList[i].setLabel(signalsLabels[i]);
                }
            }
        }
    }

    public String[] getSignalsLabels() {
        String[] signalsLabels = new String[getNumberOfSignals()];
        for (int i = 0; i < getNumberOfSignals(); i++) {
            signalsLabels[i] = getSignalConfigs()[i].getLabel();
        }
        return signalsLabels;
    }


    public int[] getNormalizedSignalsFrequencies() {
        return BdfNormalizer.getNormalizedSignalsFrequencies(this);
    }

    private String normalizeFilename(@Nullable String filename) {
        String defaultFilename = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(new Date(System.currentTimeMillis()))
                + "." + FILE_EXTENSIONS[0];

        // if filename is default filename
        if (filename == null || filename.isEmpty()) {
            return defaultFilename;
        }
        filename = filename.trim();

        // if filename has no extension
        if (filename.lastIndexOf('.') == -1) {
            filename = filename.concat(".").concat(FILE_EXTENSIONS[0]);
            return filename;
        }
        // if  extension  match with one from given FILE_EXTENSIONS
        // (?i) makes it case insensitive (catch BDF as well as bdf)
        for (String ext : FILE_EXTENSIONS) {
            if (filename.matches("(?i).*\\." + ext)) {
                return filename;
            }
        }
        // If the extension match with NONE from given FILE_EXTENSIONS. We need to replace it
        filename = filename.substring(0, filename.lastIndexOf(".") + 1).concat(FILE_EXTENSIONS[0]);
        return filename;
    }

}
