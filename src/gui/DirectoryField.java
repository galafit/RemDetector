package gui;

import com.sun.istack.internal.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Created by gala on 27/04/16.
 */
public class DirectoryField extends JComboBox{
    JFileChooser fileChooser = new JFileChooser();

    public DirectoryField() { this(null); }

    public DirectoryField(@Nullable File root) {
        String rootString;
        if(root == null) {
            root = new File(System.getProperty("user.home"));
            rootString = System.getProperty("user.home");
        }
        else if(root.isFile()) {
            rootString = root.getParent();
        }
       else {
            rootString = root.getPath();
        }
        addItem(rootString);
        setSelectedItem(rootString);
        fileChooser = new JFileChooser(root);
        fileChooser.setDialogTitle("Specify a directory to save");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Directories";
            }
        });
        addItem(root);
        for (Component component : getComponents()) {
            // cancel the popup menu opening from MouseClicks
            for (MouseListener listener : component.getMouseListeners()) {
                 component.removeMouseListener(listener);

            }
            // instead open JFileChooser
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    int fileChooserState = fileChooser.showOpenDialog(DirectoryField.this);
                    if (fileChooserState == JFileChooser.APPROVE_OPTION) {
                        String dir = fileChooser.getSelectedFile().getAbsolutePath();
                        //parent.setDirectoryToSave(dir);
                        DirectoryField.this.removeAllItems();
                        DirectoryField.this.addItem(fileChooser.getSelectedFile());
                    }
                }
            });
        }

        // disable the popup menu opening from Key-Event Alt-Down
        // which opens the popup menu even if remove all the key listeners and clear the action map
        setFocusable(false);
    }

    public String getDirectory() {
        return getSelectedItem().toString();

    }

    public void setLength(int length) {
        char[] prototype = new char[length];
        for(int i = 0; i < length; i++) {
            prototype[i] = 255;
        }
        setPrototypeDisplayValue(new String(prototype));
    }

}
