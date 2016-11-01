package properties;

import dreamrec.ApplicationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Properties;

/**
 * Created by gala on 30/10/16.
 */
public class JavaPropertiesWrapper {
    private static final Log log = LogFactory.getLog(ApplicationProperties.class);

    private File file;
    private String errMsg;
    Properties prop = new Properties();

    public JavaPropertiesWrapper(File file) throws ApplicationException {
        this.file = file;
        errMsg = "Error reading from properties file: "+ file;
        InputStream input = null;
        try {

            input = new FileInputStream(file);
            prop.load(input);
        }
        catch (IOException ex) {
            log.error(ex);
            throw new ApplicationException(errMsg);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }
    }

    public JavaPropertiesWrapper(String file) throws ApplicationException {
       this(new File(file));
    }

    public String getString(String key)   {
        return prop.getProperty(key);
    }

    public Integer getInt(String key)   {
        try {
            return  Integer.getInteger(prop.getProperty(key));
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }

    public boolean getBoolean(String key)   {
        return  Boolean.getBoolean(key);
    }

    public void setProperty(String key, String value) {
        // set the properties value
        prop.setProperty(key, value);
        // save properties to fiel
        save();
    }

    public void save() {
        OutputStream output = null;
        try {

            output = new FileOutputStream(file);
            // save properties to project root folder
            prop.store(output, null);

        }
        catch (IOException ex) {
            log.error(ex);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }
    }
}
