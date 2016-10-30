package comport;

import device.general.AdsConfiguration;
import gui.comport_gui.ComportModelInterface;

/**
 * Created by gala on 29/10/16.
 */
public class ComportFacade implements ComportModelInterface {
    private AdsConfiguration adsConfiguration;

    public ComportFacade(AdsConfiguration adsConfiguration) {
        this.adsConfiguration = adsConfiguration;
    }

    @Override
    public int getComPortSpeed() {
        return adsConfiguration.getComPortSpeed();
    }

    @Override
    public void setComPort(String portName) {
        adsConfiguration.setComPortName(portName);
    }

    @Override
    public String getCurrentComport() {
        String comportName = adsConfiguration.getComPortName();
        for(String availableName : getAvailableComports()) {
           if(comportName!= null && availableName.equals(comportName)) {
               return comportName;
           }
        }
        return null;
    }

    @Override
    public String[] getAvailableComports() {
        return ComPort.getportNames();
    }
}
