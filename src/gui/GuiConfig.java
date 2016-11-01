package gui;

import java.io.File;

public interface GuiConfig {
    public String getDefaultDirectoryToSave();
    public String getDefaultDirectoryToRead();
    public void setDefaultDirectoryToSave(String directory);
    public void setDefaultDirectoryToRead(String directory);
}
