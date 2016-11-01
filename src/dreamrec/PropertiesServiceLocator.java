package dreamrec;

import bdf.BdfProvider;
import device.general.Ads;
import device.general.AdsConfiguration;
import gui.GuiConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import properties.AdsConfigurationProperties;
import properties.ApplicationProperties;
import properties.GuiProperties;

import java.io.File;

public class PropertiesServiceLocator implements ServiceLocator {
    private static final Log log = LogFactory.getLog(PropertiesServiceLocator.class);
    ApplicationProperties appProperties;

    public PropertiesServiceLocator(ApplicationProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public GuiConfig getGuiConfig() throws ApplicationException {
        return new GuiProperties(new File("gui.properties"));
    }

    @Override
    public AdsConfiguration getDeviceConfig() throws ApplicationException {
        String adsConfigFilename =  appProperties.getDeviceConfigFileName();
        if(adsConfigFilename == null || adsConfigFilename.isEmpty()) {
            throw new ApplicationException("Device Configuration file is not specified");
        }
        //return new AdsConfigurationProperties(appProperties.getDeviceConfigFileName());
        return new AdsConfigurationProperties( new File(appProperties.getDeviceConfigFileName()));
    }

    @Override
    public BdfProvider getDevice() throws ApplicationException {
        AdsConfiguration adsConfig = getDeviceConfig();
        Ads device;
        String deviceClassName = appProperties.getDeviceClassName();
        if(deviceClassName == null || deviceClassName.isEmpty()) {
            throw new ApplicationException("Device Implementing Class is not specified");
        }
        try {
            Class deviceClass = Class.forName(deviceClassName);
            device = (Ads)deviceClass.newInstance();
        } catch (ClassNotFoundException e) {
            log.error(e);
            throw new ApplicationException("Device Implementing Class is not found ");
        }
        catch (InstantiationException e) {
            log.error(e);
            throw new ApplicationException("Device can not be Instantiated");
        } catch (IllegalAccessException e) {
            log.error(e);
            throw new ApplicationException("Device can not be Instantiated");
        }
        device.setAdsConfigurator(adsConfig);
        return device;
    }

    @Override
    public RemConfigurator getRemConfigurator() {
        return new RemConfigurator(appProperties.getEogRemFrequency(),
                appProperties.getAccelerometerRemFrequency());
    }

    @Override
    public String[] detDeviceSignalsLabels() {
        return appProperties.getDeviceChannelsLabels();
    }
}
