package gui;

import java.io.File;

public interface GuiConfig {
    public File getDirectoryToSave();
    public File getDirectoryToRead();
    public void setDirectoryToSave(String directory);
    public void setDirectoryToRead(String directory);
}
