package dreamrec;

import comport.ComPort;
import gui.MainWindow;
import properties.ApplicationProperties;

import javax.swing.*;


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
            ApplicationProperties applicationProperties = new ApplicationProperties("application.properties");
            ServiceLocator serviceLocator = new PropertiesServiceLocator(applicationProperties);
            Controller controller = new Controller(serviceLocator);
            controller.setRemMode(true);
            MainWindow mainWindow = new MainWindow(controller, serviceLocator.getGuiConfig());
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
