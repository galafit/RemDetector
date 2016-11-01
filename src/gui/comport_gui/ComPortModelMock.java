package gui.comport_gui;

import device.general.AdsConfiguration;

import java.io.*;
import java.util.Properties;

/**
 * Created by gala on 29/10/16.
 */
public class ComPortModelMock implements ComportModel {

    @Override
    public String[] getAvailableComports() {
        String[] values = {"one", "two", "tree"};
        return values;
    }
}
