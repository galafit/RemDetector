package properties;

import dreamrec.ApplicationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

/**
 *  Test realisation using only Java native java.util.Properties.
 *  Don´t have closing Dialog bug like apache.properties
 *  (not saving properties on closing dialog with standard close button)
 */
public class JavaAppProperties {
    private static final Log log = LogFactory.getLog(ApplicationProperties.class);

    private static final String DEVICE_CLASSNAME = "device.classname";
    private static final String DEVICE_CONFIG_FILENAME = "device.config_filename";
    private static final String DEVICE_CHANNEL_NAME = "device.channel.name";
    private static final String IS_FREQUENCY_AUTO_ADJUSTMENT = "is_frequency_auto_adjustment";

    private static final String ACCELEROMETER_REM_FREQUENCY = "rem.accelerometer_frequency";
    private static final String EOG_REM_FREQUENCY = "rem.eog_frequency";

    private JavaPropertiesWrapper property;

    public JavaAppProperties (File file) throws ApplicationException {
        property = new JavaPropertiesWrapper(file);
    }

    public String getDeviceClassName() throws ApplicationException {
        return property.getString(DEVICE_CLASSNAME);

    }

    public String getDeviceConfigFileName() throws ApplicationException {
        return property.getString(DEVICE_CONFIG_FILENAME);
    }


    public String[] getDeviceChannelsLabels() {
       /* Iterator<String> keys = property.getKeys(DEVICE_CHANNEL_NAME);
        HashMap<Integer, String> labelsMap = new HashMap<Integer, String>();
        int indexMax = -1;

        while(keys.hasNext()) {
            String key = keys.next();
            String indexStr = key.substring(DEVICE_CHANNEL_NAME.length()+1);
            try {
                Integer index = Integer.parseInt(indexStr);
                String label = property.getString(key);
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
        }*/
        return null;
    }

    public Integer getAccelerometerRemFrequency() {
        int defaultValue = -1;
        if (property.getInt(ACCELEROMETER_REM_FREQUENCY) != null) {
            return property.getInt(ACCELEROMETER_REM_FREQUENCY);
        }
        return defaultValue;
    }


    public Integer getEogRemFrequency() {
        int defaultValue = -1;
        if (property.getInt(EOG_REM_FREQUENCY) != null) {
            return property.getInt(EOG_REM_FREQUENCY);
        }
        return defaultValue;
    }
}
