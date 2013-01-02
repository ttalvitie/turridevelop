package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.exceptions.FilesystemException;
import fi.helsinki.cs.turridevelop.exceptions.MalformedFileException;
import fi.helsinki.cs.turridevelop.file.TurrInput;
import fi.helsinki.cs.turridevelop.file.TurrOutput;
import fi.helsinki.cs.turridevelop.logic.Project;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 * Window where a single project is modified.
 */
public class ProjectWindow {
    /**
     * The frame of the window.
     */
    private JFrame frame;
    
    /**
     * Project being manipulated, null if none.
     */
    private Project project;
    
    /**
     * Menu items that should only be enabled when a project is open.
     */
    private ArrayList<JMenuItem> project_menuitems;
    
    public ProjectWindow() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        // Add menus.
        JMenuBar menubar = new JMenuBar();
        JMenu menu;
        JMenuItem item;
        project_menuitems = new ArrayList<JMenuItem>();
        
        // File-menu.
        menu = new JMenu("File");
        
        item = new JMenuItem("Open project");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openProjectClicked();
            }
        });
        menu.add(item);
        
        item = new JMenuItem("Close project");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeProjectClicked();
            }
        });
        project_menuitems.add(item);
        menu.add(item);
        
        item = new JMenuItem("Save project as");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProjectAsClicked();
            }
        });
        project_menuitems.add(item);
        menu.add(item);
        
        item = new JMenuItem("Quit");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quitButton();
            }
        });
        menu.add(item);
        
        menubar.add(menu);
        
        frame.setJMenuBar(menubar);
        
        // Initially there are no projects open so disable all menu items that
        // require a project to be open.
        for(JMenuItem menuitem : project_menuitems) {
            menuitem.setEnabled(false);
        }
        
        frame.pack();
        frame.setVisible(true);
    }
    
    private void openProjectClicked() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        // A directory was chosen, try to open it.
        File dir = chooser.getSelectedFile();
        try {
            project = TurrInput.readProjectDirectory(dir);
        } catch(MalformedFileException e) {
            JOptionPane.showMessageDialog(
                frame,
                "Opening project '" + dir + "' failed:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Opening succeeded.
        for(JMenuItem menuitem : project_menuitems) {
            menuitem.setEnabled(true);
        }
    }
    
    private void closeProjectClicked() {
        project = null;
        
        for(JMenuItem menuitem : project_menuitems) {
            menuitem.setEnabled(false);
        }
   }
    
    private void saveProjectAsClicked() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        // A directory was chosen, create it if it doesn't exist already.
        File dir = chooser.getSelectedFile();
        if(!dir.isDirectory()) {
            JOptionPane.showMessageDialog(
                frame,
                "Saving project to '" + dir + "' failed:\n" +
                "The path is not a directory.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Try to save.
        try {
            TurrOutput.writeProject(project, dir);
        } catch(FilesystemException e) {
            JOptionPane.showMessageDialog(
                frame,
                "Saving project to '" + dir + "' failed:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void quitButton() {
        System.exit(0);
    }
}
