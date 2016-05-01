package dreamrec;

import bdf.*;
import comport.ComPort;
import device.general.AdsConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mac on 17/02/15.
 */
public class  Controller  implements InputEventHandler {
    private static final Log log = LogFactory.getLog(Controller.class);

    private List<ControllerListener> listenerList = new ArrayList<ControllerListener>();

    private ServiceLocator serviceLocator;
    private BdfProvider bdfProvider;
    private BdfHeaderData bdfHeaderData;
    private BdfWriter bdfWriter;
    private boolean isRemMode;

    public Controller(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    public void setRemMode(boolean isRemMode) {
        this.isRemMode = isRemMode;
    }

    public void addListener(ControllerListener listener) {
        listenerList.add(listener);
    }

    public void removeListener(ControllerListener listener) {
        listenerList.remove(listener);
    }


    private void fireDataStoreUpdated(Object dataStore) {
        for (ControllerListener listener : listenerList) {
            listener.dataStoreUpdated(dataStore);
        }
    }

    @Override
    public void readFromFile(BdfHeaderData bdfHeaderData) throws ApplicationException {
        stopRecording();
        File file = bdfHeaderData.getFile();
        if (file.isFile()) { // file
            BdfReader bdfReader = new BdfReader(file);
            bdfProvider = bdfReader;
            bdfHeaderData = bdfReader.getBdfConfig();
        }
        else {
            throw new ApplicationException("File: " + file + " is not valid");
        }
        bdfHeaderData.setPatientIdentification(bdfHeaderData.getPatientIdentification());
        bdfHeaderData.setRecordingIdentification(bdfHeaderData.getRecordingIdentification());
        bdfHeaderData.setSignalsLabels(bdfHeaderData.getSignalsLabels());
        BdfHeaderWriter.writeBdfHeader(bdfHeaderData, file);

        if (isRemMode) {
            RemChannels remChannels = new RemChannels(bdfHeaderData.getSignalsLabels());
            RemDataStore dataStore  = new RemDataStore(bdfProvider, remChannels);
            dataStore.configure(serviceLocator.getRemConfigurator());
            dataStore.setChannelsMask(remChannels.getRemActiveChannels());
            dataStore.setStartTime(bdfHeaderData.getStartTime());
            fireDataStoreUpdated(dataStore);

        } else {
            DataStore dataStore = new DataStore(bdfProvider);
            dataStore.setStartTime(bdfHeaderData.getStartTime());

            fireDataStoreUpdated(dataStore);
        }

        bdfProvider.startReading();
    }

    @Override
    public void startRecording(String directory, String filename, String patient, String recording) throws ApplicationException {
        stopRecording();
        BdfProvider bdfDevice = serviceLocator.getDevice();
        bdfProvider = bdfDevice;
        bdfHeaderData = new BdfHeaderData(bdfDevice.getBdfConfig());
        bdfHeaderData.setFile(directory, filename);
        bdfHeaderData.setPatientIdentification(patient);
        bdfHeaderData.setRecordingIdentification(recording);
        bdfWriter = new BdfWriter(bdfHeaderData);
        bdfWriter.setFrequencyAutoAdjustment(false);
        bdfProvider.addBdfDataListener(bdfWriter);

        if (isRemMode) {
            RemChannels remChannels = new RemChannels(bdfHeaderData.getSignalsLabels());
            RemDataStore dataStore  = new RemDataStore(bdfProvider, remChannels);
            dataStore.configure(serviceLocator.getRemConfigurator());
            dataStore.setChannelsMask(remChannels.getRemActiveChannels());
            dataStore.setStartTime(bdfHeaderData.getStartTime());
            fireDataStoreUpdated(dataStore);

        } else {
            DataStore dataStore = new DataStore(bdfProvider);
            dataStore.setStartTime(bdfHeaderData.getStartTime());

            fireDataStoreUpdated(dataStore);
        }
        bdfProvider.startReading();
    }

    @Override
    public void stopRecording() throws ApplicationException {
        if(bdfProvider != null) {
            bdfProvider.stopReading();
            bdfProvider = null;
        }
    }

    @Override
    public BdfHeaderData getFileInfo(File file) throws ApplicationException {
        return BdfHeaderReader.readBdfHeader(file);
    }

    @Override
    public AdsConfiguration getDeviceConfig() throws ApplicationException {
        return serviceLocator.getDeviceConfig();
    }


    @Override
    public String[] getFileExtensions() {
        return BdfHeaderData.FILE_EXTENSIONS;
    }

    @Override
    public String[] getComPortNames() {
        return ComPort.getportNames();
    }
}
