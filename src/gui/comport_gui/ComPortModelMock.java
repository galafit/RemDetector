package gui.comport_gui;

/**
 * Created by gala on 29/10/16.
 */
public class ComPortModelMock implements ComportModelInterface {
    int i=0;
    @Override
    public String getCurrentComport() {
        return null;
    }

    @Override
    public String[] getAvailableComports() {
        i++;
        if(i%2 == 0) {
            String [] values = {"раз", "два", "три"};
            return values;
        }

        String [] values = {"one", "two", "three"};
        return values;
    }

    @Override
    public int getComPortSpeed() {
        return 0;
    }

    @Override
    public void setComPort(String portName) {
        System.out.println("com port: "+portName);

    }
}
