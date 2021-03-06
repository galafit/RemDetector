package device.general;

import bdf.*;
import comport.ComPort;
import bdf.Calibration;
import dreamrec.ApplicationException;
import jssc.SerialPortException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class Ads implements BdfProvider {
    private static final Log log = LogFactory.getLog(Ads.class);
    private List<BdfListener> bdfListeners = new ArrayList<BdfListener>();
    protected ComPort comPort;
    protected boolean isRecording;
    protected AdsConfigurator adsConfigurator;


    public abstract void setAdsConfigurator (AdsConfiguration adsConfiguration);


    @Override
    public void startReading() throws ApplicationException {
        String failConnectMessage = "Connection failed. Check com port settings.\nReset power on the target amplifier. Restart the application.";
        AdsConfiguration adsConfiguration = adsConfigurator.getAdsConfiguration();
        try {
            FrameDecoder frameDecoder = adsConfigurator.getFrameDecoder();
            frameDecoder.addFrameListener(new FrameListener() {
                @Override
                public void onFrameReceived(byte[] frame) {
                    notifyAdsDataListeners(frame);
                }
            });
            comPort = new ComPort(adsConfiguration.getComPortName(), adsConfiguration.getComPortSpeed());
            comPort.setComPortListener(frameDecoder);
            comPort.writeToPort(adsConfigurator.writeAdsConfiguration());
            isRecording = true;
        } catch (SerialPortException  e) {
            log.error(failConnectMessage, e);
            throw new ApplicationException(failConnectMessage, e);
        }
    }

    public void stopReading() {
        log.debug("Stop Reading begins");
        for (BdfListener adsBdfListener : bdfListeners) {
            adsBdfListener.onStopReading();
        }
        if (!isRecording) return;
        comPort.writeToPort(adsConfigurator.startPinLo());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.warn(e);
        }
        comPort.disconnect();
        log.debug("Stop Reading finished");
    }


    @Override
    public void addBdfDataListener(BdfListener bdfBdfListener) {
        bdfListeners.add(bdfBdfListener);
    }

    @Override
    public void removeBdfDataListener(BdfListener bdfListener) {
        bdfListeners.remove(bdfListener);
    }

    private void notifyAdsDataListeners(byte[] bdfDataRecord) {
        for (BdfListener bdfListener : bdfListeners) {
            bdfListener.onDataRecordReceived(bdfDataRecord);
        }
    }


    @Override
    public DeviceBdfConfig getBdfConfig() {
        return createBdfConfig();
    }


    private DeviceBdfConfig createBdfConfig() {
        AdsConfiguration adsConfiguration = adsConfigurator.getAdsConfiguration();
        List<SignalConfig> signalConfigList = new ArrayList<SignalConfig>();
        for (int i = 0; i < adsConfiguration.getNumberOfAdsChannels(); i++) {
            if (adsConfiguration.isChannelEnabled(i)) {
                int physicalMax = 2400000 / adsConfiguration.getChannelGain(i).getValue();
                int numberOfSamplesInEachDataRecord = adsConfiguration.getMaxDivider().getValue() / adsConfiguration.getChannelDivider(i).getValue();
                Calibration calibration = new Calibration();
                calibration.setDigitalMax(8388607);
                calibration.setDigitalMin(-8388608);
                calibration.setPhysicalMax(physicalMax);
                calibration.setPhysicalMin(-physicalMax);
                calibration.setPhysicalDimension("uV");
                SignalConfig signalConfig = new SignalConfig(numberOfSamplesInEachDataRecord, calibration);
                signalConfig.setLabel(adsConfiguration.getChannelName(i));
                signalConfig.setTransducerType("Unknown");
                signalConfig.setPrefiltering("None");
                signalConfigList.add(signalConfig);
            }
        }
        for (int i = 0; i < 3; i++) {
            int numberOfSamplesInEachDataRecord = adsConfiguration.getMaxDivider().getValue()  / adsConfiguration.getAccelerometerDivider().getValue();
            if (adsConfiguration.isAccelerometerEnabled()) {
                Calibration calibration = new Calibration();
                calibration.setDigitalMax(9610);
                calibration.setDigitalMin(-4190);
                calibration.setPhysicalMax(1000);
                calibration.setPhysicalMin(-1000);
                calibration.setPhysicalDimension("mg");
                SignalConfig signalConfig = new SignalConfig(numberOfSamplesInEachDataRecord, calibration);
                signalConfig.setLabel(adsConfiguration.getAccelerometerName() + " " + (i + 1));
                signalConfig.setTransducerType("Unknown");
                signalConfig.setPrefiltering("None");
                signalConfigList.add(signalConfig);
            }
        }

        double DurationOfDataRecord = (double) (adsConfiguration.getMaxDivider().getValue() ) / adsConfiguration.getSps().getValue();
        SignalConfig[] signalConfigArray = signalConfigList.toArray(new SignalConfig[signalConfigList.size()]);
        DeviceBdfConfig bdfConfig = new DeviceBdfConfig(DurationOfDataRecord, adsConfiguration.getNumberOfBytesInDataFormat(), signalConfigArray);
        return bdfConfig;
    }
}
