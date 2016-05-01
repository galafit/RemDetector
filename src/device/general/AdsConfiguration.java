package device.general;

/**
 *
 */
public interface AdsConfiguration {

    public int getNumberOfBytesInDataFormat();
    public Divider[] getChannelsAvailableDividers();
    public  Divider getMaxDivider();
    public int getComPortSpeed();
    public int getNumberOfAdsChannels();
    public int getNumberOfChannels();
    public String getChannelName(int channelNumber);
    public void setChannelName(int channelNumber, String channelName);
    public String getComPortName();
    public void setComPortName(String comPortName);
    public Sps getSps();
    public void setSps(Sps sps);
    public Divider getAccelerometerDivider();
    public String getAccelerometerName();
    public void setAccelerometerDivider(Divider divider);
    public boolean isChannelEnabled(int channelNumber);
    public void setChannelEnabled(int channelNumber, boolean isEnabled);
    public boolean isAccelerometerEnabled();
    public void setAccelerometerEnabled(boolean isEnabled);
    public Divider getChannelDivider(int channelNumber);
    public Gain getChannelGain(int channelNumber);
    public CommutatorState getChannelCommutatorState(int channelNumber);
    public void setChannelDivider(int channelNumber, Divider divider);
    public void setChannelGain(int channelNumber, Gain gain);
    public void setChannelCommutatorState(int channelNumber, CommutatorState commutatorState);
    public boolean isChannelLoffEnable(int channelNumber);
    public boolean isChannelRldSenseEnable(int channelNumber);
    public void setChannelRldSenseEnabled(int channelNumber, boolean isRldEnabled);
    public void setChannelLoffEnabled(int channelNumber, boolean isLoffEnabled);
    public int getTotalNumberOfDataSamplesInEachDataRecord();
    public boolean isHighResolutionMode();
    public boolean isLoffEnabled();
    public void save();
}
