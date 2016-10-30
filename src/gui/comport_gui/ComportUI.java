package gui.comport_gui;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by gala on 29/10/16.
 */
public class ComportUI extends JPanel {
    private ComportModelInterface comportModel;
    private JComboBox comPort;
    private String labelText = "ComPort:  ";


    public ComportUI(ComportModelInterface comportModel) {
        this.comportModel = comportModel;
        int hgap = 0;
        int vgap = 0;
        setLayout(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
        if(comportModel.getCurrentComport() != null) {
            JLabel comportName = new JLabel(labelText +comportModel.getCurrentComport());
            add(comportName);
        }
        else {
            add(new Label(labelText));
            comPort = new JComboBox();
            updateComPortNames();
            comPort.addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    updateComPortNames();
                }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

                }

                @Override
                public void popupMenuCanceled(PopupMenuEvent e) {

                }
            });
            comPort.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveComPortName();

                }
            });
            add(comPort);
        }

    }

    private void saveComPortName() {
        String comPortName = (String) comPort.getSelectedItem();
        comportModel.setComPort(comPortName);
    }

    private void updateComPortNames() {
        DefaultComboBoxModel model = new DefaultComboBoxModel(comportModel.getAvailableComports());
        comPort.setModel( model );


    }
}
