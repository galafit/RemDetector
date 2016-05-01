package dreamrec;

import bdf.BdfHeaderData;
import device.general.AdsConfiguration;

import java.io.File;

/**
 * Created by mac on 17/02/15.
 */
public interface InputEventHandler {
    void startRecording(String directory, String filename, String patient, String record) throws ApplicationException;
    void stopRecording()throws ApplicationException;
    void readFromFile(BdfHeaderData bdfHeaderData) throws ApplicationException;
    BdfHeaderData getFileInfo(File file) throws ApplicationException;
    AdsConfiguration getDeviceConfig() throws ApplicationException;
    String[] getFileExtensions();
    String[] getComPortNames();
}
