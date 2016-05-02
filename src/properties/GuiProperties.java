package properties;

import dreamrec.ApplicationException;
import gui.GuiConfig;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

public class GuiProperties implements GuiConfig {
    private static final Log log = LogFactory.getLog(GuiProperties.class);

    private static final String DIRECTORY_TO_READ = "directory_to_read";
    private static final String DIRECTORY_TO_SAVE = "directory_to_save";

    private FileConfiguration config;

    public GuiProperties(File file) throws ApplicationException {
        try {
            config = new PropertiesConfiguration(file);
            config.setAutoSave(true);
        } catch (ConfigurationException e) {
            log.error(e);
            throw new ApplicationException("Error reading from properties file: " + file);
        }
    }

    @Override
    public File getDirectoryToSave() {
        String dir = config.getString(DIRECTORY_TO_SAVE);
        if(dir != null && !dir.isEmpty()) {
            return new File(dir);
        }
        return null;
    }

    @Override
    public File getDirectoryToRead() {
        String dir = config.getString(DIRECTORY_TO_READ);
        if(dir != null && !dir.isEmpty()) {
            return new File(dir);
        }
        return null;
    }

    @Override
    public void setDirectoryToSave(String directory) {
        if (directory != null) {
            config.setProperty(DIRECTORY_TO_SAVE, directory);
        }
    }

    @Override
    public void setDirectoryToRead(String directory) {
        if (directory != null) {
            config.setProperty(DIRECTORY_TO_READ, directory);
        }
    }

}
