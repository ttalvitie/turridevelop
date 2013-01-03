package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.exceptions.FilesystemException;
import fi.helsinki.cs.turridevelop.exceptions.MalformedFileException;
import fi.helsinki.cs.turridevelop.file.TurrInput;
import fi.helsinki.cs.turridevelop.file.TurrOutput;
import fi.helsinki.cs.turridevelop.logic.Project;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Window where a single project is modified.
 */
public class ProjectWindow {
    /**
     * The frame of the window.
     */
    private JFrame frame;
    
    /**
     * Project being manipulated, null if none. Change through changeProject.
     */
    private Project project;
    
    /**
     * Menu items that should only be enabled when a project is open.
     */
    private ArrayList<JMenuItem> project_menuitems;
    
    /**
     * The list of machines. Valid if project != null.
     */
    private JList machinelist;
    
    /**
     * The machine editing area. Valid if project != null.
     */
    private JPanel machinepanel;
    
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
        
        frame.getContentPane().setLayout(new BoxLayout(
            frame.getContentPane(), BoxLayout.X_AXIS
        ));
        
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
            changeProject(TurrInput.readProjectDirectory(dir));
        } catch(MalformedFileException e) {
            JOptionPane.showMessageDialog(
                frame,
                "Opening project '" + dir + "' failed:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
    }
    
    private void closeProjectClicked() {
        changeProject(null);
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
    
    private void machineSelected() {
        String machinename = (String) machinelist.getSelectedValue(); 
        machinepanel.removeAll();
        if(machinename != null) {
            machinepanel.add(new MachineView(project.getMachine(machinename)));
        }
        machinepanel.revalidate();
    }
    
    /**
     * Set the project of the class to project, update GUI accordingly.
     * 
     * @param newproject The new project.
     */
    private void changeProject(Project newproject) {
        project = newproject;
        
        // Update the menu options.
        for(JMenuItem item : project_menuitems) {
            item.setEnabled(project != null);
        }
        
        // Update the frame layout.
        frame.getContentPane().removeAll();
        if(project != null) {
            // Machine list on the left, the machine panel on the right.
            machinelist = new JList();
            machinelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            machinelist.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    machineSelected();
                }
            });
            
            JScrollPane machinelist_scroll = new JScrollPane(machinelist);
            machinelist_scroll.setMinimumSize(new Dimension(100, 0));
            machinelist_scroll.setPreferredSize(new Dimension(200, 0));
            Border border = BorderFactory.createTitledBorder("Machines");
            machinelist_scroll.setBorder(border);
            
            machinepanel = new JPanel();
            machinepanel.setLayout(new BorderLayout());
            
            JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, machinelist_scroll, machinepanel
            );
            frame.getContentPane().add(split);
            updateMachineList(null);
        }
        frame.pack();
    }
    
    /**
     * Updates machine list to match the list of machines in the current
     * project.
     * 
     * @param selection After the update, the name of the machine that should
     * be selected. If null, the machine with the same name as the previous
     * selected machine will be selected if possible.
     */
    private void updateMachineList(String selection) {
        if(selection == null) {
            selection = (String) machinelist.getSelectedValue();
        }
        
        Set<String> machinenames = project.getMachineNames();
        String[] data = new String[machinenames.size()];
        int index = 0;
        for(String name : machinenames) {
            data[index] = name;
            index++;
        }
        machinelist.setListData(data);
        
        machinelist.setSelectedValue(selection, true);
    }
}
