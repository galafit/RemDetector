package properties;

import dreamrec.ApplicationException;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Iterator;

public class ApplicationProperties  {
    private static final Log log = LogFactory.getLog(ApplicationProperties.class);

    private static final String DEVICE_CLASSNAME = "device.classname";
    private static final String DEVICE_CONFIG_FILENAME = "device.config_filename";
    private static final String DEVICE_CHANNEL_NAME = "device.channel.name";
    private static final String IS_FREQUENCY_AUTO_ADJUSTMENT = "is_frequency_auto_adjustment";

    private static final String ACCELEROMETER_REM_FREQUENCY = "rem.accelerometer_frequency";
    private static final String EOG_REM_FREQUENCY = "rem.eog_frequency";

    private FileConfiguration config;

    public ApplicationProperties(String file) throws ApplicationException {
        try {
            config = new PropertiesConfiguration(file);
            config.setAutoSave(false);
        } catch (ConfigurationException e) {
            log.error(e);
            throw new ApplicationException("Error reading from properties file: " + file);
        }
    }

    public String getDeviceClassName()  {
        return config.getString(DEVICE_CLASSNAME);

    }

    public String getDeviceConfigFileName()  {
        return config.getString(DEVICE_CONFIG_FILENAME);
    }

    public boolean isFrequencyAutoAdjustment() {
        boolean defaultValue = true;
        return config.getBoolean(IS_FREQUENCY_AUTO_ADJUSTMENT, defaultValue);
    }

    public String[] getDeviceChannelsLabels() {
        Iterator<String> keys = config.getKeys(DEVICE_CHANNEL_NAME);
        HashMap<Integer, String> labelsMap = new HashMap<Integer, String>();
        int indexMax = -1;

        while(keys.hasNext()) {
            String key = keys.next();
            String indexStr = key.substring(DEVICE_CHANNEL_NAME.length()+1);
            try {
                Integer index = Integer.parseInt(indexStr);
                String label = config.getString(key);
                labelsMap.put(index, label);
                indexMax = Math.max(indexMax, index);
            } catch (NumberFormatException e) {

            }
        }
        if(indexMax >= 0) {
            String[] labels = new String[indexMax+1];
            for (Integer index : labelsMap.keySet()) {
                labels[index] = labelsMap.get(index);
            }
            return labels;
        }
        return null;
    }

    public int getAccelerometerRemFrequency() {
        int defaultValue = -1;
        return config.getInt(ACCELEROMETER_REM_FREQUENCY, defaultValue);
    }


    public int getEogRemFrequency() {
        int defaultValue = -1;
        return config.getInt(EOG_REM_FREQUENCY, defaultValue);
    }
}
