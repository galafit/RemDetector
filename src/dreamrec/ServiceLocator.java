package dreamrec;

import bdf.BdfProvider;
import device.general.AdsConfiguration;
import gui.GuiConfig;

public interface ServiceLocator {

    public GuiConfig getGuiConfig() throws ApplicationException;
    public AdsConfiguration getDeviceConfig() throws ApplicationException;
    public BdfProvider getDevice() throws ApplicationException;
    public RemConfigurator getRemConfigurator();
    public String[] detDeviceSignalsLabels();
}
