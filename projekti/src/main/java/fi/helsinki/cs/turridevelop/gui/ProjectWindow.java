package fi.helsinki.cs.turridevelop.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
    JFrame frame;
    
    public ProjectWindow() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        // Add menus.
        JMenuBar menubar = new JMenuBar();
        JMenu menu;
        JMenuItem item;
        
        // File-menu.
        menu = new JMenu("File");
        
        item = new JMenuItem("Open project");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openProjectButton();
            }
        });
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
        
        frame.pack();
        frame.setVisible(true);
    }
    
    private void openProjectButton() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            tryOpenProject(chooser.getSelectedFile());
        }
    }
    
    /**
     * Try to open project or show error.
     * 
     * @param dir The project directory to open.
     */
    private void tryOpenProject(File dir) {
        JOptionPane.showMessageDialog(
            frame,
            "Opening project not implemented yet. Requested: " + dir
        );
    }
    
    private void quitButton() {
        System.exit(0);
    }
}
