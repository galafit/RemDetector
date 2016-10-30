package gui.comport_gui;

/**
 * Created by gala on 29/10/16.
 */
public interface ComportModelInterface {
    public String getCurrentComport() ;
    public String[] getAvailableComports();
    public int getComPortSpeed();
    public void setComPort(String portName);
}
