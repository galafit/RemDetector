package bdf;

import dreamrec.ApplicationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class BdfWriter implements BdfListener {

    private static final Log LOG = LogFactory.getLog(BdfWriter.class);
    private final BdfHeaderData bdfHeaderData;
    private RandomAccessFile fileToSave;
    private long startRecordingTime;
    private long stopRecordingTime;
    private int numberOfDataRecords;
    private boolean stopRecordingRequest;
    boolean isFrequencyAutoAdjustment = true;

    public BdfWriter(BdfHeaderData bdfHeaderData)  throws ApplicationException {
        this.bdfHeaderData = bdfHeaderData;
        try {
            this.fileToSave = new RandomAccessFile(bdfHeaderData.getFile(), "rw");
        } catch (FileNotFoundException e) {
            LOG.error(e);
            throw new ApplicationException("File: " + bdfHeaderData.getFile().getAbsolutePath() + "could not be written");
        }
    }

    public void setFrequencyAutoAdjustment(boolean isFrequencyAutoAdjustment) {
        this.isFrequencyAutoAdjustment = isFrequencyAutoAdjustment;
    }


    @Override
    public synchronized void onDataRecordReceived(byte[] bdfDataRecord) {
        if (!stopRecordingRequest) {
            if (numberOfDataRecords == 0) {
                startRecordingTime = System.currentTimeMillis() - (long) bdfHeaderData.getDurationOfDataRecord(); //1 second (1000 msec) duration of a data record
                bdfHeaderData.setStartTime(startRecordingTime);
                bdfHeaderData.setNumberOfDataRecords(-1);
                try {
                    fileToSave.write(BdfHeaderWriter.createBdfHeader(bdfHeaderData));
                } catch (IOException e) {
                    LOG.error(e);
                    throw new RuntimeException(e);
                }
            }
            numberOfDataRecords++;
            stopRecordingTime = System.currentTimeMillis();
            try {
                fileToSave.write(bdfDataRecord);
            } catch (IOException e) {
                LOG.error(e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public synchronized void onStopReading() {
        if (stopRecordingRequest) return;
        stopRecordingRequest = true;
        bdfHeaderData.setStartTime(startRecordingTime);
        bdfHeaderData.setNumberOfDataRecords(numberOfDataRecords);
        // if BdfProvide(device) don't have quartz we should calculate actualDurationOfDataRecord
        double actualDurationOfDataRecord = (stopRecordingTime - startRecordingTime) * 0.001 / numberOfDataRecords;
        try {
            fileToSave.seek(0);
            if(isFrequencyAutoAdjustment) {
                fileToSave.write(BdfHeaderWriter.createBdfHeader(bdfHeaderData, actualDurationOfDataRecord));
            }
            else{
                fileToSave.write(BdfHeaderWriter.createBdfHeader(bdfHeaderData));
            }
            fileToSave.close();
        } catch (IOException e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SS");
        LOG.info("Start recording time = " + startRecordingTime + " (" + dateFormat.format(new Date(startRecordingTime)));
        LOG.info("Stop recording time = " + stopRecordingTime + " (" + dateFormat.format(new Date(stopRecordingTime)));
        LOG.info("Number of data records = " + numberOfDataRecords);
        LOG.info("Duration of a data record = " + actualDurationOfDataRecord);
    }
}
