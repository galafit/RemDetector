package properties;


import device.general.*;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class AdsConfigurationProperties implements AdsConfiguration {

    private static final Log log = LogFactory.getLog(AdsConfigurationProperties.class);

    private static final String COM_PORT_NAME = "comPort";
    private static final String COM_PORT_SPEED = "comPortSpeed";
    private static final String SPS = "sps";
    private static final String CHANNEL_NAME = "nameChannel";
    private static final String CHANNEL_DIVIDER = "dividerChannel";
    private static final String CHANNEL_GAIN = "gainChannel";
    private static final String CHANNEL_COMMUTATOR_STATE = "commutatorStateChannel";
    private static final String CHANNEL_IS_ENABLED = "isEnabledChannel";
    private static final String CHANNEL_IS_LOFF_ENABLED = "isLoffEnabledChannel";
    private static final String CHANNEL_IS_RLD_SENSE_ENABLED = "isRldSenseEnabledChannel";
    private static final String ACCELEROMETER_IS_ENABLED = "isEnabledAccelerometer";
    private static final String ACCELEROMETER_DIVIDER = "dividerAccelerometer";
    private static final String ACCELEROMETER_NAME = "nameAccelerometer";
    private static final String MAX_DIVIDER = "dividerMax";
    private static final String NUMBER_OF_ADS_CHANNELS = "numberOfAdsChannels";
    private static final String AVAILABLE_DIVIDERS = "availableDividers";
    private static final String IS_BATTERY_VOLTAGE_MEASURE = "isBatteryVoltageMeasureEnabled";

    private static final int NUMBER_OF_BYTES_IN_DATA_FORMAT = 3;
    private boolean isHighResolutionMode = true;
    private PropertiesConfiguration config;

    public AdsConfigurationProperties(String propertiesFileName) {
        try {
            config = new PropertiesConfiguration(propertiesFileName);
        } catch (ConfigurationException e) {
            log.error(e);
        }
    }

    @Override
    public Divider[] getChannelsAvailableDividers() {
        String[] dividerStrings = config.getStringArray(AVAILABLE_DIVIDERS);
        Divider[] dividers = new Divider[dividerStrings.length];
        try {
            for(int i = 0; i< dividerStrings.length; i++) {
                dividers[i] = Divider.valueOf(Integer.valueOf(dividerStrings[i]));

            }
        } catch (IllegalArgumentException e) {
            String msg = "ads_config.properties file: " +  "Available Dividers " + e.getMessage();
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return dividers;
    }

    @Override
    public int getNumberOfChannels() {
        return getNumberOfAdsChannels() + 3;
    }

    @Override
    public String getChannelName(int channelNumber) {
        return config.getString(CHANNEL_NAME + channelNumber);
    }

    @Override
    public void setChannelName(int channelNumber, String channelName) {
        config.setProperty(CHANNEL_NAME + channelNumber, channelName);
    }

    public int getNumberOfBytesInDataFormat() {
        return NUMBER_OF_BYTES_IN_DATA_FORMAT;
    }

    public Divider getMaxDivider() {
        try {
            return Divider.valueOf(config.getInt(MAX_DIVIDER));
        } catch (IllegalArgumentException e) {
            String msg = "ads_config.properties file: " +  "max divider " + e.getMessage();
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    public int getComPortSpeed() {
        return config.getInt(COM_PORT_SPEED);
    }

    public int getNumberOfAdsChannels() {
        return config.getInt(NUMBER_OF_ADS_CHANNELS);
    }

    public String getComPortName() {
        return config.getString(COM_PORT_NAME);
    }

    public void setComPortName(String comPortName) {
        config.setProperty(COM_PORT_NAME, comPortName);
    }

    public Sps getSps() {
        try {
            return Sps.valueOf(config.getInt(SPS));
        } catch (IllegalArgumentException e) {
            String msg = "ads_config.properties file " + e.getMessage();
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    public void setSps(Sps sps) {
        config.setProperty(SPS, sps);
    }

    public Divider getAccelerometerDivider() {
        try {
            return Divider.valueOf(config.getInt(ACCELEROMETER_DIVIDER));
        } catch (IllegalArgumentException e) {
            String msg = "ads_config.properties file: " +  "accelerometer divider " + e.getMessage();
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public String getAccelerometerName() {
        return config.getString(ACCELEROMETER_NAME);
    }

    public boolean isChannelEnabled(int channelNumber) {
        return config.getBoolean(CHANNEL_IS_ENABLED + channelNumber);
    }

    public void setChannelEnabled(int channelNumber, boolean isEnabled) {
        config.setProperty(CHANNEL_IS_ENABLED + channelNumber, isEnabled);
    }

    public boolean isAccelerometerEnabled() {
        return config.getBoolean(ACCELEROMETER_IS_ENABLED);
    }

    public void setAccelerometerEnabled(boolean isEnabled) {
        config.setProperty(ACCELEROMETER_IS_ENABLED, isEnabled);
    }


    public Divider getChannelDivider(int channelNumber) {
        try {
            return Divider.valueOf(config.getInt(CHANNEL_DIVIDER + channelNumber));
        } catch (IllegalArgumentException e) {
            String msg = "ads_config.properties file: " + channelNumber + "channel " + e.getMessage();
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    public Gain getChannelGain(int channelNumber) {
        try {
            return Gain.valueOf(config.getInt(CHANNEL_GAIN + channelNumber));
        } catch (IllegalArgumentException e) {
            String msg = "ads_config.properties file: " + channelNumber + "channel " + e.getMessage();
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    public CommutatorState getChannelCommutatorState(int channelNumber) {
        return CommutatorState.valueOf(config.getString(CHANNEL_COMMUTATOR_STATE + channelNumber));
    }

    @Override
    public void setAccelerometerDivider(Divider divider) {
        config.setProperty(ACCELEROMETER_DIVIDER, divider);
    }

    public void setChannelDivider(int channelNumber, Divider divider) {
        config.setProperty(CHANNEL_DIVIDER + channelNumber, divider);
    }

    public void setChannelGain(int channelNumber, Gain gain) {
        config.setProperty(CHANNEL_GAIN + channelNumber, gain);
    }

    public void setChannelCommutatorState(int channelNumber, CommutatorState commutatorState) {
        config.setProperty(CHANNEL_COMMUTATOR_STATE + channelNumber, commutatorState);
    }


    public boolean isChannelLoffEnable(int channelNumber) {
        return config.getBoolean(CHANNEL_IS_LOFF_ENABLED + channelNumber);
    }

    public boolean isChannelRldSenseEnable(int channelNumber) {
        return config.getBoolean(CHANNEL_IS_RLD_SENSE_ENABLED + channelNumber);
    }

    @Override
    public boolean isBatteryVoltageMeasureEnabled() {
        return config.getBoolean(IS_BATTERY_VOLTAGE_MEASURE);
    }

    public void setChannelRldSenseEnabled(int channelNumber, boolean isRldEnabled) {
        config.setProperty(CHANNEL_IS_RLD_SENSE_ENABLED + channelNumber, isRldEnabled);
    }

    public void setChannelLoffEnabled(int channelNumber, boolean isLoffEnabled) {
        config.setProperty(CHANNEL_IS_LOFF_ENABLED + channelNumber, isLoffEnabled);
    }

    public int getTotalNumberOfDataSamplesInEachDataRecord() {
        int totalNumberOfDataSamples = 0;
        for (int i = 0; i < getNumberOfAdsChannels(); i++) {
            if(isChannelEnabled(i)) {
                int numberOfSamplesInEachDataRecord = getMaxDivider().getValue() / getChannelDivider(i).getValue();
                totalNumberOfDataSamples += numberOfSamplesInEachDataRecord;
            }

        }
        if(isAccelerometerEnabled()) {
            int numberOfSamplesInEachDataRecord = getMaxDivider().getValue()  / getAccelerometerDivider().getValue();
            totalNumberOfDataSamples += 3 * numberOfSamplesInEachDataRecord;
        }
        return totalNumberOfDataSamples;
    }


    public void save() {
        try {
            config.save();
        } catch (ConfigurationException e) {
            log.error(e);
        }
    }


    public boolean isHighResolutionMode() {
        return isHighResolutionMode;
    }
    public boolean isLoffEnabled() {
        for (int i = 0; i < getNumberOfAdsChannels(); i++) {
            if(isChannelLoffEnable(i)){
                return true;
            }
        }
        return false;
    }



    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < getNumberOfAdsChannels(); i++) {
            sb.append( "AdsChannelConfiguration_"+i+
                    " {divider=" +getChannelDivider(i) +
                    ", isEnabled=" + isChannelEnabled(i) +
                    ", gain=" + getChannelGain(i) +
                    ", commutatorState=" + getChannelCommutatorState(i) +
                    ", isLoffEnable=" + isChannelLoffEnable(i) +
                    ", isRldSenseEnabled=" + isChannelRldSenseEnable(i) +
                    '}' + "\n");

            sb.append("\r");
        }
        return "AdsConfiguration{" +
                "sps=" + getSps() +
                ", isAccelerometerEnabled=" + isAccelerometerEnabled() +
                ", accelerometerDivider=" + getAccelerometerDivider() +
                ", comPortName='" + getComPortName() + '\'' +
                ", isHighResolutionMode=" + isHighResolutionMode +
                '}' + sb.toString();

    }
}
