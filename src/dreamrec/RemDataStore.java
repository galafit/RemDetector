package dreamrec;

import bdf.BdfConfig;
import bdf.BdfProvider;
import bdf.BdfRecordsJoiner;
import data.*;
import filters.FilterDerivativeRem;
import functions.BooleanAND;
import functions.Composition;
import functions.Rising;
import functions.Trigger;
import prefilters.PreFilter;

import java.util.ArrayList;

/**
 * Created by mac on 17/02/15.
 */
public class RemDataStore  implements DataStoreListener {

    private double accMovementLimit =  0.15 * 1000;

    private DataStore dataStore;
    private RemChannels remChannels;
    private BdfProvider bdfProvider;

    private ArrayList<DataStoreListener> updateListeners = new ArrayList<DataStoreListener>();

    public RemDataStore(BdfProvider bdfProvider, RemChannels remChannels) throws ApplicationException {
        this.remChannels = remChannels;
        this.bdfProvider = bdfProvider;
        dataStore = new DataStore(bdfProvider);
        dataStore.addListener(this);
        int numberOfSignals = bdfProvider.getBdfConfig().getSignalConfigs().length;
        if(remChannels.getEog1() >= numberOfSignals) {
            String msg = "EOG channel number should be less then total number of channels";
            throw new ApplicationException(msg);
        }
        if(remChannels.getEog2() >= numberOfSignals) {
            String msg = "EOG channel number should be less then total number of channels";
            throw new ApplicationException(msg);
        }
        if(remChannels.getAccelerometerX() >= numberOfSignals) {
            String msg = "AccelerometerX channel number should be less then total number of channels";
            throw new ApplicationException(msg);
        }
        if(remChannels.getAccelerometerY() >= numberOfSignals) {
            String msg = "AccelerometerY channel number should be less then total number of channels";
            throw new ApplicationException(msg);
        }
        if(remChannels.getAccelerometerZ() >= numberOfSignals) {
            String msg = "AccelerometerZ channel number should be less then total number of channels";
            throw new ApplicationException(msg);
        }
    }

    public void setChannelsMask(boolean[] channelsMask) throws ApplicationException {
        if((remChannels.getEog1() < channelsMask.length) && channelsMask[remChannels.getEog1()] == false) {
            String errorMsg = "EOG channel should be enable";
            throw new ApplicationException(errorMsg);
        }
        if((remChannels.getAccelerometerX() < channelsMask.length) && channelsMask[remChannels.getAccelerometerX()] == false) {
            String errorMsg = "AccelerometerX channel should be enable";
            throw new ApplicationException(errorMsg);
        }
        if((remChannels.getAccelerometerY() < channelsMask.length) && channelsMask[remChannels.getAccelerometerY()] == false) {
            String errorMsg = "AccelerometerY channel should be enable";
            throw new ApplicationException(errorMsg);
        }
        if((remChannels.getAccelerometerZ() < channelsMask.length) && channelsMask[remChannels.getAccelerometerZ()] == false) {
            String errorMsg = "AccelerometerZ channel should be enable";
            throw new ApplicationException(errorMsg);
        }

        dataStore.setChannelsMask(channelsMask);
    }


    public void configure(final RemConfigurator remConfigurator) throws ApplicationException {
        if (remConfigurator != null) {
            BdfConfig bdfConfig = bdfProvider.getBdfConfig();
            int numberOfRecordsToJoin = remConfigurator.getNumberOfRecordsToJoin(bdfConfig);
            BdfProvider bdfProviderNew = new BdfRecordsJoiner(bdfProvider, numberOfRecordsToJoin);
            PreFilter[] prefilters = remConfigurator.getPreFilters(bdfConfig, remChannels);

            bdfProvider.removeBdfDataListener(dataStore);

            dataStore = new DataStore(bdfProviderNew);
            dataStore.setPreFilters(prefilters);
            dataStore.addListener(this);
        }
    }

    public double getAccMovementLimit() {
        // movementLimit = (int)(0.15 / getAccXData().getDataCalibration().getGain());
        return accMovementLimit;
    }

    public int getNumberOfChannels() {
        return dataStore.getNumberOfChannels();
    }

    public void addListener(DataStoreListener dataStoreListener) {
        updateListeners.add(dataStoreListener);
    }

    public DataList getChannelData(int channelNumber) {
        return dataStore.getChannelData(channelNumber);
    }

    private void fireDataUpdated() {
        for (DataStoreListener listener : updateListeners) {
            listener.onDataUpdate();
        }
    }


    public void setStartTime(long startTime) {
        dataStore.setStartTime(startTime);
    }

    public long getStartTime() { return dataStore.getStartTime(); }


    @Override
    public void onDataUpdate() {
        fireDataUpdated();
    }

    public DataSeries getEegData() {
        if(remChannels.getEeg() >= 0) {
            return dataStore.getSignalData(remChannels.getEeg());
        }
        return null;

    }


    public DataSeries getEog1Data() {
        return dataStore.getSignalData(remChannels.getEog1());
    }

    public DataSeries getEog2Data() {
        if(remChannels.getEog2() >= 0) {
            return dataStore.getSignalData(remChannels.getEog2());
        }
        return null;
    }


    public DataSeries getAccXData() {
        return dataStore.getSignalData(remChannels.getAccelerometerX());
    }

    public DataSeries getAccYData() {
        return dataStore.getSignalData(remChannels.getAccelerometerY());
    }

    public DataSeries getAccZData() {
        return dataStore.getSignalData(remChannels.getAccelerometerZ());
    }

    /**
     * Определяем величину пропорциональную движению головы
     * (дельта между max и min значением сигналов акселерометра на 3 точках).
     * Суммируем амплитуды движений по трем осям.
     * За ноль принят шумовой уровень.
     */
    public DataSeries getAccMovementData() {
        Composition accMovement = new Composition();
        try {
            accMovement.add(new Rising(getAccXData()));
            accMovement.add(new Rising(getAccYData()));
            accMovement.add(new Rising(getAccZData()));
        } catch (ApplicationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        return accMovement;
    }

    private DataSeries isNotMove() {
        return new Trigger(getAccMovementData(), accMovementLimit);
    }

    private DataSeries isEogOk() {
        FilterDerivativeRem derivativeRem = new FilterDerivativeRem(getEog1Data());
        return new Trigger(derivativeRem, 400);
    }

public DataSeries isSleep() {
        BooleanAND isSleep = new BooleanAND();
        try {
            DataCompressor isNotMove = new DataCompressor(isNotMove(), CompressionType.BOOLEAN);
            double samplingInterval = 1;
            if(isEogOk().getScaling() != null) {
                samplingInterval = isEogOk().getScaling().getSamplingInterval();
            }
            isNotMove.setSamplingInterval(samplingInterval);
            isSleep.add(isEogOk());
            isSleep.add(isNotMove);
            //return isSleep;
            return isNotMove;
        } catch (ApplicationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}






