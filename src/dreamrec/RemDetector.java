package dreamrec;

import comport.ComportFacade;
import gui.MainWindow;
import gui.comport_gui.ComPortModelMock;
import gui.comport_gui.ComportModelInterface;
import gui.comport_gui.ComportUI;
import properties.ApplicationProperties;

import javax.swing.*;
import java.io.File;


/**
 * Created by mac on 19/02/15.
 */
public class RemDetector {
    public static void main(String[] args) {
        try {
            String lookAndFeelClassName = UIManager.getCrossPlatformLookAndFeelClassName();
            // устанавливаем LookAndFeel
            UIManager.setLookAndFeel(lookAndFeelClassName);
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println("Can't use the specified look and feel on this platform.");
        } catch (Exception e) {
            System.out.println("Couldn't get specified look and feel, for some reason.");
        }

        try {
            ApplicationProperties applicationProperties = new ApplicationProperties(new File("application.properties"));
            ServiceLocator serviceLocator = new PropertiesServiceLocator(applicationProperties);
            Controller controller = new Controller(serviceLocator);
            controller.setRemMode(true);


            ComportModelInterface comportModel = new ComportFacade(serviceLocator.getDeviceConfig());
            ComportUI comportUI = new ComportUI(comportModel);

            MainWindow mainWindow = new MainWindow(controller, serviceLocator.getGuiConfig(), comportUI);
            Presenter presenter = new Presenter(mainWindow);
            controller.addListener(presenter);

        } catch (ApplicationException e) {
            showMessage(e.getMessage());
        }

    }

    public static void showMessage(String s) {
        JOptionPane.showMessageDialog(null, s);
    }
}
